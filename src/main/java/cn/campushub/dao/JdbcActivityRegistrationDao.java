package cn.campushub.dao;

import cn.campushub.model.ActivityRegistrationResult;
import cn.campushub.model.ActivityRegistrationVO;
import cn.campushub.model.ProfileActivityVO;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 使用 JDBC 实现活动报名数据的查询与持久化操作。
 */
public class JdbcActivityRegistrationDao implements ActivityRegistrationDao {
    /**
     * 查询`RegisteredActivityIds`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Set<Long> findRegisteredActivityIds(long userId) throws SQLException {
        String sql = """
                SELECT activity_id
                FROM activity_registrations
                WHERE user_id = ? AND status = 'registered'
                """;
        Set<Long> ids = new HashSet<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getLong("activity_id"));
                }
            }
        }
        return Set.copyOf(ids);
    }

    /**
     * 判断是否已报名。
     *
     * @param activityId 活动编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean isRegistered(long activityId, long userId)
            throws SQLException {
        String sql = """
                SELECT 1
                FROM activity_registrations
                WHERE activity_id = ? AND user_id = ? AND status = 'registered'
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, activityId);
            statement.setLong(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 提交`JdbcActivityRegistration`。
     *
     * @param activityId 活动编号
     * @param userId 用户编号
     * @param userNickname 参数 `userNickname`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public ActivityRegistrationResult register(
            long activityId,
            long userId,
            String userNickname
    ) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                LockedActivity activity = lockActivity(connection, activityId);
                LocalDateTime now = LocalDateTime.now();
                if (!"signup".equals(activity.status())) {
                    throw new IllegalStateException("当前活动不在报名中");
                }
                if (activity.deadline() != null && now.isAfter(activity.deadline())) {
                    throw new IllegalStateException("活动报名已截止");
                }
                if (activity.maxMembers() > 0
                        && activity.currentMembers() >= activity.maxMembers()) {
                    throw new IllegalStateException("活动报名人数已满");
                }

                String registrationStatus =
                        findRegistrationStatus(connection, activityId, userId);
                if ("registered".equals(registrationStatus)) {
                    throw new IllegalStateException("你已经报名该活动");
                }
                if ("cancelled".equals(registrationStatus)) {
                    updateRegistration(connection, activityId, userId, "registered");
                } else {
                    insertRegistration(connection, activityId, userId);
                }
                int currentMembers = activity.currentMembers() + 1;
                updateMemberCount(connection, activityId, currentMembers);
                insertMessage(
                        connection,
                        userId,
                        "活动报名成功",
                        "你已成功报名活动《" + activity.title() + "》。"
                );
                if (activity.createdBy() != null && activity.createdBy() != userId) {
                    String nickname = userNickname == null || userNickname.isBlank()
                            ? "一位同学"
                            : userNickname;
                    insertMessage(
                            connection,
                            activity.createdBy(),
                            "有人报名了你的活动",
                            nickname + " 报名了你发布的活动《"
                                    + activity.title() + "》。"
                    );
                }
                connection.commit();
                return new ActivityRegistrationResult(true, currentMembers);
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 取消`JdbcActivityRegistration`。
     *
     * @param activityId 活动编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public ActivityRegistrationResult cancel(long activityId, long userId)
            throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                LockedActivity activity = lockActivity(connection, activityId);
                String registrationStatus =
                        findRegistrationStatus(connection, activityId, userId);
                if (!"registered".equals(registrationStatus)) {
                    throw new IllegalStateException("你尚未报名该活动");
                }
                updateRegistration(connection, activityId, userId, "cancelled");
                int currentMembers = Math.max(0, activity.currentMembers() - 1);
                updateMemberCount(connection, activityId, currentMembers);
                insertMessage(
                        connection,
                        userId,
                        "已取消活动报名",
                        "你已取消报名活动《" + activity.title() + "》。"
                );
                connection.commit();
                return new ActivityRegistrationResult(false, currentMembers);
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 查询报名记录。
     *
     * @param activityId 活动编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<ActivityRegistrationVO> findRegistrations(long activityId)
            throws SQLException {
        String sql = """
                SELECT ar.id, ar.activity_id, ar.user_id, ar.status, ar.created_at,
                       u.nickname, u.avatar, u.college, u.major, u.grade,
                       u.email, u.phone
                FROM activity_registrations ar
                JOIN users u ON ar.user_id = u.id
                WHERE ar.activity_id = ? AND ar.status = 'registered'
                ORDER BY ar.created_at DESC
                """;
        List<ActivityRegistrationVO> registrations = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, activityId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    registrations.add(new ActivityRegistrationVO(
                            resultSet.getLong("id"),
                            resultSet.getLong("activity_id"),
                            resultSet.getLong("user_id"),
                            resultSet.getString("status"),
                            toLocalDateTime(resultSet.getTimestamp("created_at")),
                            resultSet.getString("nickname"),
                            resultSet.getString("avatar"),
                            resultSet.getString("college"),
                            resultSet.getString("major"),
                            resultSet.getString("grade"),
                            resultSet.getString("email"),
                            resultSet.getString("phone")
                    ));
                }
            }
        }
        return registrations;
    }

    /**
     * 根据用户查询`JdbcActivityRegistration`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<ProfileActivityVO> findByUser(long userId) throws SQLException {
        String sql = """
                SELECT a.id, a.title, a.cover_image, a.location, a.start_time,
                       a.end_time, a.deadline, a.status, a.current_members,
                       a.max_members, ar.status AS registration_status,
                       ar.created_at AS registered_at
                FROM activity_registrations ar
                JOIN activities a ON ar.activity_id = a.id
                WHERE ar.user_id = ?
                ORDER BY ar.created_at DESC
                """;
        List<ProfileActivityVO> activities = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    activities.add(new ProfileActivityVO(
                            resultSet.getLong("id"),
                            resultSet.getString("title"),
                            resultSet.getString("cover_image"),
                            resultSet.getString("location"),
                            toLocalDateTime(resultSet.getTimestamp("start_time")),
                            toLocalDateTime(resultSet.getTimestamp("end_time")),
                            toLocalDateTime(resultSet.getTimestamp("deadline")),
                            resultSet.getString("status"),
                            resultSet.getInt("current_members"),
                            resultSet.getInt("max_members"),
                            resultSet.getString("registration_status"),
                            toLocalDateTime(resultSet.getTimestamp("registered_at"))
                    ));
                }
            }
        }
        return activities;
    }

    /**
     * 根据输入计算并返回 `lockActivity` 的处理结果。
     *
     * @param connection 数据库连接
     * @param activityId 活动编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private LockedActivity lockActivity(Connection connection, long activityId)
            throws SQLException {
        String sql = """
                SELECT id, title, deadline, max_members, current_members,
                       status, created_by
                FROM activities
                WHERE id = ?
                FOR UPDATE
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, activityId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalStateException("活动不存在");
                }
                long createdBy = resultSet.getLong("created_by");
                return new LockedActivity(
                        resultSet.getString("title"),
                        toLocalDateTime(resultSet.getTimestamp("deadline")),
                        resultSet.getInt("max_members"),
                        resultSet.getInt("current_members"),
                        resultSet.getString("status"),
                        resultSet.wasNull() ? null : createdBy
                );
            }
        }
    }

    /**
     * 查询报名状态。
     *
     * @param connection 数据库连接
     * @param activityId 活动编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private String findRegistrationStatus(
            Connection connection,
            long activityId,
            long userId
    ) throws SQLException {
        String sql = """
                SELECT status
                FROM activity_registrations
                WHERE activity_id = ? AND user_id = ?
                FOR UPDATE
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, activityId);
            statement.setLong(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("status") : null;
            }
        }
    }

    /**
     * 新增报名。
     *
     * @param connection 数据库连接
     * @param activityId 活动编号
     * @param userId 用户编号
     * @throws SQLException 数据库访问失败时抛出
     */
    private void insertRegistration(
            Connection connection,
            long activityId,
            long userId
    ) throws SQLException {
        String sql = """
                INSERT INTO activity_registrations (activity_id, user_id, status)
                VALUES (?, ?, 'registered')
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, activityId);
            statement.setLong(2, userId);
            statement.executeUpdate();
        }
    }

    /**
     * 更新报名。
     *
     * @param connection 数据库连接
     * @param activityId 活动编号
     * @param userId 用户编号
     * @param status 业务状态
     * @throws SQLException 数据库访问失败时抛出
     */
    private void updateRegistration(
            Connection connection,
            long activityId,
            long userId,
            String status
    ) throws SQLException {
        String sql = """
                UPDATE activity_registrations
                SET status = ?
                WHERE activity_id = ? AND user_id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setLong(2, activityId);
            statement.setLong(3, userId);
            statement.executeUpdate();
        }
    }

    /**
     * 更新成员数量。
     *
     * @param connection 数据库连接
     * @param activityId 活动编号
     * @param currentMembers 当前人数
     * @throws SQLException 数据库访问失败时抛出
     */
    private void updateMemberCount(
            Connection connection,
            long activityId,
            int currentMembers
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE activities SET current_members = ? WHERE id = ?"
        )) {
            statement.setInt(1, currentMembers);
            statement.setLong(2, activityId);
            statement.executeUpdate();
        }
    }

    /**
     * 新增消息。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @param title 标题
     * @param content 正文内容
     * @throws SQLException 数据库访问失败时抛出
     */
    private void insertMessage(
            Connection connection,
            long userId,
            String title,
            String content
    ) throws SQLException {
        String sql = """
                INSERT INTO messages (user_id, title, content, type, is_read)
                VALUES (?, ?, ?, 'activity', 0)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setString(2, title);
            statement.setString(3, content);
            statement.executeUpdate();
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

    /**
     * 根据输入计算并返回 `LockedActivity` 的处理结果。
     *
     * @param title 标题
     * @param deadline 截止时间
     * @param maxMembers 人数上限
     * @param currentMembers 当前人数
     * @param status 业务状态
     * @param createdBy 参数 `createdBy`
     * @return 方法处理结果
     */
    private record LockedActivity(
            String title,
            LocalDateTime deadline,
            int maxMembers,
            int currentMembers,
            String status,
            Long createdBy
    ) {
    }
}
