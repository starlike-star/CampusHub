package cn.campushub.dao;

import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcReportDao implements ReportDao {
    @Override
    public Long findTargetOwnerId(String targetType, long targetId)
            throws SQLException {
        String sql = switch (targetType) {
            case "post" -> """
                    SELECT user_id FROM posts
                    WHERE id = ? AND status <> 0
                    """;
            case "comment" -> """
                    SELECT user_id FROM comments
                    WHERE id = ? AND status <> 0
                    """;
            case "goods" -> """
                    SELECT user_id FROM goods
                    WHERE id = ? AND status <> 'off_shelf'
                    """;
            case "lost_found" -> """
                    SELECT user_id FROM lost_found
                    WHERE id = ? AND status <> 'closed'
                    """;
            default -> throw new IllegalArgumentException("不支持的举报类型");
        };
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, targetId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong("user_id") : null;
            }
        }
    }

    @Override
    public boolean existsPendingReport(
            long userId,
            String targetType,
            long targetId
    ) throws SQLException {
        String sql = """
                SELECT 1
                FROM reports
                WHERE user_id = ?
                  AND target_type = ?
                  AND target_id = ?
                  AND status = 'pending'
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setString(2, targetType);
            statement.setLong(3, targetId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public int createReport(
            long userId,
            String targetType,
            long targetId,
            String reason
    ) throws SQLException {
        String sql = """
                INSERT INTO reports
                    (user_id, target_id, target_type, reason, status,
                     handled_by, handled_at)
                VALUES (?, ?, ?, ?, 'pending', NULL, NULL)
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, targetId);
            statement.setString(3, targetType);
            statement.setString(4, reason);
            return statement.executeUpdate();
        }
    }
}
