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
    /**
     * 取消账号。
     *
     * @param userId 用户编号
     * @param password 密码
     * @param reason 参数 `reason`
     * @param ipAddress 参数 `ipAddress`
     * @param userAgent 参数 `userAgent`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 判断是否`Active`。
     *
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 根据输入计算并返回 `lockUser` 的处理结果。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 取消活动报名记录。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 处理 `hideOwnedContent` 对应的业务流程。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 处理 `clearBehaviorAndPrivateData` 对应的业务流程。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 取消`PendingOrders`。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 处理 `anonymizeUser` 对应的业务流程。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @param reason 参数 `reason`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 新增`CancelLog`。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @param username 用户名
     * @param nickname 用户昵称
     * @param reason 参数 `reason`
     * @param ipAddress 参数 `ipAddress`
     * @param userAgent 参数 `userAgent`
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 判断是否具有`CancelLogTable`。
     *
     * @param connection 数据库连接
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 根据输入计算并返回 `execute` 的处理结果。
     *
     * @param connection 数据库连接
     * @param sql 参数 `sql`
     * @param values 参数 `values`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 根据输入计算并返回 `LockedUser` 的处理结果。
     *
     * @param username 用户名
     * @param password 密码
     * @param nickname 用户昵称
     * @param role 参数 `role`
     * @param status 业务状态
     * @return 方法处理结果
     */
    private record LockedUser(
            String username,
            String password,
            String nickname,
            String role,
            int status
    ) {
    }
}
