package cn.campushub.service;

import cn.campushub.dao.JdbcPrivateConversationDao;
import cn.campushub.dao.JdbcPrivateMessageDao;
import cn.campushub.dao.PrivateConversationDao;
import cn.campushub.dao.PrivateMessageDao;
import cn.campushub.model.PrivateConversation;
import cn.campushub.model.PrivateMessage;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 编排私信消息业务规则、参数校验与数据访问操作。
 */
public class PrivateMessageService {
    private final PrivateConversationDao conversationDao;
    private final PrivateMessageDao messageDao;

    /**
     * 初始化`PrivateMessage`对象及其运行所需依赖。
     */
    public PrivateMessageService() {
        this(new JdbcPrivateConversationDao(), new JdbcPrivateMessageDao());
    }

    PrivateMessageService(
            PrivateConversationDao conversationDao,
            PrivateMessageDao messageDao
    ) {
        this.conversationDao = conversationDao;
        this.messageDao = messageDao;
    }

    /**
     * 获取`OrCreateConversation`。
     *
     * @param currentUserId 当前用户编号
     * @param receiverId `receiver`编号
     * @return `OrCreateConversation`
     * @throws SQLException 数据库访问失败时抛出
     */
    public long getOrCreateConversation(long currentUserId, long receiverId)
            throws SQLException {
        validateUsers(currentUserId, receiverId);
        long userAId = Math.min(currentUserId, receiverId);
        long userBId = Math.max(currentUserId, receiverId);
        return conversationDao.getOrCreate(userAId, userBId);
    }

    /**
     * 查询`Conversations`。
     *
     * @param currentUserId 当前用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<PrivateConversation> listConversations(long currentUserId)
            throws SQLException {
        return conversationDao.findByUser(currentUserId);
    }

    /**
     * 查询会话。
     *
     * @param conversationId 会话编号
     * @param currentUserId 当前用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public Optional<PrivateConversation> findConversation(
            long conversationId,
            long currentUserId
    ) throws SQLException {
        if (conversationId <= 0 || currentUserId <= 0) {
            return Optional.empty();
        }
        return conversationDao.findByIdForUser(conversationId, currentUserId);
    }

    /**
     * 查询`Messages`。
     *
     * @param conversationId 会话编号
     * @param currentUserId 当前用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<PrivateMessage> listMessages(
            long conversationId,
            long currentUserId
    ) throws SQLException {
        if (findConversation(conversationId, currentUserId).isEmpty()) {
            throw new SecurityException("无权访问该私信会话");
        }
        return messageDao.findByConversation(conversationId, currentUserId);
    }

    /**
     * 发送消息。
     *
     * @param senderId `sender`编号
     * @param receiverId `receiver`编号
     * @param content 正文内容
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<Long> sendMessage(
            long senderId,
            long receiverId,
            String content
    ) throws SQLException {
        content = ValidationUtils.trimToNull(content);
        if (content == null) {
            return ServiceResult.failure("私信内容不能为空");
        }
        if (content.codePointCount(0, content.length()) > 1000) {
            return ServiceResult.failure("私信内容不能超过 1000 字");
        }
        try {
            long conversationId = getOrCreateConversation(senderId, receiverId);
            messageDao.create(conversationId, senderId, receiverId, content);
            return ServiceResult.success("发送成功", conversationId);
        } catch (IllegalArgumentException exception) {
            return ServiceResult.failure(exception.getMessage());
        }
    }

    /**
     * 标记会话已读状态。
     *
     * @param conversationId 会话编号
     * @param currentUserId 当前用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public int markConversationRead(long conversationId, long currentUserId)
            throws SQLException {
        if (findConversation(conversationId, currentUserId).isEmpty()) {
            throw new SecurityException("无权访问该私信会话");
        }
        return messageDao.markConversationRead(conversationId, currentUserId);
    }

    /**
     * 统计`UnreadPrivateMessages`。
     *
     * @param currentUserId 当前用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public int countUnreadPrivateMessages(long currentUserId)
            throws SQLException {
        return messageDao.countUnread(currentUserId);
    }

    /**
     * 校验用户列表。
     *
     * @param currentUserId 当前用户编号
     * @param receiverId `receiver`编号
     * @throws SQLException 数据库访问失败时抛出
     */
    private void validateUsers(long currentUserId, long receiverId)
            throws SQLException {
        if (currentUserId <= 0 || receiverId <= 0) {
            throw new IllegalArgumentException("收件人参数无效");
        }
        if (currentUserId == receiverId) {
            throw new IllegalArgumentException("不能给自己发送私信");
        }
        if (!conversationDao.isActiveUser(receiverId)) {
            throw new IllegalArgumentException("收件人不存在或账号不可用");
        }
    }
}
