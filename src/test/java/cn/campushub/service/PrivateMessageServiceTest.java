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

class PrivateMessageServiceTest {
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

        @Override
        public long getOrCreate(long userAId, long userBId) {
            this.userAId = userAId;
            this.userBId = userBId;
            return 42L;
        }

        @Override
        public Optional<PrivateConversation> findByIdForUser(
                long conversationId,
                long userId
        ) {
            return conversation;
        }

        @Override
        public List<PrivateConversation> findByUser(long userId) {
            return conversation.stream().toList();
        }

        @Override
        public boolean isActiveUser(long userId) {
            return activeUser;
        }
    }

    private static class FakeMessageDao implements PrivateMessageDao {
        private int createdCount;
        private long conversationId;
        private String content;

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

        @Override
        public List<PrivateMessage> findByConversation(
                long conversationId,
                long userId
        ) {
            return List.of();
        }

        @Override
        public int markConversationRead(long conversationId, long userId) {
            return 0;
        }

        @Override
        public int countUnread(long userId) {
            return 0;
        }
    }
}
