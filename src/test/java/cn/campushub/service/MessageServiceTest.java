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

class MessageServiceTest {
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

    @Test
    void selfInteractionDoesNotCreateNotification() throws SQLException {
        FakeMessageDao dao = new FakeMessageDao();
        dao.postTarget = Optional.of(new NotificationTarget(7L, "自己的帖子"));
        MessageService service = new MessageService(dao);

        service.notifyPostFavorite(3L, 7L, "小明");

        assertNull(dao.created);
    }

    @Test
    void markAllReadRejectsUnknownType() {
        MessageService service = new MessageService(new FakeMessageDao());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.markAllRead(7L, "unknown")
        );
    }

    private static class FakeMessageDao implements MessageDao {
        private Message created;
        private Optional<NotificationTarget> postTarget = Optional.empty();

        @Override
        public void create(Message message) {
            created = message;
        }

        @Override
        public List<Message> findByUser(long userId, String type) {
            return List.of();
        }

        @Override
        public int countByUser(long userId) {
            return 0;
        }

        @Override
        public int countUnread(long userId) {
            return 0;
        }

        @Override
        public boolean markRead(long userId, long messageId) {
            return true;
        }

        @Override
        public int markAllRead(long userId, String type) {
            return 0;
        }

        @Override
        public Optional<NotificationTarget> findPostTarget(long postId) {
            return postTarget;
        }

        @Override
        public Optional<NotificationTarget> findGoodsTarget(long goodsId) {
            return Optional.empty();
        }

        @Override
        public Optional<NotificationTarget> findCommentTarget(long commentId) {
            return Optional.empty();
        }
    }
}
