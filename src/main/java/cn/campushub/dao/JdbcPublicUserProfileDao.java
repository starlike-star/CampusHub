package cn.campushub.dao;

import cn.campushub.model.PublicUserProfile;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 使用 JDBC 实现公开用户主页数据的查询与持久化操作。
 */
public class JdbcPublicUserProfileDao implements PublicUserProfileDao {
    @Override
    public Optional<PublicUserProfile> findActiveById(long userId)
            throws SQLException {
        String sql = """
                SELECT id, nickname, avatar, college, major, grade, created_at
                FROM users
                WHERE id = ? AND status = 1
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(new PublicUserProfile(
                        resultSet.getLong("id"),
                        resultSet.getString("nickname"),
                        resultSet.getString("avatar"),
                        resultSet.getString("college"),
                        resultSet.getString("major"),
                        resultSet.getString("grade"),
                        toLocalDateTime(resultSet.getTimestamp("created_at"))
                ));
            }
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
