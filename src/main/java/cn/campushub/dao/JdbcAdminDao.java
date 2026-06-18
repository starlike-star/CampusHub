package cn.campushub.dao;

import cn.campushub.model.ReportNotificationTarget;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 使用 JDBC 实现后台管理数据的查询与持久化操作。
 */
public class JdbcAdminDao implements AdminDao {
    /**
     * 查询后台概览并返回结果。
     *
     * @return 按键组织的结果数据
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Map<String, Long> dashboard() throws SQLException {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("users", count("SELECT COUNT(*) FROM users"));
        stats.put("todayUsers", count(
                "SELECT COUNT(*) FROM users WHERE DATE(created_at) = CURDATE()"));
        stats.put("posts", count("SELECT COUNT(*) FROM posts WHERE status = 1"));
        stats.put("todayPosts", count(
                "SELECT COUNT(*) FROM posts WHERE DATE(created_at) = CURDATE()"));
        stats.put("goods", count(
                "SELECT COUNT(*) FROM goods WHERE status <> 'off_shelf'"));
        stats.put("todayGoods", count(
                "SELECT COUNT(*) FROM goods WHERE DATE(created_at) = CURDATE()"));
        stats.put("lostFound", count(
                "SELECT COUNT(*) FROM lost_found WHERE status <> 'closed'"));
        stats.put("activities", count("SELECT COUNT(*) FROM activities"));
        stats.put("pendingReports", count(
                "SELECT COUNT(*) FROM reports WHERE status = 'pending'"));
        stats.put("todayCheckins", count(
                "SELECT COUNT(DISTINCT user_id) FROM checkins "
                        + "WHERE checkin_date = CURDATE()"));
        return stats;
    }

    /**
     * 查询用户列表。
     *
     * @param keyword 搜索关键字
     * @param role 参数 `role`
     * @param status 业务状态
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Map<String, Object>> findUsers(
            String keyword,
            String role,
            Integer status
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT id, username, nickname, student_no, college, major, grade,
                       email, phone, role, status, created_at
                FROM users
                WHERE 1 = 1
                """);
        List<Object> parameters = new ArrayList<>();
        if (keyword != null) {
            sql.append("""
                     AND (username LIKE ? OR nickname LIKE ? OR student_no LIKE ?
                          OR college LIKE ?)
                    """);
            addLike(parameters, keyword, 4);
        }
        if (role != null) {
            sql.append(" AND role = ?");
            parameters.add(role);
        }
        if (status != null) {
            sql.append(" AND status = ?");
            parameters.add(status);
        }
        sql.append(" ORDER BY created_at DESC, id DESC");
        return query(sql.toString(), parameters);
    }

    /**
     * 查询帖子列表。
     *
     * @param keyword 搜索关键字
     * @param status 业务状态
     * @param categoryId 分类编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Map<String, Object>> findPosts(
            String keyword,
            Integer status,
            Long categoryId
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT p.id, p.title, p.content, u.nickname AS author_nickname,
                       c.name AS category_name, p.topic, p.like_count,
                       p.comment_count, p.favorite_count, p.view_count,
                       p.status, p.created_at
                FROM posts p
                JOIN users u ON u.id = p.user_id
                LEFT JOIN categories c ON c.id = p.category_id
                WHERE 1 = 1
                """);
        List<Object> parameters = new ArrayList<>();
        if (keyword != null) {
            sql.append("""
                     AND (p.title LIKE ? OR p.content LIKE ? OR u.nickname LIKE ?)
                    """);
            addLike(parameters, keyword, 3);
        }
        if (status != null) {
            sql.append(" AND p.status = ?");
            parameters.add(status);
        }
        if (categoryId != null) {
            sql.append(" AND p.category_id = ?");
            parameters.add(categoryId);
        }
        sql.append(" ORDER BY p.created_at DESC, p.id DESC");
        return query(sql.toString(), parameters);
    }

    /**
     * 查询商品。
     *
     * @param keyword 搜索关键字
     * @param status 业务状态
     * @param tradeMethod 参数 `tradeMethod`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Map<String, Object>> findGoods(
            String keyword,
            String status,
            String tradeMethod
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT g.id, g.title, g.description, g.price,
                       u.nickname AS seller_nickname,
                       c.name AS category_name, g.condition_level, g.trade_place,
                       g.trade_method, g.status, g.created_at
                FROM goods g
                JOIN users u ON u.id = g.user_id
                LEFT JOIN categories c ON c.id = g.category_id
                WHERE 1 = 1
                """);
        List<Object> parameters = new ArrayList<>();
        if (keyword != null) {
            sql.append("""
                     AND (g.title LIKE ? OR g.description LIKE ? OR u.nickname LIKE ?)
                    """);
            addLike(parameters, keyword, 3);
        }
        if (status != null) {
            sql.append(" AND g.status = ?");
            parameters.add(status);
        }
        if (tradeMethod != null) {
            sql.append(" AND g.trade_method = ?");
            parameters.add(tradeMethod);
        }
        sql.append(" ORDER BY g.created_at DESC, g.id DESC");
        return query(sql.toString(), parameters);
    }

    /**
     * 查询`LostFound`。
     *
     * @param keyword 搜索关键字
     * @param type 参数 `type`
     * @param status 业务状态
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Map<String, Object>> findLostFound(
            String keyword,
            String type,
            String status
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT lf.id, lf.type, lf.item_name, lf.title, lf.description,
                       lf.place,
                       lf.status, u.nickname AS author_nickname, lf.created_at
                FROM lost_found lf
                JOIN users u ON u.id = lf.user_id
                WHERE 1 = 1
                """);
        List<Object> parameters = new ArrayList<>();
        if (keyword != null) {
            sql.append("""
                     AND (lf.title LIKE ? OR lf.item_name LIKE ? OR lf.place LIKE ?
                          OR u.nickname LIKE ?)
                    """);
            addLike(parameters, keyword, 4);
        }
        if (type != null) {
            sql.append(" AND lf.type = ?");
            parameters.add(type);
        }
        if (status != null) {
            sql.append(" AND lf.status = ?");
            parameters.add(status);
        }
        sql.append(" ORDER BY lf.created_at DESC, lf.id DESC");
        return query(sql.toString(), parameters);
    }

    /**
     * 查询活动列表。
     *
     * @param keyword 搜索关键字
     * @param status 业务状态
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Map<String, Object>> findActivities(
            String keyword,
            String status
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT a.id, a.title, a.content, a.location, a.start_time, a.deadline,
                       a.current_members, a.max_members, a.status,
                       u.nickname AS author_nickname, a.created_at
                FROM activities a
                LEFT JOIN users u ON u.id = a.created_by
                WHERE 1 = 1
                """);
        List<Object> parameters = new ArrayList<>();
        if (keyword != null) {
            sql.append("""
                     AND (a.title LIKE ? OR a.location LIKE ? OR u.nickname LIKE ?)
                    """);
            addLike(parameters, keyword, 3);
        }
        if (status != null) {
            sql.append(" AND a.status = ?");
            parameters.add(status);
        }
        sql.append(" ORDER BY a.created_at DESC, a.id DESC");
        return query(sql.toString(), parameters);
    }

    /**
     * 查询公告列表。
     *
     * @param type 参数 `type`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Map<String, Object>> findNotices(String type) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT n.id, n.title, n.content, n.type, n.is_top, n.status,
                       u.nickname AS creator_nickname, n.created_at, n.updated_at
                FROM notices n
                LEFT JOIN users u ON u.id = n.created_by
                WHERE 1 = 1
                """);
        List<Object> parameters = new ArrayList<>();
        if (type != null) {
            sql.append(" AND n.type = ?");
            parameters.add(type);
        }
        sql.append(" ORDER BY n.is_top DESC, n.created_at DESC, n.id DESC");
        return query(sql.toString(), parameters);
    }

    /**
     * 查询举报记录。
     *
     * @param status 业务状态
     * @param targetType 参数 `targetType`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Map<String, Object>> findReports(
            String status,
            String targetType
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT r.id, r.target_id, r.target_type, r.reason, r.status,
                       reporter.nickname AS reporter_nickname,
                       handler.nickname AS handler_nickname,
                       r.created_at, r.handled_at
                FROM reports r
                JOIN users reporter ON reporter.id = r.user_id
                LEFT JOIN users handler ON handler.id = r.handled_by
                WHERE 1 = 1
                """);
        List<Object> parameters = new ArrayList<>();
        if (status != null) {
            sql.append(" AND r.status = ?");
            parameters.add(status);
        }
        if (targetType != null) {
            sql.append(" AND r.target_type = ?");
            parameters.add(targetType);
        }
        sql.append(" ORDER BY r.created_at DESC, r.id DESC");
        return query(sql.toString(), parameters);
    }

    /**
     * 查询`Categories`。
     *
     * @param type 参数 `type`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<Map<String, Object>> findCategories(String type) throws SQLException {
        return query(
                "SELECT id, name FROM categories "
                        + "WHERE type = ? AND status = 1 ORDER BY sort_order, id",
                List.of(type)
        );
    }

    /**
     * 更新用户状态。
     *
     * @param userId 用户编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean updateUserStatus(long userId, int status) throws SQLException {
        return update(
                "UPDATE users SET status = ? WHERE id = ? AND status != 2",
                status,
                userId
        );
    }

    /**
     * 重置用户密码。
     *
     * @param userId 用户编号
     * @param passwordHash 参数 `passwordHash`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean resetUserPassword(long userId, String passwordHash)
            throws SQLException {
        return update(
                "UPDATE users SET password = ? WHERE id = ? AND status != 2",
                passwordHash,
                userId
        );
    }

    /**
     * 更新帖子状态。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean updatePostStatus(long id, int status) throws SQLException {
        return update("UPDATE posts SET status = ? WHERE id = ?", status, id);
    }

    /**
     * 更新商品状态。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean updateGoodsStatus(long id, String status) throws SQLException {
        return update("UPDATE goods SET status = ? WHERE id = ?", status, id);
    }

    /**
     * 更新`LostFoundStatus`。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean updateLostFoundStatus(long id, String status)
            throws SQLException {
        return update("UPDATE lost_found SET status = ? WHERE id = ?", status, id);
    }

    /**
     * 更新活动状态。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean updateActivityStatus(long id, String status)
            throws SQLException {
        return update("UPDATE activities SET status = ? WHERE id = ?", status, id);
    }

    /**
     * 创建公告。
     *
     * @param title 标题
     * @param content 正文内容
     * @param type 参数 `type`
     * @param createdBy 参数 `createdBy`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean createNotice(
            String title,
            String content,
            String type,
            long createdBy
    ) throws SQLException {
        return update("""
                INSERT INTO notices
                    (title, content, type, is_top, status, created_by)
                VALUES (?, ?, ?, 0, 1, ?)
                """, title, content, type, createdBy);
    }

    /**
     * 更新公告。
     *
     * @param id 业务数据编号
     * @param title 标题
     * @param content 正文内容
     * @param type 参数 `type`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean updateNotice(
            long id,
            String title,
            String content,
            String type
    ) throws SQLException {
        return update("""
                UPDATE notices
                SET title = ?, content = ?, type = ?
                WHERE id = ?
                """, title, content, type, id);
    }

    /**
     * 更新公告状态。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean updateNoticeStatus(long id, int status) throws SQLException {
        return update("UPDATE notices SET status = ? WHERE id = ?", status, id);
    }

    /**
     * 更新`NoticeTop`。
     *
     * @param id 业务数据编号
     * @param isTop 是否`Top`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean updateNoticeTop(long id, int isTop) throws SQLException {
        return update("UPDATE notices SET is_top = ? WHERE id = ?", isTop, id);
    }

    /**
     * 处理举报。
     *
     * @param reportId 举报编号
     * @param adminId 管理员编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean handleReport(long reportId, long adminId) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                ReportTarget target = lockPendingReport(connection, reportId);
                if (target == null) {
                    connection.rollback();
                    return false;
                }
                moderateTarget(connection, target);
                boolean updated = update(
                        connection,
                        """
                        UPDATE reports
                        SET status = 'handled', handled_by = ?, handled_at = NOW()
                        WHERE id = ? AND status = 'pending'
                        """,
                        adminId,
                        reportId
                );
                if (!updated) {
                    connection.rollback();
                    return false;
                }
                connection.commit();
                return true;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 驳回举报。
     *
     * @param reportId 举报编号
     * @param adminId 管理员编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean rejectReport(long reportId, long adminId) throws SQLException {
        return update("""
                UPDATE reports
                SET status = 'rejected', handled_by = ?, handled_at = NOW()
                WHERE id = ? AND status = 'pending'
                """, adminId, reportId);
    }

    /**
     * 查询`ReportNotificationTarget`。
     *
     * @param reportId 举报编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Optional<ReportNotificationTarget> findReportNotificationTarget(long reportId)
            throws SQLException {
        String sql = """
                SELECT r.user_id AS reporter_id,
                       CASE r.target_type
                           WHEN 'post' THEN (
                               SELECT p.user_id FROM posts p WHERE p.id = r.target_id
                           )
                           WHEN 'comment' THEN (
                               SELECT c.user_id FROM comments c WHERE c.id = r.target_id
                           )
                           WHEN 'goods' THEN (
                               SELECT g.user_id FROM goods g WHERE g.id = r.target_id
                           )
                           WHEN 'lost_found' THEN (
                               SELECT lf.user_id
                               FROM lost_found lf
                               WHERE lf.id = r.target_id
                           )
                       END AS owner_id
                FROM reports r
                WHERE r.id = ?
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reportId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                long ownerId = resultSet.getLong("owner_id");
                Long nullableOwnerId = resultSet.wasNull() ? null : ownerId;
                return Optional.of(new ReportNotificationTarget(
                        resultSet.getLong("reporter_id"),
                        nullableOwnerId
                ));
            }
        }
    }

    /**
     * 统计`JdbcAdmin`。
     *
     * @param sql 参数 `sql`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private long count(String sql) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getLong(1) : 0L;
        }
    }

    /**
     * 查询`query`并返回结果。
     *
     * @param sql 参数 `sql`
     * @param parameters 参数 `parameters`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    private List<Map<String, Object>> query(
            String sql,
            List<Object> parameters
    ) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters.toArray());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Map<String, Object>> rows = new ArrayList<>();
                ResultSetMetaData metadata = resultSet.getMetaData();
                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        row.put(metadata.getColumnLabel(i), resultSet.getObject(i));
                    }
                    rows.add(row);
                }
                return rows;
            }
        }
    }

    /**
     * 更新`JdbcAdmin`。
     *
     * @param sql 参数 `sql`
     * @param parameters 参数 `parameters`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean update(String sql, Object... parameters) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            return update(connection, sql, parameters);
        }
    }

    /**
     * 更新`JdbcAdmin`。
     *
     * @param connection 数据库连接
     * @param sql 参数 `sql`
     * @param parameters 参数 `parameters`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean update(
            Connection connection,
            String sql,
            Object... parameters
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * 处理 `bind` 对应的业务流程。
     *
     * @param statement 预编译 SQL 语句
     * @param parameters 参数 `parameters`
     * @throws SQLException 数据库访问失败时抛出
     */
    private void bind(PreparedStatement statement, Object... parameters)
            throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object value = parameters[i];
            if (value == null) {
                statement.setNull(i + 1, Types.NULL);
            } else {
                statement.setObject(i + 1, value);
            }
        }
    }

    /**
     * 新增点赞。
     *
     * @param parameters 参数 `parameters`
     * @param keyword 搜索关键字
     * @param count 参数 `count`
     */
    private void addLike(List<Object> parameters, String keyword, int count) {
        String value = "%" + keyword + "%";
        for (int i = 0; i < count; i++) {
            parameters.add(value);
        }
    }

    /**
     * 根据输入计算并返回 `lockPendingReport` 的处理结果。
     *
     * @param connection 数据库连接
     * @param reportId 举报编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private ReportTarget lockPendingReport(Connection connection, long reportId)
            throws SQLException {
        String sql = """
                SELECT target_id, target_type
                FROM reports
                WHERE id = ? AND status = 'pending'
                FOR UPDATE
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reportId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new ReportTarget(
                        resultSet.getLong("target_id"),
                        resultSet.getString("target_type")
                );
            }
        }
    }

    /**
     * 处理 `moderateTarget` 对应的业务流程。
     *
     * @param connection 数据库连接
     * @param target 参数 `target`
     * @throws SQLException 数据库访问失败时抛出
     */
    private void moderateTarget(Connection connection, ReportTarget target)
            throws SQLException {
        String sql = switch (target.type()) {
            case "post" -> "UPDATE posts SET status = 0 WHERE id = ?";
            case "comment" -> "UPDATE comments SET status = 0 WHERE id = ?";
            case "goods" -> "UPDATE goods SET status = 'off_shelf' WHERE id = ?";
            case "lost_found" -> "UPDATE lost_found SET status = 'closed' WHERE id = ?";
            default -> throw new SQLException("不支持的举报目标类型");
        };
        update(connection, sql, target.id());
    }

    /**
     * 根据输入计算并返回 `ReportTarget` 的处理结果。
     *
     * @param id 业务数据编号
     * @param type 参数 `type`
     * @return 方法处理结果
     */
    private record ReportTarget(long id, String type) {
    }
}
