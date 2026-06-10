package cn.campushub.service;

import cn.campushub.dao.JdbcMessageDao;
import cn.campushub.dao.MessageDao;
import cn.campushub.model.Message;
import cn.campushub.model.NotificationTarget;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MessageService {
    private static final Set<String> FILTER_TYPES =
            Set.of("comment", "like", "favorite", "claim", "activity", "system");
    private static final Set<String> MESSAGE_TYPES =
            Set.of("comment", "like", "favorite", "claim", "activity", "system");

    private final MessageDao messageDao;

    public MessageService() {
        this(new JdbcMessageDao());
    }

    MessageService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public String normalizeTab(String tab) {
        return tab != null && FILTER_TYPES.contains(tab) ? tab : "all";
    }

    public String normalizeOptionalType(String type) {
        type = ValidationUtils.trimToNull(type);
        if (type == null) {
            return null;
        }
        if (!FILTER_TYPES.contains(type)) {
            throw new IllegalArgumentException("消息类型无效");
        }
        return type;
    }

    public List<Message> listMessages(long userId, String tab) throws SQLException {
        String normalized = normalizeTab(tab);
        return messageDao.findByUser(
                userId,
                "all".equals(normalized) ? null : normalized
        );
    }

    public int countMessages(long userId) throws SQLException {
        return messageDao.countByUser(userId);
    }

    public int countUnread(long userId) throws SQLException {
        return messageDao.countUnread(userId);
    }

    public boolean markRead(long userId, long messageId) throws SQLException {
        return messageId > 0 && messageDao.markRead(userId, messageId);
    }

    public int markAllRead(long userId, String type) throws SQLException {
        return messageDao.markAllRead(userId, normalizeOptionalType(type));
    }

    public void createMessage(
            long userId,
            String title,
            String content,
            String type
    ) throws SQLException {
        title = ValidationUtils.trimToNull(title);
        content = ValidationUtils.trimToNull(content);
        if (userId <= 0 || title == null || content == null
                || title.length() > 150 || !MESSAGE_TYPES.contains(type)) {
            throw new IllegalArgumentException("消息参数无效");
        }
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        messageDao.create(message);
    }

    public void notifyPostComment(
            long postId,
            long actorId,
            String actorNickname,
            String commentContent
    ) throws SQLException {
        notifyTarget(
                messageDao.findPostTarget(postId),
                actorId,
                "有人评论了你的帖子",
                safeNickname(actorNickname) + " 评论了你的帖子《%s》："
                        + summarize(commentContent),
                "comment"
        );
    }

    public void notifyPostLike(long postId, long actorId, String actorNickname)
            throws SQLException {
        notifyTarget(
                messageDao.findPostTarget(postId),
                actorId,
                "有人点赞了你的帖子",
                safeNickname(actorNickname) + " 点赞了你的帖子《%s》。",
                "like"
        );
    }

    public void notifyCommentLike(
            long commentId,
            long actorId,
            String actorNickname
    ) throws SQLException {
        notifyTarget(
                messageDao.findCommentTarget(commentId),
                actorId,
                "有人点赞了你的评论",
                safeNickname(actorNickname) + " 点赞了你在《%s》下的评论。",
                "like"
        );
    }

    public void notifyPostFavorite(
            long postId,
            long actorId,
            String actorNickname
    ) throws SQLException {
        notifyTarget(
                messageDao.findPostTarget(postId),
                actorId,
                "有人收藏了你的帖子",
                safeNickname(actorNickname) + " 收藏了你的帖子《%s》。",
                "favorite"
        );
    }

    public void notifyGoodsFavorite(
            long goodsId,
            long actorId,
            String actorNickname
    ) throws SQLException {
        notifyTarget(
                messageDao.findGoodsTarget(goodsId),
                actorId,
                "有人收藏了你的商品",
                safeNickname(actorNickname) + " 收藏了你的商品《%s》。",
                "favorite"
        );
    }

    private void notifyTarget(
            Optional<NotificationTarget> target,
            long actorId,
            String title,
            String contentTemplate,
            String type
    ) throws SQLException {
        if (target.isEmpty() || target.get().ownerId() == actorId) {
            return;
        }
        NotificationTarget value = target.get();
        createMessage(
                value.ownerId(),
                title,
                contentTemplate.formatted(value.title()),
                type
        );
    }

    private String safeNickname(String nickname) {
        nickname = ValidationUtils.trimToNull(nickname);
        return nickname == null ? "一位用户" : nickname;
    }

    private String summarize(String content) {
        content = ValidationUtils.trimToNull(content);
        if (content == null) {
            return "";
        }
        String normalized = content.replaceAll("\\s+", " ");
        return normalized.length() <= 80
                ? normalized
                : normalized.substring(0, 80) + "...";
    }
}
