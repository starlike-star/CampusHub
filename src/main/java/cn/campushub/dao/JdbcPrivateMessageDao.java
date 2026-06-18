package cn.campushub.dao;

import cn.campushub.model.PrivateMessage;
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

/**
 * 使用 JDBC 实现私信消息数据的查询与持久化操作。
 */
public class JdbcPrivateMessageDao implements PrivateMessageDao {
    /**
     * 创建`JdbcPrivateMessage`。
     *
     * @param conversationId 会话编号
     * @param senderId `sender`编号
     * @param receiverId `receiver`编号
     * @param content 正文内容
     * @return 新建数据的编号
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public long create(
            long conversationId,
            long senderId,
            long receiverId,
            String content
    ) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!conversationMatches(
                        connection,
                        conversationId,
                        senderId,
                        receiverId
                )) {
                    throw new SQLException("私信会话参与者不匹配");
                }
                long messageId = insertMessage(
                        connection,
                        conversationId,
                        senderId,
                        receiverId,
                        content
                );
                updateConversation(connection, conversationId, content);
                connection.commit();
                return messageId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 根据会话查询`JdbcPrivateMessage`。
     *
     * @param conversationId 会话编号
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<PrivateMessage> findByConversation(
            long conversationId,
            long userId
    ) throws SQLException {
        String sql = """
                SELECT m.id, m.conversation_id, m.sender_id, m.receiver_id,
                       m.content, m.is_read, m.created_at
                FROM private_messages m
                JOIN private_conversations c ON c.id = m.conversation_id
                WHERE m.conversation_id = ?
                  AND (c.user_a_id = ? OR c.user_b_id = ?)
                ORDER BY m.created_at ASC, m.id ASC
                """;
        List<PrivateMessage> messages = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, conversationId);
            statement.setLong(2, userId);
            statement.setLong(3, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    messages.add(mapMessage(resultSet));
                }
            }
        }
        return messages;
    }

    /**
     * 标记会话已读状态。
     *
     * @param conversationId 会话编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public int markConversationRead(long conversationId, long userId)
            throws SQLException {
        String sql = """
                UPDATE private_messages m
                JOIN private_conversations c ON c.id = m.conversation_id
                SET m.is_read = 1
                WHERE m.conversation_id = ?
                  AND m.receiver_id = ?
                  AND m.is_read = 0
                  AND (c.user_a_id = ? OR c.user_b_id = ?)
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, conversationId);
            statement.setLong(2, userId);
            statement.setLong(3, userId);
            statement.setLong(4, userId);
            return statement.executeUpdate();
        }
    }

    /**
     * 统计未读。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public int countUnread(long userId) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM private_messages
                WHERE receiver_id = ? AND is_read = 0
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    /**
     * 根据输入计算并返回 `conversationMatches` 的处理结果。
     *
     * @param connection 数据库连接
     * @param conversationId 会话编号
     * @param senderId `sender`编号
     * @param receiverId `receiver`编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean conversationMatches(
            Connection connection,
            long conversationId,
            long senderId,
            long receiverId
    ) throws SQLException {
        String sql = """
                SELECT 1
                FROM private_conversations
                WHERE id = ?
                  AND user_a_id = ?
                  AND user_b_id = ?
                FOR UPDATE
                """;
        long userAId = Math.min(senderId, receiverId);
        long userBId = Math.max(senderId, receiverId);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, conversationId);
            statement.setLong(2, userAId);
            statement.setLong(3, userBId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 新增消息。
     *
     * @param connection 数据库连接
     * @param conversationId 会话编号
     * @param senderId `sender`编号
     * @param receiverId `receiver`编号
     * @param content 正文内容
     * @return 新建数据的编号
     * @throws SQLException 数据库访问失败时抛出
     */
    private long insertMessage(
            Connection connection,
            long conversationId,
            long senderId,
            long receiverId,
            String content
    ) throws SQLException {
        String sql = """
                INSERT INTO private_messages
                    (conversation_id, sender_id, receiver_id, content, is_read)
                VALUES (?, ?, ?, ?, 0)
                """;
        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setLong(1, conversationId);
            statement.setLong(2, senderId);
            statement.setLong(3, receiverId);
            statement.setString(4, content);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("发送私信后未获得主键");
    }

    /**
     * 更新会话。
     *
     * @param connection 数据库连接
     * @param conversationId 会话编号
     * @param content 正文内容
     * @throws SQLException 数据库访问失败时抛出
     */
    private void updateConversation(
            Connection connection,
            long conversationId,
            String content
    ) throws SQLException {
        String sql = """
                UPDATE private_conversations
                SET last_message = ?, last_message_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, summarize(content));
            statement.setLong(2, conversationId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("私信会话不存在");
            }
        }
    }

    /**
     * 根据输入计算并返回 `summarize` 的处理结果。
     *
     * @param content 正文内容
     * @return 方法处理结果
     */
    private String summarize(String content) {
        String normalized = content.replaceAll("\\s+", " ");
        int codePoints = normalized.codePointCount(0, normalized.length());
        if (codePoints <= 500) {
            return normalized;
        }
        int end = normalized.offsetByCodePoints(0, 500);
        return normalized.substring(0, end);
    }

    /**
     * 将数据库结果映射为消息。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private PrivateMessage mapMessage(ResultSet resultSet) throws SQLException {
        return new PrivateMessage(
                resultSet.getLong("id"),
                resultSet.getLong("conversation_id"),
                resultSet.getLong("sender_id"),
                resultSet.getLong("receiver_id"),
                resultSet.getString("content"),
                resultSet.getInt("is_read") == 1,
                toLocalDateTime(resultSet.getTimestamp("created_at"))
        );
    }

    /**
     * 转换为`LocalDateTime`。
     *
     * @param timestamp 数据库时间戳
     * @return 方法处理结果
     */
    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
