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

/**
 * 编排站内通知业务规则、参数校验与数据访问操作。
 */
public class MessageService {
    private static final Set<String> FILTER_TYPES =
            Set.of("comment", "like", "favorite", "claim", "activity", "system");
    private static final Set<String> MESSAGE_TYPES =
            Set.of("comment", "like", "favorite", "claim", "activity", "system");

    private final MessageDao messageDao;

    /**
     * 初始化消息对象及其运行所需依赖。
     */
    public MessageService() {
        this(new JdbcMessageDao());
    }

    MessageService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    /**
     * 规范化`Tab`。
     *
     * @param tab 参数 `tab`
     * @return 方法处理结果
     */
    public String normalizeTab(String tab) {
        return tab != null && FILTER_TYPES.contains(tab) ? tab : "all";
    }

    /**
     * 规范化`OptionalType`。
     *
     * @param type 参数 `type`
     * @return 方法处理结果
     */
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

    /**
     * 查询`Messages`。
     *
     * @param userId 用户编号
     * @param tab 参数 `tab`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Message> listMessages(long userId, String tab) throws SQLException {
        String normalized = normalizeTab(tab);
        return messageDao.findByUser(
                userId,
                "all".equals(normalized) ? null : normalized
        );
    }

    /**
     * 统计`Messages`。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public int countMessages(long userId) throws SQLException {
        return messageDao.countByUser(userId);
    }

    /**
     * 统计未读。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public int countUnread(long userId) throws SQLException {
        return messageDao.countUnread(userId);
    }

    /**
     * 标记已读状态。
     *
     * @param userId 用户编号
     * @param messageId 消息编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    public boolean markRead(long userId, long messageId) throws SQLException {
        return messageId > 0 && messageDao.markRead(userId, messageId);
    }

    /**
     * 标记全部数据已读状态。
     *
     * @param userId 用户编号
     * @param type 参数 `type`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public int markAllRead(long userId, String type) throws SQLException {
        return messageDao.markAllRead(userId, normalizeOptionalType(type));
    }

    /**
     * 创建消息。
     *
     * @param userId 用户编号
     * @param title 标题
     * @param content 正文内容
     * @param type 参数 `type`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 发送通知：`AdminsOfReport`。
     *
     * @param targetType 参数 `targetType`
     * @throws SQLException 数据库访问失败时抛出
     */
    public void notifyAdminsOfReport(String targetType) throws SQLException {
        String targetName = switch (targetType) {
            case "post" -> "帖子";
            case "comment" -> "评论";
            case "goods" -> "商品";
            case "lost_found" -> "失物招领";
            default -> "内容";
        };
        for (Long adminId : messageDao.findActiveAdminIds()) {
            createMessage(
                    adminId,
                    "收到新的举报",
                    "有用户举报了【" + targetName + "】，请前往后台举报管理处理。",
                    "system"
            );
        }
    }

    /**
     * 发送通知：`ReportHandled`。
     *
     * @param reporterId `reporter`编号
     * @param ownerId `owner`编号
     * @throws SQLException 数据库访问失败时抛出
     */
    public void notifyReportHandled(long reporterId, Long ownerId)
            throws SQLException {
        createMessage(
                reporterId,
                "你的举报已处理",
                "你提交的举报已由管理员处理，感谢你维护社区环境。",
                "system"
        );
        if (ownerId != null && ownerId > 0 && ownerId != reporterId) {
            createMessage(
                    ownerId,
                    "你的内容已被处理",
                    "你发布的内容因违反平台规范，已由管理员进行处理。如有疑问请联系管理员。",
                    "system"
            );
        }
    }

    /**
     * 发送通知：`ReportRejected`。
     *
     * @param reporterId `reporter`编号
     * @throws SQLException 数据库访问失败时抛出
     */
    public void notifyReportRejected(long reporterId) throws SQLException {
        createMessage(
                reporterId,
                "你的举报已审核",
                "经管理员审核，暂未发现该内容违规，感谢你的反馈。",
                "system"
        );
    }

    /**
     * 发送通知：帖子评论。
     *
     * @param postId 帖子编号
     * @param actorId `actor`编号
     * @param actorNickname 参数 `actorNickname`
     * @param commentContent 参数 `commentContent`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 发送通知：帖子点赞。
     *
     * @param postId 帖子编号
     * @param actorId `actor`编号
     * @param actorNickname 参数 `actorNickname`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 发送通知：评论点赞。
     *
     * @param commentId 评论编号
     * @param actorId `actor`编号
     * @param actorNickname 参数 `actorNickname`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 发送通知：帖子收藏。
     *
     * @param postId 帖子编号
     * @param actorId `actor`编号
     * @param actorNickname 参数 `actorNickname`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 发送通知：商品收藏。
     *
     * @param goodsId 商品编号
     * @param actorId `actor`编号
     * @param actorNickname 参数 `actorNickname`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 发送通知：`TradePaid`。
     *
     * @param buyerId `buyer`编号
     * @param sellerId `seller`编号
     * @param goodsTitle 参数 `goodsTitle`
     * @throws SQLException 数据库访问失败时抛出
     */
    public void notifyTradePaid(
            long buyerId,
            long sellerId,
            String goodsTitle
    ) throws SQLException {
        createMessage(
                buyerId,
                "模拟支付成功",
                "你已成功购买商品《" + goodsTitle + "》，请与卖家确认交付方式。",
                "system"
        );
        createMessage(
                sellerId,
                "商品已售出",
                "你的商品《" + goodsTitle + "》已被用户购买，请及时联系买家完成交付。",
                "system"
        );
    }

    /**
     * 发送通知：`Target`。
     *
     * @param target 参数 `target`
     * @param actorId `actor`编号
     * @param title 标题
     * @param contentTemplate 参数 `contentTemplate`
     * @param type 参数 `type`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 根据输入计算并返回 `safeNickname` 的处理结果。
     *
     * @param nickname 用户昵称
     * @return 方法处理结果
     */
    private String safeNickname(String nickname) {
        nickname = ValidationUtils.trimToNull(nickname);
        return nickname == null ? "一位用户" : nickname;
    }

    /**
     * 根据输入计算并返回 `summarize` 的处理结果。
     *
     * @param content 正文内容
     * @return 方法处理结果
     */
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
