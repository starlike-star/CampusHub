package cn.campushub.service;

import cn.campushub.dao.JdbcReportDao;
import cn.campushub.dao.ReportDao;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.Set;

public class ReportService {
    private static final Set<String> TARGET_TYPES =
            Set.of("post", "comment", "goods", "lost_found");
    private static final int MIN_REASON_LENGTH = 5;
    private static final int MAX_REASON_LENGTH = 255;

    private final ReportDao reportDao;

    public ReportService() {
        this(new JdbcReportDao());
    }

    ReportService(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public ServiceResult<Void> create(
            long userId,
            String targetIdValue,
            String targetType,
            String reason
    ) throws SQLException {
        Long targetId = parsePositiveLong(targetIdValue);
        reason = ValidationUtils.trimToNull(reason);

        if (targetId == null) {
            return ServiceResult.failure("举报目标参数无效");
        }
        if (!TARGET_TYPES.contains(targetType)) {
            return ServiceResult.failure("举报类型无效");
        }
        if (reason == null || reason.length() < MIN_REASON_LENGTH) {
            return ServiceResult.failure("举报原因至少需要 5 个字符");
        }
        if (reason.length() > MAX_REASON_LENGTH) {
            return ServiceResult.failure("举报原因不能超过 255 个字符");
        }

        Long ownerId = reportDao.findTargetOwnerId(targetType, targetId);
        if (ownerId == null) {
            return ServiceResult.failure("举报目标不存在或已不可见");
        }
        if (ownerId == userId) {
            return ServiceResult.failure("不能举报自己发布的内容");
        }
        if (reportDao.existsPendingReport(userId, targetType, targetId)) {
            return ServiceResult.failure(
                    "你已经举报过该内容，请等待管理员处理"
            );
        }
        if (reportDao.createReport(userId, targetType, targetId, reason) != 1) {
            return ServiceResult.failure("举报提交失败，请稍后重试");
        }
        return ServiceResult.success(
                "举报提交成功，管理员会尽快处理",
                null
        );
    }

    private Long parsePositiveLong(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
