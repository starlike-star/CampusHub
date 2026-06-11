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

    @Override
    public void updateLastUsed(String selector) throws SQLException {
        executeUpdate(
                "UPDATE remember_tokens SET last_used_at = CURRENT_TIMESTAMP "
                        + "WHERE selector = ?",
                selector
        );
    }

    @Override
    public void deleteBySelector(String selector) throws SQLException {
        executeUpdate("DELETE FROM remember_tokens WHERE selector = ?", selector);
    }

    @Override
    public int deleteExpiredTokens() throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM remember_tokens WHERE expires_at <= CURRENT_TIMESTAMP"
             )) {
            return statement.executeUpdate();
        }
    }

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

    private void executeUpdate(String sql, String selector) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, selector);
            statement.executeUpdate();
        }
    }

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

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
