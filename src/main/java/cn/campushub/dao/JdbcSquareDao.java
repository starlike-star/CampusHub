package cn.campushub.dao;

import cn.campushub.model.Notice;
import cn.campushub.model.Post;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 JDBC 实现校园广场数据的查询与持久化操作。
 */
public class JdbcSquareDao implements SquareDao {
    public static final String LATEST_ORDER =
            "ORDER BY p.created_at DESC";
    public static final String HOT_ORDER =
            "ORDER BY p.like_count DESC, p.comment_count DESC, "
                    + "p.view_count DESC, p.created_at DESC";
    public static final String CATEGORY_FILTER =
            "AND c.name = ? AND c.type = 'post'";
    public static final String NOTICE_ORDER =
            "ORDER BY n.is_top DESC, n.created_at DESC";

    private static final String POST_SELECT = """
            SELECT p.id, p.user_id, p.category_id, p.title, p.content, p.images,
                   p.topic, p.like_count, p.comment_count, p.favorite_count,
                   p.view_count, p.status, p.created_at, p.updated_at,
                   u.avatar AS author_avatar, u.nickname AS author_nickname,
                   u.college AS author_college, u.grade AS author_grade,
                   c.name AS category_name,
                   EXISTS (
                       SELECT 1 FROM likes current_like
                       WHERE current_like.user_id = ?
                         AND current_like.target_id = p.id
                         AND current_like.target_type = 'post'
                   ) AS liked,
                   EXISTS (
                       SELECT 1 FROM favorites current_favorite
                       WHERE current_favorite.user_id = ?
                         AND current_favorite.target_id = p.id
                         AND current_favorite.target_type = 'post'
                   ) AS favorited
            FROM posts p
            JOIN users u ON u.id = p.user_id
            LEFT JOIN categories c ON c.id = p.category_id
            WHERE p.status = 1
            """;

    /**
     * 查询帖子列表。
     *
     * @param tab 参数 `tab`
     * @param currentUserId 当前用户编号
     * @param keyword 搜索关键字
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Post> findPosts(String tab, Long currentUserId, String keyword)
            throws SQLException {
        boolean categoryTab = "study".equals(tab)
                || "life".equals(tab)
                || "trade".equals(tab);
        boolean hasKeyword = keyword != null;
        StringBuilder sql = new StringBuilder(POST_SELECT);
        if (categoryTab) {
            sql.append('\n').append(CATEGORY_FILTER);
        }
        if (hasKeyword) {
            sql.append("""

                    AND (p.title LIKE ? OR p.content LIKE ? OR p.topic LIKE ?)
                    """);
        }
        sql.append('\n').append("hot".equals(tab) ? HOT_ORDER : LATEST_ORDER);

        List<Post> posts = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {
            int index = 1;
            long userId = currentUserId == null ? 0L : currentUserId;
            statement.setLong(index++, userId);
            statement.setLong(index++, userId);
            if (categoryTab) {
                statement.setString(index++, categoryName(tab));
            }
            if (hasKeyword) {
                String pattern = "%" + keyword + "%";
                statement.setString(index++, pattern);
                statement.setString(index++, pattern);
                statement.setString(index, pattern);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        }
        return posts;
    }

    /**
     * 根据输入计算并返回 `categoryName` 的处理结果。
     *
     * @param tab 参数 `tab`
     * @return 方法处理结果
     */
    private String categoryName(String tab) {
        return switch (tab) {
            case "study" -> "学习交流";
            case "trade" -> "二手交易";
            default -> "校园生活";
        };
    }

    /**
     * 查询公告列表。
     *
     * @param keyword 搜索关键字
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Notice> findNotices(String keyword) throws SQLException {
        boolean hasKeyword = keyword != null;
        StringBuilder sql = new StringBuilder("""
                SELECT n.id, n.title, n.content, n.type, n.is_top, n.created_at
                FROM notices n
                WHERE n.status = 1
                """);
        if (hasKeyword) {
            sql.append("""

                    AND (n.title LIKE ? OR n.content LIKE ?)
                    """);
        }
        sql.append('\n').append(NOTICE_ORDER);

        List<Notice> notices = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {
            if (hasKeyword) {
                String pattern = "%" + keyword + "%";
                statement.setString(1, pattern);
                statement.setString(2, pattern);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    notices.add(mapNotice(resultSet));
                }
            }
        }
        return notices;
    }

    /**
     * 将数据库结果映射为帖子。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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
        post.setLiked(resultSet.getBoolean("liked"));
        post.setFavorited(resultSet.getBoolean("favorited"));
        return post;
    }

    /**
     * 将数据库结果映射为公告。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private Notice mapNotice(ResultSet resultSet) throws SQLException {
        Notice notice = new Notice();
        notice.setId(resultSet.getLong("id"));
        notice.setTitle(resultSet.getString("title"));
        notice.setContent(resultSet.getString("content"));
        notice.setType(resultSet.getString("type"));
        notice.setTop(resultSet.getInt("is_top") == 1);
        notice.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        return notice;
    }

    /**
     * 转换为`LocalDateTime`。
     *
     * @param timestamp 数据库时间戳
     * @return 方法处理结果
     */
    private java.time.LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
