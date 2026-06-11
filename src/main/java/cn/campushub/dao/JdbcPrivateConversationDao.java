package cn.campushub.dao;

import cn.campushub.model.PrivateConversation;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 使用 JDBC 实现私信会话数据的查询与持久化操作。
 */
public class JdbcPrivateConversationDao implements PrivateConversationDao {
    private static final String SELECT_CONVERSATION = """
            SELECT c.id, c.user_a_id, c.user_b_id, c.last_message,
                   c.last_message_at, c.created_at,
                   u.id AS other_user_id, u.nickname AS other_nickname,
                   u.avatar AS other_avatar,
                   (SELECT COUNT(*)
                    FROM private_messages unread
                    WHERE unread.conversation_id = c.id
                      AND unread.receiver_id = ?
                      AND unread.is_read = 0) AS unread_count
            FROM private_conversations c
            JOIN users u
              ON u.id = CASE
                    WHEN c.user_a_id = ? THEN c.user_b_id
                    ELSE c.user_a_id
                 END
            WHERE (c.user_a_id = ? OR c.user_b_id = ?)
            """;

    @Override
    public long getOrCreate(long userAId, long userBId) throws SQLException {
        String sql = """
                INSERT INTO private_conversations (user_a_id, user_b_id)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            statement.setLong(1, userAId);
            statement.setLong(2, userBId);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        return findPairId(userAId, userBId);
    }

    @Override
    public Optional<PrivateConversation> findByIdForUser(
            long conversationId,
            long userId
    ) throws SQLException {
        String sql = SELECT_CONVERSATION + " AND c.id = ? LIMIT 1";
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setUserParameters(statement, userId);
            statement.setLong(5, conversationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapConversation(resultSet))
                        : Optional.empty();
            }
        }
    }

    @Override
    public List<PrivateConversation> findByUser(long userId) throws SQLException {
        String sql = SELECT_CONVERSATION + """
                ORDER BY COALESCE(c.last_message_at, c.created_at) DESC, c.id DESC
                """;
        List<PrivateConversation> conversations = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setUserParameters(statement, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    conversations.add(mapConversation(resultSet));
                }
            }
        }
        return conversations;
    }

    @Override
    public boolean isActiveUser(long userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE id = ? AND status = 1 LIMIT 1";
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private long findPairId(long userAId, long userBId) throws SQLException {
        String sql = """
                SELECT id
                FROM private_conversations
                WHERE user_a_id = ? AND user_b_id = ?
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userAId);
            statement.setLong(2, userBId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                }
            }
        }
        throw new SQLException("创建私信会话后未获得主键");
    }

    private void setUserParameters(PreparedStatement statement, long userId)
            throws SQLException {
        statement.setLong(1, userId);
        statement.setLong(2, userId);
        statement.setLong(3, userId);
        statement.setLong(4, userId);
    }

    private PrivateConversation mapConversation(ResultSet resultSet)
            throws SQLException {
        return new PrivateConversation(
                resultSet.getLong("id"),
                resultSet.getLong("user_a_id"),
                resultSet.getLong("user_b_id"),
                resultSet.getLong("other_user_id"),
                resultSet.getString("other_nickname"),
                resultSet.getString("other_avatar"),
                resultSet.getString("last_message"),
                toLocalDateTime(resultSet.getTimestamp("last_message_at")),
                toLocalDateTime(resultSet.getTimestamp("created_at")),
                resultSet.getInt("unread_count")
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
