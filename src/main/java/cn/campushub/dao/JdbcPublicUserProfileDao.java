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
    /**
     * 查询`ActiveById`。
     *
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
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
