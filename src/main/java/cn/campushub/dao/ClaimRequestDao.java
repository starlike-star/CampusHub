package cn.campushub.dao;

import cn.campushub.model.ClaimCreateResult;
import cn.campushub.model.ClaimHandleResult;
import cn.campushub.model.ClaimRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义认领申请数据访问能力及业务层依赖的数据契约。
 */
public interface ClaimRequestDao {
    /**
     * 创建`ClaimRequest`。
     *
     * @param lostFoundId `lostFound`编号
     * @param userId 用户编号
     * @param message 消息数据
     * @param contact 参数 `contact`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    ClaimCreateResult create(
            long lostFoundId,
            long userId,
            String message,
            String contact
    ) throws SQLException;

    /**
     * 根据`LostFound`查询`ClaimRequest`。
     *
     * @param lostFoundId `lostFound`编号
     * @param ownerId `owner`编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<ClaimRequest> findByLostFound(long lostFoundId, long ownerId)
            throws SQLException;

    /**
     * 处理`ClaimRequest`。
     *
     * @param claimId 认领编号
     * @param ownerId `owner`编号
     * @param action 参数 `action`
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<ClaimHandleResult> handle(
            long claimId,
            long ownerId,
            String action
    ) throws SQLException;
}
