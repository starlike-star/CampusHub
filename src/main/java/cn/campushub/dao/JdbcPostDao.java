package cn.campushub.dao;

import cn.campushub.model.Category;
import cn.campushub.model.Comment;
import cn.campushub.model.CommentCreateResult;
import cn.campushub.model.Post;
import cn.campushub.model.PostToggleResult;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 使用 JDBC 实现帖子数据的查询与持久化操作。
 */
public class JdbcPostDao implements PostDao {
    public static final String HOME_POST_SQL = """
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
            ORDER BY p.created_at DESC
            """;

    private static final String POST_BY_ID_SQL = """
            SELECT p.id, p.user_id, p.category_id, p.title, p.content, p.images,
                   p.topic, p.like_count, p.comment_count, p.favorite_count,
                   p.view_count, p.status, p.created_at, p.updated_at,
                   u.avatar AS author_avatar, u.nickname AS author_nickname,
                   u.college AS author_college, u.grade AS author_grade,
                   c.name AS category_name,
                   0 AS liked, 0 AS favorited
            FROM posts p
            JOIN users u ON u.id = p.user_id
            LEFT JOIN categories c ON c.id = p.category_id
            WHERE p.id = ? AND p.status = 1
            LIMIT 1
            """;

    /**
     * 查询`ActivePosts`。
     *
     * @param currentUserId 当前用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Post> findActivePosts(Long currentUserId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(HOME_POST_SQL)) {
            long userId = currentUserId == null ? 0L : currentUserId;
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        }
        return posts;
    }

    /**
     * 查询`ActivePostById`。
     *
     * @param postId 帖子编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Optional<Post> findActivePostById(long postId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(POST_BY_ID_SQL)) {
            statement.setLong(1, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapPost(resultSet)) : Optional.empty();
            }
        }
    }

    /**
     * 查询`ActivePostCategories`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Category> findActivePostCategories() throws SQLException {
        String sql = """
                SELECT id, name, type, description, sort_order, status, created_at
                FROM categories
                WHERE type = 'post' AND status = 1
                ORDER BY sort_order ASC, id ASC
                """;
        List<Category> categories = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categories.add(mapCategory(resultSet));
            }
        }
        return categories;
    }

    /**
     * 判断是否`ActivePostCategory`。
     *
     * @param categoryId 分类编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean isActivePostCategory(long categoryId) throws SQLException {
        String sql = """
                SELECT 1 FROM categories
                WHERE id = ? AND type = 'post' AND status = 1
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 创建`JdbcPost`。
     *
     * @param post 帖子数据
     * @return 新建数据的编号
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public long create(Post post) throws SQLException {
        String sql = """
                INSERT INTO posts
                    (user_id, category_id, title, content, images, topic,
                     like_count, comment_count, favorite_count, view_count, status)
                VALUES (?, ?, ?, ?, ?, ?, 0, 0, 0, 0, 1)
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, post.getUserId());
            statement.setLong(2, post.getCategoryId());
            statement.setString(3, post.getTitle());
            statement.setString(4, post.getContent());
            statement.setString(5, post.getImages());
            statement.setString(6, post.getTopic());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("创建帖子后未获得主键");
                }
                return keys.getLong(1);
            }
        }
    }

    /**
     * 增加`ViewCount`。
     *
     * @param postId 帖子编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean incrementViewCount(long postId) throws SQLException {
        String sql = """
                UPDATE posts
                SET view_count = COALESCE(view_count, 0) + 1
                WHERE id = ? AND status = 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, postId);
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * 查询`ActiveComments`。
     *
     * @param postId 帖子编号
     * @param currentUserId 当前用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Comment> findActiveComments(long postId, Long currentUserId)
            throws SQLException {
        String sql = """
                SELECT c.id, c.post_id, c.user_id, c.content, c.like_count,
                       c.status, c.created_at,
                       u.avatar AS author_avatar, u.nickname AS author_nickname,
                       u.college AS author_college, u.grade AS author_grade,
                       EXISTS(
                           SELECT 1 FROM likes cl
                           WHERE cl.user_id = ? AND cl.target_id = c.id
                             AND cl.target_type = 'comment'
                       ) AS liked
                FROM comments c
                JOIN users u ON u.id = c.user_id
                WHERE c.post_id = ? AND c.status = 1
                ORDER BY c.created_at ASC
                """;
        List<Comment> comments = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, currentUserId == null ? 0L : currentUserId);
            statement.setLong(2, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    comments.add(mapComment(resultSet));
                }
            }
        }
        return comments;
    }

    /**
     * 新增评论。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @param content 正文内容
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public CommentCreateResult addComment(long postId, long userId, String content)
            throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!lockActivePost(connection, postId)) {
                    connection.rollback();
                    return null;
                }
                long commentId;
                try (PreparedStatement insert = connection.prepareStatement("""
                        INSERT INTO comments
                            (post_id, user_id, content, like_count, status)
                        VALUES (?, ?, ?, 0, 1)
                        """, Statement.RETURN_GENERATED_KEYS)) {
                    insert.setLong(1, postId);
                    insert.setLong(2, userId);
                    insert.setString(3, content);
                    insert.executeUpdate();
                    try (ResultSet keys = insert.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("创建评论后未获得主键");
                        }
                        commentId = keys.getLong(1);
                    }
                }
                try (PreparedStatement update = connection.prepareStatement("""
                        UPDATE posts
                        SET comment_count = COALESCE(comment_count, 0) + 1
                        WHERE id = ?
                        """)) {
                    update.setLong(1, postId);
                    update.executeUpdate();
                }
                Comment comment = findCreatedComment(connection, commentId);
                int commentCount = findPostCount(connection, postId, "comment_count");
                connection.commit();
                return new CommentCreateResult(comment, commentCount);
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 切换帖子点赞。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public PostToggleResult togglePostLike(long postId, long userId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!lockActivePost(connection, postId)) {
                    connection.rollback();
                    throw new SQLException("帖子不存在或不可操作");
                }

                boolean liked = hasPostLike(connection, postId, userId);
                if (liked) {
                    try (PreparedStatement delete = connection.prepareStatement("""
                            DELETE FROM likes
                            WHERE user_id = ? AND target_id = ? AND target_type = 'post'
                            """)) {
                        delete.setLong(1, userId);
                        delete.setLong(2, postId);
                        delete.executeUpdate();
                    }
                    try (PreparedStatement update = connection.prepareStatement("""
                            UPDATE posts
                            SET like_count = GREATEST(COALESCE(like_count, 0) - 1, 0)
                            WHERE id = ?
                            """)) {
                        update.setLong(1, postId);
                        update.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insert = connection.prepareStatement("""
                            INSERT INTO likes (user_id, target_id, target_type)
                            VALUES (?, ?, 'post')
                            """)) {
                        insert.setLong(1, userId);
                        insert.setLong(2, postId);
                        insert.executeUpdate();
                    }
                    try (PreparedStatement update = connection.prepareStatement("""
                            UPDATE posts
                            SET like_count = COALESCE(like_count, 0) + 1
                            WHERE id = ?
                            """)) {
                        update.setLong(1, postId);
                        update.executeUpdate();
                    }
                }
                int likeCount = findPostCount(connection, postId, "like_count");
                connection.commit();
                return new PostToggleResult(!liked, likeCount);
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 判断是否具有帖子点赞。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean hasPostLike(long postId, long userId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            return hasPostLike(connection, postId, userId);
        }
    }

    /**
     * 判断是否具有帖子收藏。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean hasPostFavorite(long postId, long userId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            return hasPostFavorite(connection, postId, userId);
        }
    }

    /**
     * 切换帖子收藏。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public PostToggleResult togglePostFavorite(long postId, long userId)
            throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!lockActivePost(connection, postId)) {
                    connection.rollback();
                    throw new SQLException("帖子不存在或不可操作");
                }
                boolean favorited = hasPostFavorite(connection, postId, userId);
                if (favorited) {
                    try (PreparedStatement delete = connection.prepareStatement("""
                            DELETE FROM favorites
                            WHERE user_id = ? AND target_id = ? AND target_type = 'post'
                            """)) {
                        delete.setLong(1, userId);
                        delete.setLong(2, postId);
                        delete.executeUpdate();
                    }
                    try (PreparedStatement update = connection.prepareStatement("""
                            UPDATE posts
                            SET favorite_count =
                                GREATEST(COALESCE(favorite_count, 0) - 1, 0)
                            WHERE id = ?
                            """)) {
                        update.setLong(1, postId);
                        update.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insert = connection.prepareStatement("""
                            INSERT INTO favorites (user_id, target_id, target_type)
                            VALUES (?, ?, 'post')
                            """)) {
                        insert.setLong(1, userId);
                        insert.setLong(2, postId);
                        insert.executeUpdate();
                    }
                    try (PreparedStatement update = connection.prepareStatement("""
                            UPDATE posts
                            SET favorite_count = COALESCE(favorite_count, 0) + 1
                            WHERE id = ?
                            """)) {
                        update.setLong(1, postId);
                        update.executeUpdate();
                    }
                }
                int favoriteCount = findPostCount(connection, postId, "favorite_count");
                connection.commit();
                return new PostToggleResult(!favorited, favoriteCount);
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 切换评论点赞。
     *
     * @param commentId 评论编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public PostToggleResult toggleCommentLike(long commentId, long userId)
            throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!lockActiveComment(connection, commentId)) {
                    connection.rollback();
                    throw new SQLException("评论不存在或不可操作");
                }
                boolean liked = hasCommentLike(connection, commentId, userId);
                if (liked) {
                    try (PreparedStatement delete = connection.prepareStatement("""
                            DELETE FROM likes
                            WHERE user_id = ? AND target_id = ?
                              AND target_type = 'comment'
                            """)) {
                        delete.setLong(1, userId);
                        delete.setLong(2, commentId);
                        delete.executeUpdate();
                    }
                    try (PreparedStatement update = connection.prepareStatement("""
                            UPDATE comments
                            SET like_count = GREATEST(COALESCE(like_count, 0) - 1, 0)
                            WHERE id = ?
                            """)) {
                        update.setLong(1, commentId);
                        update.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insert = connection.prepareStatement("""
                            INSERT INTO likes (user_id, target_id, target_type)
                            VALUES (?, ?, 'comment')
                            """)) {
                        insert.setLong(1, userId);
                        insert.setLong(2, commentId);
                        insert.executeUpdate();
                    }
                    try (PreparedStatement update = connection.prepareStatement("""
                            UPDATE comments
                            SET like_count = COALESCE(like_count, 0) + 1
                            WHERE id = ?
                            """)) {
                        update.setLong(1, commentId);
                        update.executeUpdate();
                    }
                }
                int count = findCommentLikeCount(connection, commentId);
                connection.commit();
                return new PostToggleResult(!liked, count);
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 更新`OwnedPost`。
     *
     * @param post 帖子数据
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Optional<Post> updateOwnedPost(Post post) throws SQLException {
        String updateSql = """
                UPDATE posts
                SET title = ?, content = ?, topic = ?, category_id = ?, images = ?
                WHERE id = ? AND user_id = ? AND status = 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement update = connection.prepareStatement(updateSql)) {
            update.setString(1, post.getTitle());
            update.setString(2, post.getContent());
            update.setString(3, post.getTopic());
            update.setLong(4, post.getCategoryId());
            update.setString(5, post.getImages());
            update.setLong(6, post.getId());
            update.setLong(7, post.getUserId());
            if (update.executeUpdate() != 1) {
                return Optional.empty();
            }
        }
        return findActivePostById(post.getId());
    }

    /**
     * 删除`OwnedPost`。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean deleteOwnedPost(long postId, long userId) throws SQLException {
        String sql = """
                UPDATE posts
                SET status = 0
                WHERE id = ? AND user_id = ? AND status = 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, postId);
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * 判断是否具有帖子点赞。
     *
     * @param connection 数据库连接
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean hasPostLike(Connection connection, long postId, long userId)
            throws SQLException {
        String sql = """
                SELECT 1 FROM likes
                WHERE user_id = ? AND target_id = ? AND target_type = 'post'
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 判断是否具有帖子收藏。
     *
     * @param connection 数据库连接
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean hasPostFavorite(Connection connection, long postId, long userId)
            throws SQLException {
        String sql = """
                SELECT 1 FROM favorites
                WHERE user_id = ? AND target_id = ? AND target_type = 'post'
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 根据输入计算并返回 `lockActiveComment` 的处理结果。
     *
     * @param connection 数据库连接
     * @param commentId 评论编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean lockActiveComment(Connection connection, long commentId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT id
                FROM comments
                WHERE id = ? AND status = 1
                FOR UPDATE
                """)) {
            statement.setLong(1, commentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 判断是否具有评论点赞。
     *
     * @param connection 数据库连接
     * @param commentId 评论编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean hasCommentLike(
            Connection connection,
            long commentId,
            long userId
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT 1
                FROM likes
                WHERE user_id = ? AND target_id = ? AND target_type = 'comment'
                LIMIT 1
                """)) {
            statement.setLong(1, userId);
            statement.setLong(2, commentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 查询评论点赞数量。
     *
     * @param connection 数据库连接
     * @param commentId 评论编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private int findCommentLikeCount(Connection connection, long commentId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT COALESCE(like_count, 0)
                FROM comments
                WHERE id = ?
                """)) {
            statement.setLong(1, commentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("评论不存在");
                }
                return resultSet.getInt(1);
            }
        }
    }

    /**
     * 根据输入计算并返回 `lockActivePost` 的处理结果。
     *
     * @param connection 数据库连接
     * @param postId 帖子编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean lockActivePost(Connection connection, long postId) throws SQLException {
        String sql = "SELECT status FROM posts WHERE id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt("status") == 1;
            }
        }
    }

    /**
     * 查询帖子数量。
     *
     * @param connection 数据库连接
     * @param postId 帖子编号
     * @param column 参数 `column`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private int findPostCount(Connection connection, long postId, String column)
            throws SQLException {
        String sql = "SELECT " + column + " FROM posts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("帖子不存在");
                }
                return resultSet.getInt(column);
            }
        }
    }

    /**
     * 查询`CreatedComment`。
     *
     * @param connection 数据库连接
     * @param commentId 评论编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private Comment findCreatedComment(Connection connection, long commentId)
            throws SQLException {
        String sql = """
                SELECT c.id, c.post_id, c.user_id, c.content, c.like_count,
                       c.status, c.created_at,
                       u.avatar AS author_avatar, u.nickname AS author_nickname,
                       u.college AS author_college, u.grade AS author_grade
                FROM comments c
                JOIN users u ON u.id = c.user_id
                WHERE c.id = ?
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, commentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("评论不存在");
                }
                return mapComment(resultSet);
            }
        }
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
     * 将数据库结果映射为分类。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private Category mapCategory(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getLong("id"));
        category.setName(resultSet.getString("name"));
        category.setType(resultSet.getString("type"));
        category.setDescription(resultSet.getString("description"));
        category.setSortOrder(resultSet.getInt("sort_order"));
        category.setStatus(resultSet.getInt("status"));
        category.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        return category;
    }

    /**
     * 将数据库结果映射为评论。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private Comment mapComment(ResultSet resultSet) throws SQLException {
        Comment comment = new Comment();
        comment.setId(resultSet.getLong("id"));
        comment.setPostId(resultSet.getLong("post_id"));
        comment.setUserId(resultSet.getLong("user_id"));
        comment.setContent(resultSet.getString("content"));
        comment.setLikeCount(resultSet.getInt("like_count"));
        comment.setStatus(resultSet.getInt("status"));
        comment.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        comment.setAuthorAvatar(resultSet.getString("author_avatar"));
        comment.setAuthorNickname(resultSet.getString("author_nickname"));
        comment.setAuthorCollege(resultSet.getString("author_college"));
        comment.setAuthorGrade(resultSet.getString("author_grade"));
        try {
            comment.setLiked(resultSet.getBoolean("liked"));
        } catch (SQLException ignored) {
            comment.setLiked(false);
        }
        return comment;
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
