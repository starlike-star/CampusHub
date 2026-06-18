package cn.campushub.service;

import cn.campushub.dao.ClaimRequestDao;
import cn.campushub.model.ClaimCreateResult;
import cn.campushub.model.ClaimHandleResult;
import cn.campushub.model.ClaimRequest;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 认领申请相关逻辑的正常路径、边界条件和失败场景。
 */
class ClaimRequestServiceTest {
    /**
     * 验证 `duplicatePendingClaimReturnsFailure` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void duplicatePendingClaimReturnsFailure() throws SQLException {
        FakeClaimRequestDao dao = new FakeClaimRequestDao();
        dao.createResult = new ClaimCreateResult(false, 3L, "校园卡");
        ClaimRequestService service = new ClaimRequestService(dao);

        ServiceResult<ClaimCreateResult> result =
                service.create(8L, 7L, "这是我的卡", "13800000000");

        assertFalse(result.success());
    }

    /**
     * 验证 `validClaimReturnsSuccess` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void validClaimReturnsSuccess() throws SQLException {
        FakeClaimRequestDao dao = new FakeClaimRequestDao();
        dao.createResult = new ClaimCreateResult(true, 3L, "校园卡");
        ClaimRequestService service = new ClaimRequestService(dao);

        ServiceResult<ClaimCreateResult> result =
                service.create(8L, 7L, "这是我的卡", "13800000000");

        assertTrue(result.success());
    }

    /**
     * 验证 `handleRejectsUnknownAction` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void handleRejectsUnknownAction() throws SQLException {
        ClaimRequestService service =
                new ClaimRequestService(new FakeClaimRequestDao());

        assertFalse(service.handle(1L, 3L, "unknown").success());
        assertFalse(service.handle(1L, 3L, null).success());
    }

    private static class FakeClaimRequestDao implements ClaimRequestDao {
        private ClaimCreateResult createResult;

        /**
         * 创建`FakeClaimRequest`。
         *
         * @param lostFoundId `lostFound`编号
         * @param userId 用户编号
         * @param message 消息数据
         * @param contact 参数 `contact`
         * @return 方法处理结果
         */
        @Override
        public ClaimCreateResult create(
                long lostFoundId,
                long userId,
                String message,
                String contact
        ) {
            return createResult;
        }

        /**
         * 根据`LostFound`查询`FakeClaimRequest`。
         *
         * @param lostFoundId `lostFound`编号
         * @param ownerId `owner`编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<ClaimRequest> findByLostFound(
                long lostFoundId,
                long ownerId
        ) {
            return List.of();
        }

        /**
         * 处理`FakeClaimRequest`。
         *
         * @param claimId 认领编号
         * @param ownerId `owner`编号
         * @param action 参数 `action`
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<ClaimHandleResult> handle(
                long claimId,
                long ownerId,
                String action
        ) {
            return Optional.of(new ClaimHandleResult(7L, "校园卡", true));
        }
    }
}
