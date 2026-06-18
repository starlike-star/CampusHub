package cn.campushub.service;

import cn.campushub.dao.MessageDao;
import cn.campushub.model.Message;
import cn.campushub.model.NotificationTarget;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 站内通知相关逻辑的正常路径、边界条件和失败场景。
 */
class MessageServiceTest {
    /**
     * 验证 `postLikeCreatesNotificationForOwner` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void postLikeCreatesNotificationForOwner() throws SQLException {
        FakeMessageDao dao = new FakeMessageDao();
        dao.postTarget = Optional.of(new NotificationTarget(8L, "测试帖子"));
        MessageService service = new MessageService(dao);

        service.notifyPostLike(3L, 7L, "小明");

        assertEquals(8L, dao.created.getUserId());
        assertEquals("like", dao.created.getType());
        assertEquals("有人点赞了你的帖子", dao.created.getTitle());
        assertEquals("小明 点赞了你的帖子《测试帖子》。", dao.created.getContent());
    }

    /**
     * 验证 `selfInteractionDoesNotCreateNotification` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void selfInteractionDoesNotCreateNotification() throws SQLException {
        FakeMessageDao dao = new FakeMessageDao();
        dao.postTarget = Optional.of(new NotificationTarget(7L, "自己的帖子"));
        MessageService service = new MessageService(dao);

        service.notifyPostFavorite(3L, 7L, "小明");

        assertNull(dao.created);
    }

    /**
     * 验证 `markAllReadRejectsUnknownType` 场景下的业务行为与预期结果一致。
     */
    @Test
    void markAllReadRejectsUnknownType() {
        MessageService service = new MessageService(new FakeMessageDao());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.markAllRead(7L, "unknown")
        );
    }

    /**
     * 验证 `claimIsAValidMessageFilter` 场景下的业务行为与预期结果一致。
     */
    @Test
    void claimIsAValidMessageFilter() {
        MessageService service = new MessageService(new FakeMessageDao());

        assertEquals("claim", service.normalizeTab("claim"));
        assertEquals("activity", service.normalizeTab("activity"));
    }

    /**
     * 验证 `reportSubmissionNotifiesEveryActiveAdmin` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void reportSubmissionNotifiesEveryActiveAdmin() throws SQLException {
        FakeMessageDao dao = new FakeMessageDao();
        dao.adminIds = List.of(2L, 5L);
        MessageService service = new MessageService(dao);

        service.notifyAdminsOfReport("goods");

        assertEquals(2, dao.createdMessages.size());
        assertEquals("收到新的举报", dao.createdMessages.get(0).getTitle());
        assertEquals("system", dao.createdMessages.get(0).getType());
        assertEquals(
                "有用户举报了【商品】，请前往后台举报管理处理。",
                dao.createdMessages.get(0).getContent()
        );
    }

    /**
     * 验证 `handledReportDoesNotNotifyReporterTwiceWhenTheyOwnTarget` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void handledReportDoesNotNotifyReporterTwiceWhenTheyOwnTarget()
            throws SQLException {
        FakeMessageDao dao = new FakeMessageDao();
        MessageService service = new MessageService(dao);

        service.notifyReportHandled(7L, 7L);

        assertEquals(1, dao.createdMessages.size());
        assertEquals("你的举报已处理", dao.createdMessages.get(0).getTitle());
    }

    /**
     * 验证 `rejectedReportOnlyNotifiesReporter` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void rejectedReportOnlyNotifiesReporter() throws SQLException {
        FakeMessageDao dao = new FakeMessageDao();
        MessageService service = new MessageService(dao);

        service.notifyReportRejected(7L);

        assertEquals(1, dao.createdMessages.size());
        assertEquals("你的举报已审核", dao.createdMessages.get(0).getTitle());
    }

    private static class FakeMessageDao implements MessageDao {
        private Message created;
        private List<Long> adminIds = List.of();
        private final List<Message> createdMessages = new java.util.ArrayList<>();
        private Optional<NotificationTarget> postTarget = Optional.empty();

        /**
         * 创建模拟消息。
         *
         * @param message 消息数据
         */
        @Override
        public void create(Message message) {
            created = message;
            createdMessages.add(message);
        }

        /**
         * 查询`ActiveAdminIds`。
         *
         * @return 符合条件的数据列表
         */
        @Override
        public List<Long> findActiveAdminIds() {
            return adminIds;
        }

        /**
         * 根据用户查询模拟消息。
         *
         * @param userId 用户编号
         * @param type 参数 `type`
         * @return 符合条件的数据列表
         */
        @Override
        public List<Message> findByUser(long userId, String type) {
            return List.of();
        }

        /**
         * 统计`ByUser`。
         *
         * @param userId 用户编号
         * @return 方法处理结果
         */
        @Override
        public int countByUser(long userId) {
            return 0;
        }

        /**
         * 统计未读。
         *
         * @param userId 用户编号
         * @return 方法处理结果
         */
        @Override
        public int countUnread(long userId) {
            return 0;
        }

        /**
         * 标记已读状态。
         *
         * @param userId 用户编号
         * @param messageId 消息编号
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean markRead(long userId, long messageId) {
            return true;
        }

        /**
         * 标记全部数据已读状态。
         *
         * @param userId 用户编号
         * @param type 参数 `type`
         * @return 方法处理结果
         */
        @Override
        public int markAllRead(long userId, String type) {
            return 0;
        }

        /**
         * 查询`PostTarget`。
         *
         * @param postId 帖子编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<NotificationTarget> findPostTarget(long postId) {
            return postTarget;
        }

        /**
         * 查询`GoodsTarget`。
         *
         * @param goodsId 商品编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<NotificationTarget> findGoodsTarget(long goodsId) {
            return Optional.empty();
        }

        /**
         * 查询`CommentTarget`。
         *
         * @param commentId 评论编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<NotificationTarget> findCommentTarget(long commentId) {
            return Optional.empty();
        }
    }
}
