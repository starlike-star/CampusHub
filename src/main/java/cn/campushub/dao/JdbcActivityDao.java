package cn.campushub.dao;

import cn.campushub.model.Activity;
import cn.campushub.model.ActivityVO;
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
 * 使用 JDBC 实现活动数据的查询与持久化操作。
 */
public class JdbcActivityDao implements ActivityDao {
    private static final String SELECT_FIELDS = """
            SELECT a.id, a.title, a.content, a.cover_image, a.location,
                   a.start_time, a.end_time, a.deadline, a.max_members,
                   a.current_members, a.status, a.created_by, a.created_at,
                   a.updated_at, u.nickname, u.avatar, u.college
            FROM activities a
            LEFT JOIN users u ON a.created_by = u.id
            """;

    @Override
    public List<ActivityVO> findAll(String status, String keyword, String sort)
            throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_FIELDS).append(" WHERE 1 = 1");
        List<String> parameters = new ArrayList<>();
        if (status != null) {
            sql.append(" AND a.status = ?");
            parameters.add(status);
        }
        if (keyword != null) {
            sql.append("""
                     AND (a.title LIKE ? OR a.content LIKE ? OR a.location LIKE ?)
                    """);
            String like = "%" + keyword + "%";
            parameters.add(like);
            parameters.add(like);
            parameters.add(like);
        }
        if ("hot".equals(sort)) {
            sql.append(" ORDER BY a.current_members DESC, a.created_at DESC");
        } else if ("soon".equals(sort)) {
            sql.append(" ORDER BY a.start_time ASC, a.created_at DESC");
        } else {
            sql.append(" ORDER BY a.created_at DESC");
        }

        List<ActivityVO> activities = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setString(index + 1, parameters.get(index));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    activities.add(mapActivity(resultSet));
                }
            }
        }
        return activities;
    }

    @Override
    public Optional<ActivityVO> findById(long id) throws SQLException {
        String sql = SELECT_FIELDS + " WHERE a.id = ? LIMIT 1";
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapActivity(resultSet))
                        : Optional.empty();
            }
        }
    }

    @Override
    public long create(Activity activity) throws SQLException {
        String sql = """
                INSERT INTO activities
                    (title, content, cover_image, location, start_time, end_time,
                     deadline, max_members, current_members, status, created_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 'signup', ?)
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            setEditableFields(statement, activity, false);
            statement.setLong(9, activity.getCreatedBy());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("活动发布后未获得主键");
                }
                return keys.getLong(1);
            }
        }
    }

    @Override
    public boolean update(Activity activity, long userId, boolean admin)
            throws SQLException {
        String sql = """
                UPDATE activities
                SET title = ?, content = ?, cover_image = ?, location = ?,
                    start_time = ?, end_time = ?, deadline = ?, max_members = ?
                WHERE id = ? AND ? >= current_members
                """ + (admin ? "" : " AND created_by = ?");
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setEditableFields(statement, activity, false);
            statement.setLong(9, activity.getId());
            statement.setInt(10, activity.getMaxMembers());
            if (!admin) {
                statement.setLong(11, userId);
            }
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean updateStatus(
            long id,
            long userId,
            boolean admin,
            String status
    ) throws SQLException {
        String sql = """
                UPDATE activities
                SET status = ?
                WHERE id = ?
                """ + (admin ? "" : " AND created_by = ?");
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setLong(2, id);
            if (!admin) {
                statement.setLong(3, userId);
            }
            return statement.executeUpdate() == 1;
        }
    }

    private void setEditableFields(
            PreparedStatement statement,
            Activity activity,
            boolean includeId
    ) throws SQLException {
        statement.setString(1, activity.getTitle());
        statement.setString(2, activity.getContent());
        statement.setString(3, activity.getCoverImage());
        statement.setString(4, activity.getLocation());
        statement.setTimestamp(5, Timestamp.valueOf(activity.getStartTime()));
        statement.setTimestamp(6, Timestamp.valueOf(activity.getEndTime()));
        statement.setTimestamp(7, Timestamp.valueOf(activity.getDeadline()));
        statement.setInt(8, activity.getMaxMembers());
        if (includeId) {
            statement.setLong(9, activity.getId());
        }
    }

    private ActivityVO mapActivity(ResultSet resultSet) throws SQLException {
        Activity activity = new Activity();
        activity.setId(resultSet.getLong("id"));
        activity.setTitle(resultSet.getString("title"));
        activity.setContent(resultSet.getString("content"));
        activity.setCoverImage(resultSet.getString("cover_image"));
        activity.setLocation(resultSet.getString("location"));
        activity.setStartTime(toLocalDateTime(resultSet.getTimestamp("start_time")));
        activity.setEndTime(toLocalDateTime(resultSet.getTimestamp("end_time")));
        activity.setDeadline(toLocalDateTime(resultSet.getTimestamp("deadline")));
        activity.setMaxMembers(resultSet.getInt("max_members"));
        activity.setCurrentMembers(resultSet.getInt("current_members"));
        activity.setStatus(resultSet.getString("status"));
        long createdBy = resultSet.getLong("created_by");
        activity.setCreatedBy(resultSet.wasNull() ? null : createdBy);
        activity.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        activity.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        return new ActivityVO(
                activity,
                resultSet.getString("nickname"),
                resultSet.getString("avatar"),
                resultSet.getString("college")
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
