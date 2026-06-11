package cn.campushub.dao;

import cn.campushub.model.Message;
import cn.campushub.model.NotificationTarget;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 使用 JDBC 实现站内通知数据的查询与持久化操作。
 */
public class JdbcMessageDao implements MessageDao {
    @Override
    public void create(Message message) throws SQLException {
        String sql = """
                INSERT INTO messages (user_id, title, content, type, is_read)
                VALUES (?, ?, ?, ?, 0)
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, message.getUserId());
            statement.setString(2, message.getTitle());
            statement.setString(3, message.getContent());
            statement.setString(4, message.getType());
            statement.executeUpdate();
        }
    }

    @Override
    public List<Long> findActiveAdminIds() throws SQLException {
        String sql = """
                SELECT id
                FROM users
                WHERE role = 'admin' AND status = 1
                ORDER BY id
                """;
        List<Long> adminIds = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                adminIds.add(resultSet.getLong("id"));
            }
        }
        return adminIds;
    }

    @Override
    public List<Message> findByUser(long userId, String type) throws SQLException {
        String sql = """
                SELECT id, user_id, title, content, type, is_read, created_at
                FROM messages
                WHERE user_id = ?
                """ + (type == null ? "" : " AND type = ?") + """

                ORDER BY created_at DESC, id DESC
                """;
        List<Message> messages = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            if (type != null) {
                statement.setString(2, type);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    messages.add(mapMessage(resultSet));
                }
            }
        }
        return messages;
    }

    @Override
    public int countByUser(long userId) throws SQLException {
        return count(
                "SELECT COUNT(*) FROM messages WHERE user_id = ?",
                userId
        );
    }

    @Override
    public int countUnread(long userId) throws SQLException {
        return count(
                "SELECT COUNT(*) FROM messages WHERE user_id = ? AND is_read = 0",
                userId
        );
    }

    @Override
    public boolean markRead(long userId, long messageId) throws SQLException {
        String sql = """
                UPDATE messages
                SET is_read = 1
                WHERE id = ? AND user_id = ?
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, messageId);
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public int markAllRead(long userId, String type) throws SQLException {
        String sql = """
                UPDATE messages
                SET is_read = 1
                WHERE user_id = ? AND is_read = 0
                """ + (type == null ? "" : " AND type = ?");
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            if (type != null) {
                statement.setString(2, type);
            }
            return statement.executeUpdate();
        }
    }

    @Override
    public Optional<NotificationTarget> findPostTarget(long postId)
            throws SQLException {
        return findTarget(
                """
                SELECT user_id, title
                FROM posts
                WHERE id = ? AND status = 1
                LIMIT 1
                """,
                postId
        );
    }

    @Override
    public Optional<NotificationTarget> findGoodsTarget(long goodsId)
            throws SQLException {
        return findTarget(
                """
                SELECT user_id, title
                FROM goods
                WHERE id = ? AND status != 'off_shelf'
                LIMIT 1
                """,
                goodsId
        );
    }

    @Override
    public Optional<NotificationTarget> findCommentTarget(long commentId)
            throws SQLException {
        String sql = """
                SELECT c.user_id, p.title
                FROM comments c
                JOIN posts p ON p.id = c.post_id
                WHERE c.id = ? AND c.status = 1 AND p.status = 1
                LIMIT 1
                """;
        return findTarget(sql, commentId);
    }

    private int count(String sql, long userId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    private Optional<NotificationTarget> findTarget(String sql, long targetId)
            throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, targetId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(new NotificationTarget(
                        resultSet.getLong("user_id"),
                        resultSet.getString("title")
                ));
            }
        }
    }

    private Message mapMessage(ResultSet resultSet) throws SQLException {
        Message message = new Message();
        message.setId(resultSet.getLong("id"));
        message.setUserId(resultSet.getLong("user_id"));
        message.setTitle(resultSet.getString("title"));
        message.setContent(resultSet.getString("content"));
        message.setType(resultSet.getString("type"));
        message.setRead(resultSet.getInt("is_read") == 1);
        message.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        return message;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
