package cn.campushub.dao;

import cn.campushub.model.FavoriteItemVO;
import cn.campushub.model.Post;
import cn.campushub.model.ProfileOverviewVO;
import cn.campushub.model.ProfileActivityVO;
import cn.campushub.model.User;
import cn.campushub.model.UserCheckinStatsVO;
import cn.campushub.model.UserCommentVO;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class JdbcProfileDao implements ProfileDao {
    private static final String OVERVIEW_SQL = """
            SELECT u.id, u.username, u.password, u.nickname, u.avatar,
                   u.student_no, u.college, u.major, u.grade, u.email,
                   u.phone, u.role, u.status, u.created_at, u.updated_at,
                   (SELECT COUNT(*) FROM posts p
                    WHERE p.user_id = u.id AND p.status = 1) AS post_count,
                   (SELECT COUNT(*) FROM comments cm
                    WHERE cm.user_id = u.id AND cm.status = 1) AS comment_count,
                   (SELECT COUNT(*) FROM favorites f
                    WHERE f.user_id = u.id) AS favorite_count,
                   (SELECT COUNT(*) FROM goods g
                    WHERE g.user_id = u.id) AS goods_count,
                   (SELECT COUNT(*) FROM checkins ck
                    WHERE ck.user_id = u.id) AS checkin_days,
                   COALESCE((SELECT ck.continuous_days FROM checkins ck
                    WHERE ck.user_id = u.id
                    ORDER BY ck.checkin_date DESC LIMIT 1), 0) AS continuous_days
            FROM users u
            WHERE u.id = ? AND u.status = 1
            LIMIT 1
            """;

    private static final String POSTS_SQL = """
            SELECT p.id, p.user_id, p.category_id, p.title, p.content,
                   p.images, p.topic, p.like_count, p.comment_count,
                   p.favorite_count, p.view_count, p.status,
                   p.created_at, p.updated_at, u.avatar AS author_avatar,
                   u.nickname AS author_nickname, u.college AS author_college,
                   u.grade AS author_grade, c.name AS category_name
            FROM posts p
            JOIN users u ON p.user_id = u.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE p.user_id = ? AND p.status = 1
            ORDER BY p.created_at DESC
            """;

    private static final String COMMENTS_SQL = """
            SELECT cm.id, cm.post_id, cm.content, cm.like_count,
                   cm.created_at, p.title AS post_title
            FROM comments cm
            JOIN posts p ON cm.post_id = p.id
            WHERE cm.user_id = ? AND cm.status = 1
            ORDER BY cm.created_at DESC
            """;

    private static final String FAVORITE_POSTS_SQL = """
            SELECT f.id AS favorite_id, f.created_at AS favorite_time,
                   p.id, p.title, p.content, p.topic, p.like_count,
                   p.comment_count, p.view_count, u.nickname,
                   c.name AS category_name
            FROM favorites f
            JOIN posts p ON f.target_id = p.id
            JOIN users u ON p.user_id = u.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE f.user_id = ? AND f.target_type = 'post' AND p.status = 1
            ORDER BY f.created_at DESC
            """;

    private static final String FAVORITE_GOODS_SQL = """
            SELECT f.id AS favorite_id, f.created_at AS favorite_time,
                   g.id, g.title, g.price, g.condition_level, g.images,
                   g.trade_place, g.trade_method, g.status, u.nickname,
                   c.name AS category_name
            FROM favorites f
            JOIN goods g ON f.target_id = g.id
            JOIN users u ON g.user_id = u.id
            LEFT JOIN categories c ON g.category_id = c.id
            WHERE f.user_id = ? AND f.target_type = 'goods'
              AND g.status != 'off_shelf'
            ORDER BY f.created_at DESC
            """;

    private static final String CHECKIN_STATS_SQL = """
            SELECT COUNT(*) AS total_days,
                   COALESCE(SUM(points), 0) AS total_points,
                   COALESCE((SELECT continuous_days FROM checkins
                    WHERE user_id = ?
                    ORDER BY checkin_date DESC LIMIT 1), 0) AS continuous_days
            FROM checkins
            WHERE user_id = ?
            """;

    private static final String CHECKIN_RECORDS_SQL = """
            SELECT checkin_date, points, continuous_days, created_at
            FROM checkins
            WHERE user_id = ?
            ORDER BY checkin_date DESC
            LIMIT 30
            """;

    private static final String ACTIVITIES_SQL = """
            SELECT a.id, a.title, a.cover_image, a.location, a.start_time,
                   a.end_time, a.deadline, a.status, a.current_members,
                   a.max_members, ar.status AS registration_status,
                   ar.created_at AS registered_at
            FROM activity_registrations ar
            JOIN activities a ON ar.activity_id = a.id
            WHERE ar.user_id = ?
            ORDER BY ar.created_at DESC
            """;

    private static final String UPDATE_PROFILE_SQL = """
            UPDATE users
            SET nickname = ?, avatar = ?, student_no = ?, college = ?,
                major = ?, grade = ?, email = ?, phone = ?
            WHERE id = ? AND status = 1
            """;

    @Override
    public Optional<ProfileOverviewVO> findOverview(long userId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(OVERVIEW_SQL)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                User user = mapUser(resultSet);
                ProfileOverviewVO.Stats stats = new ProfileOverviewVO.Stats(
                        resultSet.getInt("post_count"),
                        resultSet.getInt("comment_count"),
                        resultSet.getInt("favorite_count"),
                        resultSet.getInt("goods_count"),
                        resultSet.getInt("checkin_days"),
                        resultSet.getInt("continuous_days")
                );
                return Optional.of(new ProfileOverviewVO(user, stats));
            }
        }
    }

    @Override
    public List<Post> findPosts(long userId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(POSTS_SQL)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        }
        return posts;
    }

    @Override
    public List<UserCommentVO> findComments(long userId) throws SQLException {
        List<UserCommentVO> comments = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(COMMENTS_SQL)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    comments.add(new UserCommentVO(
                            resultSet.getLong("id"),
                            resultSet.getLong("post_id"),
                            resultSet.getString("content"),
                            resultSet.getInt("like_count"),
                            toLocalDateTime(resultSet.getTimestamp("created_at")),
                            resultSet.getString("post_title")
                    ));
                }
            }
        }
        return comments;
    }

    @Override
    public List<FavoriteItemVO> findFavorites(long userId) throws SQLException {
        List<FavoriteItemVO> favorites = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement(FAVORITE_POSTS_SQL)) {
                statement.setLong(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String content = resultSet.getString("content");
                        favorites.add(new FavoriteItemVO(
                                resultSet.getLong("favorite_id"),
                                "post",
                                resultSet.getLong("id"),
                                resultSet.getString("title"),
                                summarize(content),
                                resultSet.getString("category_name"),
                                resultSet.getString("nickname"),
                                resultSet.getString("topic"),
                                resultSet.getInt("like_count"),
                                resultSet.getInt("comment_count"),
                                resultSet.getInt("view_count"),
                                null, null, null, null, null, null,
                                toLocalDateTime(resultSet.getTimestamp("favorite_time"))
                        ));
                    }
                }
            }
            try (PreparedStatement statement =
                         connection.prepareStatement(FAVORITE_GOODS_SQL)) {
                statement.setLong(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        favorites.add(new FavoriteItemVO(
                                resultSet.getLong("favorite_id"),
                                "goods",
                                resultSet.getLong("id"),
                                resultSet.getString("title"),
                                null,
                                resultSet.getString("category_name"),
                                resultSet.getString("nickname"),
                                null, 0, 0, 0,
                                resultSet.getBigDecimal("price"),
                                resultSet.getString("condition_level"),
                                resultSet.getString("images"),
                                resultSet.getString("trade_place"),
                                resultSet.getString("trade_method"),
                                resultSet.getString("status"),
                                toLocalDateTime(resultSet.getTimestamp("favorite_time"))
                        ));
                    }
                }
            }
        }
        favorites.sort(Comparator.comparing(
                FavoriteItemVO::favoriteTime,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
        return favorites;
    }

    @Override
    public UserCheckinStatsVO findCheckins(long userId) throws SQLException {
        int totalDays = 0;
        int totalPoints = 0;
        int continuousDays = 0;
        List<UserCheckinStatsVO.Record> records = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement(CHECKIN_STATS_SQL)) {
                statement.setLong(1, userId);
                statement.setLong(2, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        totalDays = resultSet.getInt("total_days");
                        totalPoints = resultSet.getInt("total_points");
                        continuousDays = resultSet.getInt("continuous_days");
                    }
                }
            }
            try (PreparedStatement statement =
                         connection.prepareStatement(CHECKIN_RECORDS_SQL)) {
                statement.setLong(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Date date = resultSet.getDate("checkin_date");
                        records.add(new UserCheckinStatsVO.Record(
                                date == null ? null : date.toLocalDate(),
                                resultSet.getInt("points"),
                                resultSet.getInt("continuous_days"),
                                toLocalDateTime(resultSet.getTimestamp("created_at"))
                        ));
                    }
                }
            }
        }
        return new UserCheckinStatsVO(
                totalDays,
                totalPoints,
                continuousDays,
                List.copyOf(records)
        );
    }

    @Override
    public List<ProfileActivityVO> findActivities(long userId)
            throws SQLException {
        List<ProfileActivityVO> activities = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(ACTIVITIES_SQL)) {
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

    @Override
    public Optional<User> updateProfile(User user) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(UPDATE_PROFILE_SQL)) {
            statement.setString(1, user.getNickname());
            statement.setString(2, user.getAvatar());
            statement.setString(3, user.getStudentNo());
            statement.setString(4, user.getCollege());
            statement.setString(5, user.getMajor());
            statement.setString(6, user.getGrade());
            statement.setString(7, user.getEmail());
            statement.setString(8, user.getPhone());
            statement.setLong(9, user.getId());
            if (statement.executeUpdate() != 1) {
                return Optional.empty();
            }
        }
        return findOverview(user.getId()).map(ProfileOverviewVO::user);
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setNickname(resultSet.getString("nickname"));
        user.setAvatar(resultSet.getString("avatar"));
        user.setStudentNo(resultSet.getString("student_no"));
        user.setCollege(resultSet.getString("college"));
        user.setMajor(resultSet.getString("major"));
        user.setGrade(resultSet.getString("grade"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));
        user.setRole(resultSet.getString("role"));
        user.setStatus(resultSet.getInt("status"));
        user.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        user.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        return user;
    }

    private Post mapPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setUserId(resultSet.getLong("user_id"));
        long categoryId = resultSet.getLong("category_id");
        post.setCategoryId(resultSet.wasNull() ? null : categoryId);
        post.setTitle(resultSet.getString("title"));
        post.setContent(resultSet.getString("content"));
        post.setImages(resultSet.getString("images"));
        post.setTopic(resultSet.getString("topic"));
        post.setLikeCount(resultSet.getInt("like_count"));
        post.setCommentCount(resultSet.getInt("comment_count"));
        post.setFavoriteCount(resultSet.getInt("favorite_count"));
        post.setViewCount(resultSet.getInt("view_count"));
        post.setStatus(resultSet.getInt("status"));
        post.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        post.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        post.setAuthorAvatar(resultSet.getString("author_avatar"));
        post.setAuthorNickname(resultSet.getString("author_nickname"));
        post.setAuthorCollege(resultSet.getString("author_college"));
        post.setAuthorGrade(resultSet.getString("author_grade"));
        post.setCategoryName(resultSet.getString("category_name"));
        return post;
    }

    private String summarize(String content) {
        if (content == null) {
            return "";
        }
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 120
                ? normalized
                : normalized.substring(0, 120) + "...";
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
