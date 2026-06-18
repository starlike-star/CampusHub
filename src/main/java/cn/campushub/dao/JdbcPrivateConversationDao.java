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

    /**
     * 获取`OrCreate`。
     *
     * @param userAId `userA`编号
     * @param userBId `userB`编号
     * @return `OrCreate`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 根据`IdForUser`查询`JdbcPrivateConversation`。
     *
     * @param conversationId 会话编号
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 根据用户查询`JdbcPrivateConversation`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 判断是否`ActiveUser`。
     *
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 查询`PairId`。
     *
     * @param userAId `userA`编号
     * @param userBId `userB`编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 设置`UserParameters`。
     *
     * @param statement 预编译 SQL 语句
     * @param userId 用户编号
     * @throws SQLException 数据库访问失败时抛出
     */
    private void setUserParameters(PreparedStatement statement, long userId)
            throws SQLException {
        statement.setLong(1, userId);
        statement.setLong(2, userId);
        statement.setLong(3, userId);
        statement.setLong(4, userId);
    }

    /**
     * 将数据库结果映射为会话。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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
