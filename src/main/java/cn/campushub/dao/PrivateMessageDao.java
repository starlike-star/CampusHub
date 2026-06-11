package cn.campushub.dao;

import cn.campushub.model.PrivateMessage;

import java.sql.SQLException;
import java.util.List;

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
