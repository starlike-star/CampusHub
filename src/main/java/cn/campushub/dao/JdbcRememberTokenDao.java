package cn.campushub.dao;

import cn.campushub.model.RememberToken;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 使用 JDBC 实现记住登录令牌数据的查询与持久化操作。
 */
public class JdbcRememberTokenDao implements RememberTokenDao {
    /**
     * 创建令牌。
     *
     * @param userId 用户编号
     * @param selector 参数 `selector`
     * @param tokenHash 参数 `tokenHash`
     * @param expiresAt 参数 `expiresAt`
     * @param userAgent 参数 `userAgent`
     * @param ipAddress 参数 `ipAddress`
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public void createToken(
            long userId,
            String selector,
            String tokenHash,
            LocalDateTime expiresAt,
            String userAgent,
            String ipAddress
    ) throws SQLException {
        String sql = """
                INSERT INTO remember_tokens
                    (user_id, selector, token_hash, expires_at,
                     user_agent, ip_address)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setString(2, selector);
            statement.setString(3, tokenHash);
            statement.setTimestamp(4, Timestamp.valueOf(expiresAt));
            statement.setString(5, userAgent);
            statement.setString(6, ipAddress);
            statement.executeUpdate();
        }
    }

    /**
     * 根据`Selector`查询`JdbcRememberToken`。
     *
     * @param selector 参数 `selector`
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Optional<RememberToken> findBySelector(String selector)
            throws SQLException {
        String sql = """
                SELECT id, user_id, selector, token_hash, expires_at,
                       created_at, last_used_at, user_agent, ip_address
                FROM remember_tokens
                WHERE selector = ?
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, selector);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapToken(resultSet))
                        : Optional.empty();
            }
        }
    }

    /**
     * 更新`LastUsed`。
     *
     * @param selector 参数 `selector`
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public void updateLastUsed(String selector) throws SQLException {
        executeUpdate(
                "UPDATE remember_tokens SET last_used_at = CURRENT_TIMESTAMP "
                        + "WHERE selector = ?",
                selector
        );
    }

    /**
     * 删除`BySelector`。
     *
     * @param selector 参数 `selector`
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public void deleteBySelector(String selector) throws SQLException {
        executeUpdate("DELETE FROM remember_tokens WHERE selector = ?", selector);
    }

    /**
     * 删除`ExpiredTokens`。
     *
     * @return `deleteExpiredTokens`
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public int deleteExpiredTokens() throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM remember_tokens WHERE expires_at <= CURRENT_TIMESTAMP"
             )) {
            return statement.executeUpdate();
        }
    }

    /**
     * 删除`ByUserId`。
     *
     * @param userId 用户编号
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public void deleteByUserId(long userId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM remember_tokens WHERE user_id = ?"
             )) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    /**
     * 处理 `executeUpdate` 对应的业务流程。
     *
     * @param sql 参数 `sql`
     * @param selector 参数 `selector`
     * @throws SQLException 数据库访问失败时抛出
     */
    private void executeUpdate(String sql, String selector) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, selector);
            statement.executeUpdate();
        }
    }

    /**
     * 将数据库结果映射为令牌。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private RememberToken mapToken(ResultSet resultSet) throws SQLException {
        return new RememberToken(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getString("selector"),
                resultSet.getString("token_hash"),
                toLocalDateTime(resultSet.getTimestamp("expires_at")),
                toLocalDateTime(resultSet.getTimestamp("created_at")),
                toLocalDateTime(resultSet.getTimestamp("last_used_at")),
                resultSet.getString("user_agent"),
                resultSet.getString("ip_address")
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
