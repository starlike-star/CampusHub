package cn.campushub.service;

import cn.campushub.dao.ClaimRequestDao;
import cn.campushub.dao.JdbcClaimRequestDao;
import cn.campushub.model.ClaimCreateResult;
import cn.campushub.model.ClaimHandleResult;
import cn.campushub.model.ClaimRequest;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ClaimRequestService {
    private static final Set<String> ACTIONS = Set.of("approve", "reject");
    private final ClaimRequestDao claimRequestDao;

    public ClaimRequestService() {
        this(new JdbcClaimRequestDao());
    }

    ClaimRequestService(ClaimRequestDao claimRequestDao) {
        this.claimRequestDao = claimRequestDao;
    }

    public ServiceResult<ClaimCreateResult> create(
            long lostFoundId,
            long userId,
            String message,
            String contact
    ) throws SQLException {
        message = ValidationUtils.trimToNull(message);
        contact = ValidationUtils.trimToNull(contact);
        if (lostFoundId <= 0) {
            return ServiceResult.failure("失物招领参数无效");
        }
        if (message == null) {
            return ServiceResult.failure("申请说明不能为空");
        }
        if (contact == null || contact.length() > 100) {
            return ServiceResult.failure("联系方式不能为空且不能超过 100 个字符");
        }
        ClaimCreateResult result =
                claimRequestDao.create(lostFoundId, userId, message, contact);
        if (result == null) {
            return ServiceResult.failure("不能申请认领该信息");
        }
        if (!result.created()) {
            return ServiceResult.failure("你已提交过认领申请，请等待发布者处理");
        }
        return ServiceResult.success("认领申请已提交", result);
    }

    public List<ClaimRequest> list(long lostFoundId, long ownerId)
            throws SQLException {
        return claimRequestDao.findByLostFound(lostFoundId, ownerId);
    }

    public ServiceResult<ClaimHandleResult> handle(
            long claimId,
            long ownerId,
            String action
    ) throws SQLException {
        if (claimId <= 0 || action == null || !ACTIONS.contains(action)) {
            return ServiceResult.failure("认领申请参数无效");
        }
        Optional<ClaimHandleResult> result =
                claimRequestDao.handle(claimId, ownerId, action);
        return result
                .map(value -> ServiceResult.success("处理成功", value))
                .orElseGet(() -> ServiceResult.failure(
                        "申请不存在、已处理或无权操作"
                ));
    }
}
