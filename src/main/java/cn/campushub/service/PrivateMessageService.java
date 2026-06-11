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

    public long getOrCreateConversation(long currentUserId, long receiverId)
            throws SQLException {
        validateUsers(currentUserId, receiverId);
        long userAId = Math.min(currentUserId, receiverId);
        long userBId = Math.max(currentUserId, receiverId);
        return conversationDao.getOrCreate(userAId, userBId);
    }

    public List<PrivateConversation> listConversations(long currentUserId)
            throws SQLException {
        return conversationDao.findByUser(currentUserId);
    }

    public Optional<PrivateConversation> findConversation(
            long conversationId,
            long currentUserId
    ) throws SQLException {
        if (conversationId <= 0 || currentUserId <= 0) {
            return Optional.empty();
        }
        return conversationDao.findByIdForUser(conversationId, currentUserId);
    }

    public List<PrivateMessage> listMessages(
            long conversationId,
            long currentUserId
    ) throws SQLException {
        if (findConversation(conversationId, currentUserId).isEmpty()) {
            throw new SecurityException("无权访问该私信会话");
        }
        return messageDao.findByConversation(conversationId, currentUserId);
    }

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

    public int markConversationRead(long conversationId, long currentUserId)
            throws SQLException {
        if (findConversation(conversationId, currentUserId).isEmpty()) {
            throw new SecurityException("无权访问该私信会话");
        }
        return messageDao.markConversationRead(conversationId, currentUserId);
    }

    public int countUnreadPrivateMessages(long currentUserId)
            throws SQLException {
        return messageDao.countUnread(currentUserId);
    }

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
