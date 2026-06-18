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

/**
 * 编排认领申请业务规则、参数校验与数据访问操作。
 */
public class ClaimRequestService {
    private static final Set<String> ACTIONS = Set.of("approve", "reject");
    private final ClaimRequestDao claimRequestDao;

    /**
     * 初始化`ClaimRequest`对象及其运行所需依赖。
     */
    public ClaimRequestService() {
        this(new JdbcClaimRequestDao());
    }

    ClaimRequestService(ClaimRequestDao claimRequestDao) {
        this.claimRequestDao = claimRequestDao;
    }

    /**
     * 创建`ClaimRequest`。
     *
     * @param lostFoundId `lostFound`编号
     * @param userId 用户编号
     * @param message 消息数据
     * @param contact 参数 `contact`
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 查询`ClaimRequest`。
     *
     * @param lostFoundId `lostFound`编号
     * @param ownerId `owner`编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<ClaimRequest> list(long lostFoundId, long ownerId)
            throws SQLException {
        return claimRequestDao.findByLostFound(lostFoundId, ownerId);
    }

    /**
     * 处理`ClaimRequest`。
     *
     * @param claimId 认领编号
     * @param ownerId `owner`编号
     * @param action 参数 `action`
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
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
