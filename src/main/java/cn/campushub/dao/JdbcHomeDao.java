package cn.campushub.dao;

import cn.campushub.model.CheckinResult;
import cn.campushub.model.ExperienceInfo;
import cn.campushub.model.HomeSidebarVO;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcHomeDao implements HomeDao {
    private static final int MYSQL_DUPLICATE_KEY = 1062;
    private static final String CHECKIN_EXPERIENCE_SOURCE = "checkin";
    private static final String CHECKIN_EXPERIENCE_DESCRIPTION =
            "每日签到获得经验";

    private final ExperienceDao experienceDao = new JdbcExperienceDao();

    public static final String NOTICE_SQL = """
            SELECT id, title, type, created_at
            FROM notices
            WHERE status = 1
            ORDER BY is_top DESC, created_at DESC
            LIMIT 3
            """;

    public static final String ACTIVITY_SQL = """
            SELECT id, title, cover_image, location, start_time,
                   current_members, max_members
            FROM activities
            WHERE status = 'signup'
            ORDER BY current_members DESC, created_at DESC
            LIMIT 3
            """;

    public static final String LOST_FOUND_SQL = """
            SELECT id, title, type, place, created_at
            FROM lost_found
            WHERE status IN ('pending', 'claiming')
            ORDER BY created_at DESC
            LIMIT 3
            """;

    private static final String CHECKIN_SQL = """
            SELECT points, continuous_days
            FROM checkins
            WHERE user_id = ? AND checkin_date = ?
            LIMIT 1
            """;

    private static final String INSERT_CHECKIN_SQL = """
            INSERT INTO checkins (user_id, checkin_date, points, continuous_days)
            VALUES (?, ?, ?, ?)
            """;

    @Override
    public List<HomeSidebarVO.NoticeItem> findLatestNotices() throws SQLException {
        List<HomeSidebarVO.NoticeItem> notices = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(NOTICE_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                notices.add(new HomeSidebarVO.NoticeItem(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("type"),
                        toLocalDateTime(resultSet.getTimestamp("created_at"))
                ));
            }
        }
        return notices;
    }

    @Override
    public List<HomeSidebarVO.ActivityItem> findRecommendedActivities()
            throws SQLException {
        List<HomeSidebarVO.ActivityItem> activities = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(ACTIVITY_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                activities.add(new HomeSidebarVO.ActivityItem(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("cover_image"),
                        resultSet.getString("location"),
                        toLocalDateTime(resultSet.getTimestamp("start_time")),
                        resultSet.getInt("current_members"),
                        resultSet.getInt("max_members")
                ));
            }
        }
        return activities;
    }

    @Override
    public List<HomeSidebarVO.LostFoundItem> findLatestLostFound()
            throws SQLException {
        List<HomeSidebarVO.LostFoundItem> items = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(LOST_FOUND_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                items.add(new HomeSidebarVO.LostFoundItem(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("type"),
                        resultSet.getString("place"),
                        toLocalDateTime(resultSet.getTimestamp("created_at"))
                ));
            }
        }
        return items;
    }

    @Override
    public Optional<HomeSidebarVO.CheckinStatus> findCheckin(
            long userId,
            LocalDate date
    ) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            return findCheckin(connection, userId, date);
        }
    }

    @Override
    public Optional<ExperienceInfo> findExperienceInfo(long userId)
            throws SQLException {
        experienceDao.reconcileCheckinExperience(userId);
        return experienceDao.getUserExperienceInfo(userId);
    }

    @Override
    public CheckinResult checkIn(long userId, LocalDate date, int points)
            throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Optional<HomeSidebarVO.CheckinStatus> today =
                        findCheckin(connection, userId, date);
                if (today.isPresent()) {
                    connection.rollback();
                    HomeSidebarVO.CheckinStatus status = today.get();
                    return CheckinResult.alreadyCheckedIn(
                            status.points(),
                            status.continuousDays(),
                            experienceDao.getUserExperienceInfo(connection, userId)
                                    .orElse(null)
                    );
                }

                int continuousDays = findCheckin(connection, userId, date.minusDays(1))
                        .map(HomeSidebarVO.CheckinStatus::continuousDays)
                        .map(days -> days + 1)
                        .orElse(1);
                try {
                    insertCheckin(
                            connection,
                            userId,
                            date,
                            points,
                            continuousDays
                    );
                } catch (SQLIntegrityConstraintViolationException exception) {
                    if (exception.getErrorCode() != MYSQL_DUPLICATE_KEY) {
                        throw exception;
                    }
                    connection.rollback();
                    HomeSidebarVO.CheckinStatus status =
                            findCheckin(connection, userId, date)
                                    .orElseThrow(() -> exception);
                    return CheckinResult.alreadyCheckedIn(
                            status.points(),
                            status.continuousDays(),
                            experienceDao.getUserExperienceInfo(connection, userId)
                                    .orElse(null)
                    );
                }

                ExperienceInfo experience =
                        experienceDao.addExperience(connection, userId, points);
                experienceDao.insertExperienceLog(
                        connection,
                        userId,
                        points,
                        CHECKIN_EXPERIENCE_SOURCE,
                        CHECKIN_EXPERIENCE_DESCRIPTION
                );
                connection.commit();
                return CheckinResult.success(
                        points,
                        continuousDays,
                        experience
                );
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void insertCheckin(
            Connection connection,
            long userId,
            LocalDate date,
            int points,
            int continuousDays
    ) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement(INSERT_CHECKIN_SQL)) {
            statement.setLong(1, userId);
            statement.setDate(2, Date.valueOf(date));
            statement.setInt(3, points);
            statement.setInt(4, continuousDays);
            statement.executeUpdate();
        }
    }

    private Optional<HomeSidebarVO.CheckinStatus> findCheckin(
            Connection connection,
            long userId,
            LocalDate date
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CHECKIN_SQL)) {
            statement.setLong(1, userId);
            statement.setDate(2, Date.valueOf(date));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(new HomeSidebarVO.CheckinStatus(
                        true,
                        true,
                        resultSet.getInt("points"),
                        resultSet.getInt("continuous_days")
                ));
            }
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
