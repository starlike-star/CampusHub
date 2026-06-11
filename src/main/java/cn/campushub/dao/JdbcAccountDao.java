package cn.campushub.dao;

import cn.campushub.model.AccountCancelResult;
import cn.campushub.util.JdbcUtils;
import cn.campushub.util.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 JDBC 实现账号数据的查询与持久化操作。
 */
public class JdbcAccountDao implements AccountDao {
    @Override
    public AccountCancelResult cancelAccount(
            long userId,
            String password,
            String reason,
            String ipAddress,
            String userAgent
    ) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                LockedUser user = lockUser(connection, userId);
                if (user == null) {
                    connection.rollback();
                    return AccountCancelResult.failure("当前账号不存在");
                }
                if (user.status() == 2) {
                    connection.rollback();
                    return AccountCancelResult.failure("账号已注销，请勿重复操作");
                }
                if (user.status() != 1) {
                    connection.rollback();
                    return AccountCancelResult.failure("当前账号状态不可注销");
                }
                if ("admin".equalsIgnoreCase(user.role())) {
                    connection.rollback();
                    return AccountCancelResult.failure(
                            "管理员账号不能在前台注销，请先联系系统管理员。"
                    );
                }
                if (!PasswordUtils.matches(password, user.password())) {
                    connection.rollback();
                    return AccountCancelResult.failure("当前密码错误");
                }

                cancelActivityRegistrations(connection, userId);
                hideOwnedContent(connection, userId);
                clearBehaviorAndPrivateData(connection, userId);
                cancelPendingOrders(connection, userId);
                anonymizeUser(connection, userId, reason);
                if (hasCancelLogTable(connection)) {
                    insertCancelLog(
                            connection,
                            userId,
                            user.username(),
                            user.nickname(),
                            reason,
                            ipAddress,
                            userAgent
                    );
                }
                connection.commit();
                return AccountCancelResult.completed();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean isActive(long userId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT 1
                     FROM users
                     WHERE id = ? AND status = 1
                     LIMIT 1
                     """)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private LockedUser lockUser(Connection connection, long userId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT username, password, nickname, role, status
                FROM users
                WHERE id = ?
                FOR UPDATE
                """)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new LockedUser(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("nickname"),
                        resultSet.getString("role"),
                        resultSet.getInt("status")
                );
            }
        }
    }

    private void cancelActivityRegistrations(
            Connection connection,
            long userId
    ) throws SQLException {
        List<Long> activityIds = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT activity_id
                FROM activity_registrations
                WHERE user_id = ? AND status = 'registered'
                FOR UPDATE
                """)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    activityIds.add(resultSet.getLong("activity_id"));
                }
            }
        }
        execute(
                connection,
                """
                UPDATE activity_registrations
                SET status = 'cancelled'
                WHERE user_id = ? AND status = 'registered'
                """,
                userId
        );
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE activities
                SET current_members = GREATEST(current_members - 1, 0)
                WHERE id = ?
                """)) {
            for (Long activityId : activityIds) {
                statement.setLong(1, activityId);
                statement.addBatch();
            }
            if (!activityIds.isEmpty()) {
                statement.executeBatch();
            }
        }
    }

    private void hideOwnedContent(Connection connection, long userId)
            throws SQLException {
        execute(connection, "UPDATE posts SET status = 0 WHERE user_id = ?", userId);
        execute(connection, "UPDATE comments SET status = 0 WHERE user_id = ?", userId);
        execute(
                connection,
                "UPDATE goods SET status = 'off_shelf' WHERE user_id = ?",
                userId
        );
        execute(
                connection,
                "UPDATE lost_found SET status = 'closed' WHERE user_id = ?",
                userId
        );
        execute(
                connection,
                """
                UPDATE activities
                SET status = 'closed'
                WHERE created_by = ? AND status IN ('signup', 'ongoing')
                """,
                userId
        );
        execute(
                connection,
                """
                UPDATE claim_requests
                SET status = 'rejected', message = NULL, contact = NULL
                WHERE user_id = ?
                """,
                userId
        );
    }

    private void clearBehaviorAndPrivateData(
            Connection connection,
            long userId
    ) throws SQLException {
        execute(connection, "DELETE FROM favorites WHERE user_id = ?", userId);
        execute(connection, "DELETE FROM likes WHERE user_id = ?", userId);
        execute(connection, "DELETE FROM checkins WHERE user_id = ?", userId);
        execute(connection, "DELETE FROM messages WHERE user_id = ?", userId);
        execute(
                connection,
                "DELETE FROM user_experience_logs WHERE user_id = ?",
                userId
        );
        execute(
                connection,
                """
                DELETE FROM private_messages
                WHERE sender_id = ? OR receiver_id = ?
                """,
                userId,
                userId
        );
        execute(
                connection,
                """
                DELETE FROM private_conversations
                WHERE user_a_id = ? OR user_b_id = ?
                """,
                userId,
                userId
        );
        execute(connection, "DELETE FROM remember_tokens WHERE user_id = ?", userId);
    }

    private void cancelPendingOrders(Connection connection, long userId)
            throws SQLException {
        execute(
                connection,
                """
                UPDATE goods_orders
                SET status = 'cancelled', cancelled_at = NOW()
                WHERE (buyer_id = ? OR seller_id = ?)
                  AND status = 'pending_payment'
                """,
                userId,
                userId
        );
    }

    private void anonymizeUser(
            Connection connection,
            long userId,
            String reason
    ) throws SQLException {
        execute(
                connection,
                """
                UPDATE users
                SET status = 2,
                    canceled_at = NOW(),
                    cancel_reason = ?,
                    username = CONCAT('canceled_user_', id),
                    nickname = '已注销用户',
                    avatar = 'images/default-user.png',
                    email = NULL,
                    phone = NULL,
                    student_no = NULL,
                    college = NULL,
                    major = NULL,
                    grade = NULL
                WHERE id = ? AND status = 1
                """,
                reason,
                userId
        );
    }

    private void insertCancelLog(
            Connection connection,
            long userId,
            String username,
            String nickname,
            String reason,
            String ipAddress,
            String userAgent
    ) throws SQLException {
        execute(
                connection,
                """
                INSERT INTO account_cancel_logs
                    (user_id, username_snapshot, nickname_snapshot,
                     cancel_reason, canceled_at, ip_address, user_agent)
                VALUES (?, ?, ?, ?, NOW(), ?, ?)
                """,
                userId,
                username,
                nickname,
                reason,
                ipAddress,
                userAgent
        );
    }

    private boolean hasCancelLogTable(Connection connection)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = DATABASE()
                  AND table_name = 'account_cancel_logs'
                LIMIT 1
                """);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next();
        }
    }

    private int execute(
            Connection connection,
            String sql,
            Object... values
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            return statement.executeUpdate();
        }
    }

    private record LockedUser(
            String username,
            String password,
            String nickname,
            String role,
            int status
    ) {
    }
}
