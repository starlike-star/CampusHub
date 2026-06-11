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
    ClaimCreateResult create(
            long lostFoundId,
            long userId,
            String message,
            String contact
    ) throws SQLException;

    List<ClaimRequest> findByLostFound(long lostFoundId, long ownerId)
            throws SQLException;

    Optional<ClaimHandleResult> handle(
            long claimId,
            long ownerId,
            String action
    ) throws SQLException;
}
