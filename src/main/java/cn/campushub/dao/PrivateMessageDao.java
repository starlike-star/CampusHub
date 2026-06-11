package cn.campushub.dao;

import cn.campushub.model.PrivateMessage;

import java.sql.SQLException;
import java.util.List;

/**
 * 定义私信消息数据访问能力及业务层依赖的数据契约。
 */
public interface PrivateMessageDao {
    long create(
            long conversationId,
            long senderId,
            long receiverId,
            String content
    ) throws SQLException;

    List<PrivateMessage> findByConversation(long conversationId, long userId)
            throws SQLException;

    int markConversationRead(long conversationId, long userId)
            throws SQLException;

    int countUnread(long userId) throws SQLException;
}
