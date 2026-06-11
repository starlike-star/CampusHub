package cn.campushub.dao;

import cn.campushub.model.PrivateConversation;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PrivateConversationDao {
    long getOrCreate(long userAId, long userBId) throws SQLException;

    Optional<PrivateConversation> findByIdForUser(long conversationId, long userId)
            throws SQLException;

    List<PrivateConversation> findByUser(long userId) throws SQLException;

    boolean isActiveUser(long userId) throws SQLException;
}
