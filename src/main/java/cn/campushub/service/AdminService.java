package cn.campushub.service;

import cn.campushub.dao.AdminDao;
import cn.campushub.dao.JdbcAdminDao;
import cn.campushub.model.ReportNotificationTarget;
import cn.campushub.util.PasswordUtils;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 编排后台管理业务规则、参数校验与数据访问操作。
 */
public class AdminService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());
    private static final Set<String> ROLES = Set.of("student", "admin");
    private static final Set<String> GOODS_STATUSES =
            Set.of("on_sale", "reserved", "sold", "off_shelf");
    private static final Set<String> TRADE_METHODS =
            Set.of("offline", "online", "both");
    private static final Set<String> LOST_FOUND_TYPES = Set.of("lost", "found");
    private static final Set<String> LOST_FOUND_STATUSES =
            Set.of("pending", "claiming", "completed", "closed");
    private static final Set<String> ACTIVITY_STATUSES =
            Set.of("signup", "closed", "ongoing", "finished");
    private static final Set<String> NOTICE_TYPES =
            Set.of("teaching", "life", "activity", "system", "urgent");
    private static final Set<String> REPORT_STATUSES =
            Set.of("pending", "handled", "rejected");
    private static final Set<String> REPORT_TARGET_TYPES =
            Set.of("post", "comment", "goods", "lost_found");

    private final AdminDao adminDao;
    private final MessageService messageService;

    public AdminService() {
        this(new JdbcAdminDao(), new MessageService());
    }

    AdminService(AdminDao adminDao) {
        this(adminDao, null);
    }

    AdminService(AdminDao adminDao, MessageService messageService) {
        this.adminDao = adminDao;
        this.messageService = messageService;
    }

    public Map<String, Long> dashboard() throws SQLException {
        return adminDao.dashboard();
    }

    public List<Map<String, Object>> users(
            String keyword,
            String role,
            String status
    ) throws SQLException {
        return adminDao.findUsers(
                keyword(keyword),
                allowed(role, ROLES),
                integer(status, Set.of(0, 1, 2))
        );
    }

    public List<Map<String, Object>> posts(
            String keyword,
            String status,
            String categoryId
    ) throws SQLException {
        return adminDao.findPosts(
                keyword(keyword),
                integer(status, Set.of(0, 1, 2)),
                positiveLongOrNull(categoryId)
        );
    }

    public List<Map<String, Object>> goods(
            String keyword,
            String status,
            String tradeMethod
    ) throws SQLException {
        return adminDao.findGoods(
                keyword(keyword),
                allowed(status, GOODS_STATUSES),
                allowed(tradeMethod, TRADE_METHODS)
        );
    }

    public List<Map<String, Object>> lostFound(
            String keyword,
            String type,
            String status
    ) throws SQLException {
        return adminDao.findLostFound(
                keyword(keyword),
                allowed(type, LOST_FOUND_TYPES),
                allowed(status, LOST_FOUND_STATUSES)
        );
    }

    public List<Map<String, Object>> activities(
            String keyword,
            String status
    ) throws SQLException {
        return adminDao.findActivities(
                keyword(keyword),
                allowed(status, ACTIVITY_STATUSES)
        );
    }

    public List<Map<String, Object>> notices(String type) throws SQLException {
        return adminDao.findNotices(allowed(type, NOTICE_TYPES));
    }

    public List<Map<String, Object>> reports(
            String status,
            String targetType
    ) throws SQLException {
        return adminDao.findReports(
                allowed(status, REPORT_STATUSES),
                allowed(targetType, REPORT_TARGET_TYPES)
        );
    }

    public List<Map<String, Object>> postCategories() throws SQLException {
        return adminDao.findCategories("post");
    }

    public ServiceResult<Void> updateUserStatus(
            long currentAdminId,
            String userIdValue,
            String statusValue
    ) throws SQLException {
        Long userId = positiveLongOrNull(userIdValue);
        Integer status = integer(statusValue, Set.of(0, 1));
        if (userId == null || status == null) {
            return ServiceResult.failure("用户状态参数无效");
        }
        if (userId == currentAdminId && status == 0) {
            return ServiceResult.failure("管理员不能禁用自己的账号");
        }
        return result(adminDao.updateUserStatus(userId, status), "用户状态已更新");
    }

    public ServiceResult<Void> resetPassword(
            String userIdValue,
            String password
    ) throws SQLException {
        Long userId = positiveLongOrNull(userIdValue);
        if (userId == null || !ValidationUtils.isValidPassword(password)) {
            return ServiceResult.failure("密码需为 8-72 位，并同时包含字母和数字");
        }
        return result(
                adminDao.resetUserPassword(userId, PasswordUtils.hash(password)),
                "密码已重置"
        );
    }

    public ServiceResult<Void> updatePostStatus(String idValue, String statusValue)
            throws SQLException {
        Long id = positiveLongOrNull(idValue);
        Integer status = integer(statusValue, Set.of(0, 1, 2));
        if (id == null || status == null) {
            return ServiceResult.failure("帖子状态参数无效");
        }
        return result(adminDao.updatePostStatus(id, status), "帖子状态已更新");
    }

    public ServiceResult<Void> updateGoodsStatus(String idValue, String status)
            throws SQLException {
        Long id = positiveLongOrNull(idValue);
        status = allowed(status, GOODS_STATUSES);
        if (id == null || status == null) {
            return ServiceResult.failure("商品状态参数无效");
        }
        return result(adminDao.updateGoodsStatus(id, status), "商品状态已更新");
    }

    public ServiceResult<Void> updateLostFoundStatus(String idValue, String status)
            throws SQLException {
        Long id = positiveLongOrNull(idValue);
        status = allowed(status, LOST_FOUND_STATUSES);
        if (id == null || status == null) {
            return ServiceResult.failure("失物招领状态参数无效");
        }
        return result(adminDao.updateLostFoundStatus(id, status), "信息状态已更新");
    }

    public ServiceResult<Void> updateActivityStatus(String idValue, String status)
            throws SQLException {
        Long id = positiveLongOrNull(idValue);
        status = allowed(status, ACTIVITY_STATUSES);
        if (id == null || status == null) {
            return ServiceResult.failure("活动状态参数无效");
        }
        return result(adminDao.updateActivityStatus(id, status), "活动状态已更新");
    }

    public ServiceResult<Void> createNotice(
            String title,
            String content,
            String type,
            long adminId
    ) throws SQLException {
        ServiceResult<String[]> values = noticeValues(title, content, type);
        if (!values.success()) {
            return ServiceResult.failure(values.message());
        }
        String[] data = values.data();
        return result(
                adminDao.createNotice(data[0], data[1], data[2], adminId),
                "公告已发布"
        );
    }

    public ServiceResult<Void> updateNotice(
            String idValue,
            String title,
            String content,
            String type
    ) throws SQLException {
        Long id = positiveLongOrNull(idValue);
        ServiceResult<String[]> values = noticeValues(title, content, type);
        if (id == null || !values.success()) {
            return ServiceResult.failure(
                    id == null ? "公告参数无效" : values.message()
            );
        }
        String[] data = values.data();
        return result(
                adminDao.updateNotice(id, data[0], data[1], data[2]),
                "公告已更新"
        );
    }

    public ServiceResult<Void> updateNoticeStatus(String idValue, String value)
            throws SQLException {
        Long id = positiveLongOrNull(idValue);
        Integer status = integer(value, Set.of(0, 1));
        if (id == null || status == null) {
            return ServiceResult.failure("公告状态参数无效");
        }
        return result(adminDao.updateNoticeStatus(id, status), "公告状态已更新");
    }

    public ServiceResult<Void> updateNoticeTop(String idValue, String value)
            throws SQLException {
        Long id = positiveLongOrNull(idValue);
        Integer top = integer(value, Set.of(0, 1));
        if (id == null || top == null) {
            return ServiceResult.failure("公告置顶参数无效");
        }
        return result(adminDao.updateNoticeTop(id, top), "公告置顶状态已更新");
    }

    public ServiceResult<Void> handleReport(
            String idValue,
            long adminId
    ) throws SQLException {
        Long id = positiveLongOrNull(idValue);
        if (id == null) {
            return ServiceResult.failure("举报参数无效");
        }
        if (!adminDao.handleReport(id, adminId)) {
            return ServiceResult.failure("目标不存在或状态已发生变化");
        }
        notifyReportResult(id, true);
        return ServiceResult.success("举报已处理", null);
    }

    public ServiceResult<Void> rejectReport(
            String idValue,
            long adminId
    ) throws SQLException {
        Long id = positiveLongOrNull(idValue);
        if (id == null) {
            return ServiceResult.failure("举报参数无效");
        }
        if (!adminDao.rejectReport(id, adminId)) {
            return ServiceResult.failure("目标不存在或状态已发生变化");
        }
        notifyReportResult(id, false);
        return ServiceResult.success("举报已驳回", null);
    }

    private void notifyReportResult(long reportId, boolean handled) {
        if (messageService == null) {
            return;
        }
        try {
            Optional<ReportNotificationTarget> target =
                    adminDao.findReportNotificationTarget(reportId);
            if (target.isEmpty()) {
                return;
            }
            ReportNotificationTarget value = target.get();
            if (handled) {
                messageService.notifyReportHandled(
                        value.reporterId(),
                        value.ownerId()
                );
            } else {
                messageService.notifyReportRejected(value.reporterId());
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.WARNING, "举报状态已更新，但消息创建失败", exception);
        }
    }

    private ServiceResult<String[]> noticeValues(
            String title,
            String content,
            String type
    ) {
        title = ValidationUtils.trimToNull(title);
        content = ValidationUtils.trimToNull(content);
        type = allowed(type, NOTICE_TYPES);
        if (title == null || title.length() > 150) {
            return ServiceResult.failure("公告标题不能为空且不能超过 150 个字符");
        }
        if (content == null) {
            return ServiceResult.failure("公告内容不能为空");
        }
        if (type == null) {
            return ServiceResult.failure("公告类型无效");
        }
        return ServiceResult.success("验证通过", new String[]{title, content, type});
    }

    private ServiceResult<Void> result(boolean changed, String successMessage) {
        return changed
                ? ServiceResult.success(successMessage, null)
                : ServiceResult.failure("目标不存在或状态已发生变化");
    }

    private String keyword(String value) {
        value = ValidationUtils.trimToNull(value);
        if (value == null) {
            return null;
        }
        return value.length() <= 100 ? value : value.substring(0, 100);
    }

    private String allowed(String value, Set<String> values) {
        return value != null && values.contains(value) ? value : null;
    }

    private Integer integer(String value, Set<Integer> values) {
        try {
            int parsed = Integer.parseInt(value);
            return values.contains(parsed) ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private Long positiveLongOrNull(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
