package cn.campushub.service;

import cn.campushub.dao.PrivateConversationDao;
import cn.campushub.dao.PrivateMessageDao;
import cn.campushub.model.PrivateConversation;
import cn.campushub.model.PrivateMessage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 私信消息相关逻辑的正常路径、边界条件和失败场景。
 */
class PrivateMessageServiceTest {
    /**
     * 验证 `getOrCreateStoresSmallerUserIdFirst` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
    @Test
    void getOrCreateStoresSmallerUserIdFirst() throws Exception {
        FakeConversationDao conversations = new FakeConversationDao();
        PrivateMessageService service =
                new PrivateMessageService(conversations, new FakeMessageDao());

        long id = service.getOrCreateConversation(9L, 3L);

        assertEquals(42L, id);
        assertEquals(3L, conversations.userAId);
        assertEquals(9L, conversations.userBId);
    }

    /**
     * 验证 `sendRejectsSelfAndDoesNotCreateMessage` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
    @Test
    void sendRejectsSelfAndDoesNotCreateMessage() throws Exception {
        FakeConversationDao conversations = new FakeConversationDao();
        FakeMessageDao messages = new FakeMessageDao();
        PrivateMessageService service =
                new PrivateMessageService(conversations, messages);

        ServiceResult<Long> result = service.sendMessage(7L, 7L, "hello");

        assertFalse(result.success());
        assertEquals(0, messages.createdCount);
    }

    /**
     * 验证 `sendRejectsInactiveReceiver` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
    @Test
    void sendRejectsInactiveReceiver() throws Exception {
        FakeConversationDao conversations = new FakeConversationDao();
        conversations.activeUser = false;
        FakeMessageDao messages = new FakeMessageDao();
        PrivateMessageService service =
                new PrivateMessageService(conversations, messages);

        ServiceResult<Long> result = service.sendMessage(7L, 8L, "hello");

        assertFalse(result.success());
        assertEquals(0, messages.createdCount);
    }

    /**
     * 验证 `sendTrimsContentAndUsesConversation` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
    @Test
    void sendTrimsContentAndUsesConversation() throws Exception {
        FakeConversationDao conversations = new FakeConversationDao();
        FakeMessageDao messages = new FakeMessageDao();
        PrivateMessageService service =
                new PrivateMessageService(conversations, messages);

        ServiceResult<Long> result = service.sendMessage(7L, 8L, "  hello  ");

        assertTrue(result.success());
        assertEquals(42L, result.data());
        assertEquals(42L, messages.conversationId);
        assertEquals("hello", messages.content);
    }

    /**
     * 验证 `listMessagesRejectsUnrelatedConversation` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
    @Test
    void listMessagesRejectsUnrelatedConversation() throws Exception {
        FakeConversationDao conversations = new FakeConversationDao();
        conversations.conversation = Optional.empty();
        PrivateMessageService service =
                new PrivateMessageService(conversations, new FakeMessageDao());

        boolean rejected = false;
        try {
            service.listMessages(99L, 7L);
        } catch (SecurityException exception) {
            rejected = true;
        }

        assertTrue(rejected);
    }

    private static class FakeConversationDao
            implements PrivateConversationDao {
        private boolean activeUser = true;
        private long userAId;
        private long userBId;
        private Optional<PrivateConversation> conversation = Optional.of(
                new PrivateConversation(
                        42L, 7L, 8L, 8L, "User", null,
                        null, null, null, 0
                )
        );

        /**
         * 获取`OrCreate`。
         *
         * @param userAId `userA`编号
         * @param userBId `userB`编号
         * @return `OrCreate`
         */
        @Override
        public long getOrCreate(long userAId, long userBId) {
            this.userAId = userAId;
            this.userBId = userBId;
            return 42L;
        }

        /**
         * 根据`IdForUser`查询模拟会话。
         *
         * @param conversationId 会话编号
         * @param userId 用户编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<PrivateConversation> findByIdForUser(
                long conversationId,
                long userId
        ) {
            return conversation;
        }

        /**
         * 根据用户查询模拟会话。
         *
         * @param userId 用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<PrivateConversation> findByUser(long userId) {
            return conversation.stream().toList();
        }

        /**
         * 判断是否`ActiveUser`。
         *
         * @param userId 用户编号
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean isActiveUser(long userId) {
            return activeUser;
        }
    }

    private static class FakeMessageDao implements PrivateMessageDao {
        private int createdCount;
        private long conversationId;
        private String content;

        /**
         * 创建模拟消息。
         *
         * @param conversationId 会话编号
         * @param senderId `sender`编号
         * @param receiverId `receiver`编号
         * @param content 正文内容
         * @return 新建数据的编号
         */
        @Override
        public long create(
                long conversationId,
                long senderId,
                long receiverId,
                String content
        ) {
            createdCount++;
            this.conversationId = conversationId;
            this.content = content;
            return 77L;
        }

        /**
         * 根据会话查询模拟消息。
         *
         * @param conversationId 会话编号
         * @param userId 用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<PrivateMessage> findByConversation(
                long conversationId,
                long userId
        ) {
            return List.of();
        }

        /**
         * 标记会话已读状态。
         *
         * @param conversationId 会话编号
         * @param userId 用户编号
         * @return 方法处理结果
         */
        @Override
        public int markConversationRead(long conversationId, long userId) {
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
    }
}
