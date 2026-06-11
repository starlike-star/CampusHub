package cn.campushub.dao;

import cn.campushub.model.ExperienceInfo;
import cn.campushub.model.ExperienceLog;
import cn.campushub.util.JdbcUtils;
import cn.campushub.util.LevelUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExperienceDao implements ExperienceDao {
    private static final int CHECKIN_EXPERIENCE = 5;
    private static final String CHECKIN_SOURCE = "checkin";
    private static final String HISTORICAL_CHECKIN_DESCRIPTION =
            "历史每日签到经验补发";

    private static final String ADD_EXPERIENCE_SQL = """
            UPDATE users
            SET experience = COALESCE(experience, 0) + ?
            WHERE id = ? AND status = 1
            """;

    private static final String UPDATE_LEVEL_SQL = """
            UPDATE users
            SET level = ?
            WHERE id = ? AND status = 1
            """;

    private static final String EXPERIENCE_INFO_SQL = """
            SELECT experience, level
            FROM users
            WHERE id = ? AND status = 1
            LIMIT 1
            """;

    private static final String INSERT_LOG_SQL = """
            INSERT INTO user_experience_logs
                (user_id, change_value, source, description)
            VALUES (?, ?, ?, ?)
            """;

    private static final String LOCK_USER_SQL = """
            SELECT experience
            FROM users
            WHERE id = ? AND status = 1
            FOR UPDATE
            """;

    private static final String CHECKIN_COUNT_SQL = """
            SELECT COUNT(*)
            FROM checkins
            WHERE user_id = ?
            """;

    private static final String CHECKIN_LOG_COUNT_SQL = """
            SELECT COUNT(*)
            FROM user_experience_logs
            WHERE user_id = ? AND source = 'checkin'
            """;

    @Override
    public ExperienceInfo addExperience(
            Connection connection,
            long userId,
            int value
    ) throws SQLException {
        if (value <= 0) {
            throw new IllegalArgumentException("Experience value must be positive");
        }
        try (PreparedStatement statement =
                     connection.prepareStatement(ADD_EXPERIENCE_SQL)) {
            statement.setInt(1, value);
            statement.setLong(2, userId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("User does not exist or is unavailable");
            }
        }
        ExperienceInfo info = getUserExperienceInfo(connection, userId)
                .orElseThrow(() -> new SQLException("Failed to read user experience"));
        int calculatedLevel = LevelUtils.calculateLevel(info.experience());
        updateUserLevel(connection, userId, calculatedLevel);
        return LevelUtils.experienceInfo(info.experience());
    }

    @Override
    public void insertExperienceLog(
            Connection connection,
            long userId,
            int changeValue,
            String source,
            String description
    ) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement(INSERT_LOG_SQL)) {
            statement.setLong(1, userId);
            statement.setInt(2, changeValue);
            statement.setString(3, source);
            statement.setString(4, description);
            statement.executeUpdate();
        }
    }

    @Override
    public List<ExperienceLog> getRecentLogs(long userId, int limit)
            throws SQLException {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        String sql = """
                SELECT id, user_id, change_value, source, description, created_at
                FROM user_experience_logs
                WHERE user_id = ?
                ORDER BY created_at DESC, id DESC
                LIMIT ?
                """;
        List<ExperienceLog> logs = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setInt(2, safeLimit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(new ExperienceLog(
                            resultSet.getLong("id"),
                            resultSet.getLong("user_id"),
                            resultSet.getInt("change_value"),
                            resultSet.getString("source"),
                            resultSet.getString("description"),
                            toLocalDateTime(resultSet.getTimestamp("created_at"))
                    ));
                }
            }
        }
        return List.copyOf(logs);
    }

    @Override
    public int reconcileCheckinExperience(long userId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!lockActiveUser(connection, userId)) {
                    connection.rollback();
                    return 0;
                }
                int checkinCount = countForUser(
                        connection,
                        CHECKIN_COUNT_SQL,
                        userId
                );
                int checkinLogCount = countForUser(
                        connection,
                        CHECKIN_LOG_COUNT_SQL,
                        userId
                );
                int missingCount = Math.max(checkinCount - checkinLogCount, 0);
                if (missingCount == 0) {
                    connection.commit();
                    return 0;
                }

                int experienceToAdd = Math.multiplyExact(
                        missingCount,
                        CHECKIN_EXPERIENCE
                );
                addExperience(connection, userId, experienceToAdd);
                for (int i = 0; i < missingCount; i++) {
                    insertExperienceLog(
                            connection,
                            userId,
                            CHECKIN_EXPERIENCE,
                            CHECKIN_SOURCE,
                            HISTORICAL_CHECKIN_DESCRIPTION
                    );
                }
                connection.commit();
                return missingCount;
            } catch (SQLException | ArithmeticException exception) {
                connection.rollback();
                if (exception instanceof SQLException sqlException) {
                    throw sqlException;
                }
                throw new SQLException(
                        "Historical check-in experience exceeds supported range",
                        exception
                );
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public void updateUserLevel(
            Connection connection,
            long userId,
            int level
    ) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement(UPDATE_LEVEL_SQL)) {
            statement.setInt(1, Math.max(level, 1));
            statement.setLong(2, userId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Failed to update user level");
            }
        }
    }

    @Override
    public Optional<ExperienceInfo> getUserExperienceInfo(long userId)
            throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            return getUserExperienceInfo(connection, userId);
        }
    }

    @Override
    public Optional<ExperienceInfo> getUserExperienceInfo(
            Connection connection,
            long userId
    ) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement(EXPERIENCE_INFO_SQL)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                int experience = resultSet.getInt("experience");
                ExperienceInfo calculated = LevelUtils.experienceInfo(experience);
                return Optional.of(calculated);
            }
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private boolean lockActiveUser(Connection connection, long userId)
            throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement(LOCK_USER_SQL)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private int countForUser(
            Connection connection,
            String sql,
            long userId
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }
}
