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
    /**
     * 创建`JdbcMessage`。
     *
     * @param message 消息数据
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 查询`ActiveAdminIds`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 根据用户查询`JdbcMessage`。
     *
     * @param userId 用户编号
     * @param type 参数 `type`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 统计`ByUser`。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public int countByUser(long userId) throws SQLException {
        return count(
                "SELECT COUNT(*) FROM messages WHERE user_id = ?",
                userId
        );
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
        return count(
                "SELECT COUNT(*) FROM messages WHERE user_id = ? AND is_read = 0",
                userId
        );
    }

    /**
     * 标记已读状态。
     *
     * @param userId 用户编号
     * @param messageId 消息编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 标记全部数据已读状态。
     *
     * @param userId 用户编号
     * @param type 参数 `type`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 查询`PostTarget`。
     *
     * @param postId 帖子编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 查询`GoodsTarget`。
     *
     * @param goodsId 商品编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 查询`CommentTarget`。
     *
     * @param commentId 评论编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 统计`JdbcMessage`。
     *
     * @param sql 参数 `sql`
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private int count(String sql, long userId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    /**
     * 查询`Target`。
     *
     * @param sql 参数 `sql`
     * @param targetId `target`编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 将数据库结果映射为消息。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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
