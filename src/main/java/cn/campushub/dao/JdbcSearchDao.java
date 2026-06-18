package cn.campushub.dao;

import cn.campushub.model.SearchResultVO;
import cn.campushub.util.JdbcUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用 JDBC 实现全站搜索数据的查询与持久化操作。
 */
public class JdbcSearchDao implements SearchDao {
    private static final DateTimeFormatter EXTRA_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String COUNT_SQL = """
            SELECT 'post' AS result_type, COUNT(*) AS total
            FROM posts p
            LEFT JOIN users u ON u.id = p.user_id
            LEFT JOIN categories c ON c.id = p.category_id
            WHERE p.status = 1
              AND (p.title LIKE ? OR p.content LIKE ? OR p.topic LIKE ?
                   OR u.nickname LIKE ? OR c.name LIKE ?)
            UNION ALL
            SELECT 'goods', COUNT(*)
            FROM goods g
            LEFT JOIN users u ON u.id = g.user_id
            LEFT JOIN categories c ON c.id = g.category_id
            WHERE g.status != 'off_shelf'
              AND (g.title LIKE ? OR g.description LIKE ? OR g.trade_place LIKE ?
                   OR u.nickname LIKE ? OR c.name LIKE ?)
            UNION ALL
            SELECT 'lost_found', COUNT(*)
            FROM lost_found lf
            LEFT JOIN users u ON u.id = lf.user_id
            LEFT JOIN categories c ON c.id = lf.category_id
            WHERE lf.status != 'closed'
              AND (lf.title LIKE ? OR lf.item_name LIKE ? OR lf.description LIKE ?
                   OR lf.place LIKE ? OR u.nickname LIKE ? OR c.name LIKE ?)
            UNION ALL
            SELECT 'activity', COUNT(*)
            FROM activities a
            LEFT JOIN users u ON u.id = a.created_by
            WHERE a.status != 'finished'
              AND (a.title LIKE ? OR a.content LIKE ? OR a.location LIKE ?
                   OR u.nickname LIKE ?)
            UNION ALL
            SELECT 'notice', COUNT(*)
            FROM notices n
            WHERE n.status = 1
              AND (n.title LIKE ? OR n.content LIKE ? OR n.type LIKE ?)
            """;

    private static final String POST_SQL = """
            SELECT p.id, p.title, p.content, p.images, p.created_at,
                   p.like_count, p.comment_count, u.nickname AS author_name,
                   c.name AS category_name
            FROM posts p
            LEFT JOIN users u ON u.id = p.user_id
            LEFT JOIN categories c ON c.id = p.category_id
            WHERE p.status = 1
              AND (p.title LIKE ? OR p.content LIKE ? OR p.topic LIKE ?
                   OR u.nickname LIKE ? OR c.name LIKE ?)
            ORDER BY p.created_at DESC, p.id DESC
            LIMIT ?
            """;

    private static final String GOODS_SQL = """
            SELECT g.id, g.title, g.description, g.images, g.price,
                   g.trade_place, g.trade_method, g.status, g.created_at,
                   u.nickname AS author_name, c.name AS category_name
            FROM goods g
            LEFT JOIN users u ON u.id = g.user_id
            LEFT JOIN categories c ON c.id = g.category_id
            WHERE g.status != 'off_shelf'
              AND (g.title LIKE ? OR g.description LIKE ? OR g.trade_place LIKE ?
                   OR u.nickname LIKE ? OR c.name LIKE ?)
            ORDER BY g.created_at DESC, g.id DESC
            LIMIT ?
            """;

    private static final String LOST_FOUND_SQL = """
            SELECT lf.id, lf.type, lf.item_name, lf.title, lf.description,
                   lf.place, lf.images, lf.status, lf.created_at,
                   u.nickname AS author_name, c.name AS category_name
            FROM lost_found lf
            LEFT JOIN users u ON u.id = lf.user_id
            LEFT JOIN categories c ON c.id = lf.category_id
            WHERE lf.status != 'closed'
              AND (lf.title LIKE ? OR lf.item_name LIKE ? OR lf.description LIKE ?
                   OR lf.place LIKE ? OR u.nickname LIKE ? OR c.name LIKE ?)
            ORDER BY lf.created_at DESC, lf.id DESC
            LIMIT ?
            """;

    private static final String ACTIVITY_SQL = """
            SELECT a.id, a.title, a.content, a.cover_image, a.location,
                   a.start_time, a.current_members, a.max_members, a.status,
                   a.created_at, u.nickname AS author_name
            FROM activities a
            LEFT JOIN users u ON u.id = a.created_by
            WHERE a.status != 'finished'
              AND (a.title LIKE ? OR a.content LIKE ? OR a.location LIKE ?
                   OR u.nickname LIKE ?)
            ORDER BY a.created_at DESC, a.id DESC
            LIMIT ?
            """;

    private static final String NOTICE_SQL = """
            SELECT n.id, n.title, n.content, n.type, n.is_top, n.created_at
            FROM notices n
            WHERE n.status = 1
              AND (n.title LIKE ? OR n.content LIKE ? OR n.type LIKE ?)
            ORDER BY n.is_top DESC, n.created_at DESC, n.id DESC
            LIMIT ?
            """;

    /**
     * 统计`Matches`。
     *
     * @param keyword 搜索关键字
     * @return 按键组织的结果数据
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Map<String, Integer> countMatches(String keyword) throws SQLException {
        Map<String, Integer> counts = emptyCounts();
        String pattern = likePattern(keyword);
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_SQL)) {
            int index = 1;
            for (int occurrences : new int[]{5, 5, 6, 4, 3}) {
                index = setPattern(statement, index, pattern, occurrences);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    counts.put(
                            resultSet.getString("result_type"),
                            resultSet.getInt("total")
                    );
                }
            }
        }
        return counts;
    }

    /**
     * 搜索帖子列表。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<SearchResultVO> searchPosts(String keyword, int limit)
            throws SQLException {
        return query(POST_SQL, keyword, 5, limit, resultSet -> {
            String category = valueOr(resultSet.getString("category_name"), "未分类");
            String author = valueOr(resultSet.getString("author_name"), "校园用户");
            return new SearchResultVO(
                    resultSet.getLong("id"),
                    "post",
                    resultSet.getString("title"),
                    summary(resultSet.getString("content")),
                    firstImage(resultSet.getString("images")),
                    author,
                    "正常",
                    category + " · " + resultSet.getInt("like_count")
                            + " 赞 · " + resultSet.getInt("comment_count") + " 评论",
                    "/post/detail?id=" + resultSet.getLong("id"),
                    toLocalDateTime(resultSet.getTimestamp("created_at"))
            );
        });
    }

    /**
     * 搜索商品。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<SearchResultVO> searchGoods(String keyword, int limit)
            throws SQLException {
        return query(GOODS_SQL, keyword, 5, limit, resultSet -> {
            BigDecimal price = resultSet.getBigDecimal("price");
            String extraInfo = "¥" + (price == null ? "0.00" : price.toPlainString())
                    + " · " + valueOr(resultSet.getString("trade_place"), "地点待定")
                    + " · " + tradeMethodText(resultSet.getString("trade_method"));
            return new SearchResultVO(
                    resultSet.getLong("id"),
                    "goods",
                    resultSet.getString("title"),
                    summary(resultSet.getString("description")),
                    firstImage(resultSet.getString("images")),
                    valueOr(resultSet.getString("author_name"), "校园用户"),
                    goodsStatusText(resultSet.getString("status")),
                    extraInfo,
                    "/goods/detail?id=" + resultSet.getLong("id"),
                    toLocalDateTime(resultSet.getTimestamp("created_at"))
            );
        });
    }

    /**
     * 搜索`LostFound`。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<SearchResultVO> searchLostFound(String keyword, int limit)
            throws SQLException {
        return query(LOST_FOUND_SQL, keyword, 6, limit, resultSet -> {
            String itemType = "lost".equals(resultSet.getString("type"))
                    ? "失物" : "招领";
            return new SearchResultVO(
                    resultSet.getLong("id"),
                    "lost_found",
                    resultSet.getString("title"),
                    summary(resultSet.getString("description")),
                    firstImage(resultSet.getString("images")),
                    valueOr(resultSet.getString("author_name"), "校园用户"),
                    lostFoundStatusText(resultSet.getString("status")),
                    itemType + " · "
                            + valueOr(resultSet.getString("item_name"), "未命名物品")
                            + " · " + valueOr(resultSet.getString("place"), "地点未填写"),
                    "/lostfound/detail?id=" + resultSet.getLong("id"),
                    toLocalDateTime(resultSet.getTimestamp("created_at"))
            );
        });
    }

    /**
     * 搜索活动列表。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<SearchResultVO> searchActivities(String keyword, int limit)
            throws SQLException {
        return query(ACTIVITY_SQL, keyword, 4, limit, resultSet -> {
            int maxMembers = resultSet.getInt("max_members");
            String memberText = resultSet.getInt("current_members") + "/"
                    + (maxMembers == 0 ? "不限" : maxMembers) + " 人";
            LocalDateTime startTime =
                    toLocalDateTime(resultSet.getTimestamp("start_time"));
            return new SearchResultVO(
                    resultSet.getLong("id"),
                    "activity",
                    resultSet.getString("title"),
                    summary(resultSet.getString("content")),
                    resultSet.getString("cover_image"),
                    valueOr(resultSet.getString("author_name"), "校园用户"),
                    activityStatusText(resultSet.getString("status")),
                    valueOr(resultSet.getString("location"), "地点待定")
                            + " · " + formatStartTime(startTime)
                            + " · " + memberText,
                    "/activity/detail?id=" + resultSet.getLong("id"),
                    toLocalDateTime(resultSet.getTimestamp("created_at"))
            );
        });
    }

    /**
     * 搜索公告列表。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public List<SearchResultVO> searchNotices(String keyword, int limit)
            throws SQLException {
        return query(NOTICE_SQL, keyword, 3, limit, resultSet ->
                new SearchResultVO(
                        resultSet.getLong("id"),
                        "notice",
                        resultSet.getString("title"),
                        summary(resultSet.getString("content")),
                        null,
                        null,
                        resultSet.getBoolean("is_top") ? "置顶" : "公告",
                        noticeTypeText(resultSet.getString("type")),
                        "/notice/detail?id=" + resultSet.getLong("id"),
                        toLocalDateTime(resultSet.getTimestamp("created_at"))
                )
        );
    }

    /**
     * 查询`query`并返回结果。
     *
     * @param sql 参数 `sql`
     * @param keyword 搜索关键字
     * @param patternOccurrences 参数 `patternOccurrences`
     * @param limit 查询数量上限
     * @param mapper 参数 `mapper`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    private List<SearchResultVO> query(
            String sql,
            String keyword,
            int patternOccurrences,
            int limit,
            RowMapper mapper
    ) throws SQLException {
        List<SearchResultVO> results = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = setPattern(
                    statement,
                    1,
                    likePattern(keyword),
                    patternOccurrences
            );
            statement.setInt(index, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(mapper.map(resultSet));
                }
            }
        }
        return results;
    }

    /**
     * 设置`Pattern`。
     *
     * @param statement 预编译 SQL 语句
     * @param startIndex 参数 `startIndex`
     * @param pattern 参数 `pattern`
     * @param occurrences 参数 `occurrences`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private int setPattern(
            PreparedStatement statement,
            int startIndex,
            String pattern,
            int occurrences
    ) throws SQLException {
        int index = startIndex;
        for (int count = 0; count < occurrences; count++) {
            statement.setString(index++, pattern);
        }
        return index;
    }

    /**
     * 根据输入计算并返回 `likePattern` 的处理结果。
     *
     * @param keyword 搜索关键字
     * @return 方法处理结果
     */
    private String likePattern(String keyword) {
        return "%" + keyword + "%";
    }

    /**
     * 查询`emptyCounts`并返回结果。
     *
     * @return 按键组织的结果数据
     */
    private Map<String, Integer> emptyCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("post", 0);
        counts.put("goods", 0);
        counts.put("lost_found", 0);
        counts.put("activity", 0);
        counts.put("notice", 0);
        return counts;
    }

    /**
     * 根据输入计算并返回 `summary` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private String summary(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 100
                ? normalized
                : normalized.substring(0, 100) + "...";
    }

    /**
     * 根据输入计算并返回 `firstImage` 的处理结果。
     *
     * @param images 参数 `images`
     * @return 方法处理结果
     */
    private String firstImage(String images) {
        if (images == null || images.isBlank()) {
            return null;
        }
        String first = images.split(",", 2)[0].trim();
        return first.isEmpty() ? null : first;
    }

    /**
     * 根据输入计算并返回 `valueOr` 的处理结果。
     *
     * @param value 待处理的值
     * @param fallback 参数 `fallback`
     * @return 方法处理结果
     */
    private String valueOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    /**
     * 根据输入计算并返回 `tradeMethodText` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private String tradeMethodText(String value) {
        return switch (value == null ? "" : value) {
            case "online" -> "线上交易";
            case "both" -> "线上/线下均可";
            default -> "线下交易";
        };
    }

    /**
     * 根据输入计算并返回 `goodsStatusText` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private String goodsStatusText(String value) {
        return switch (value == null ? "" : value) {
            case "reserved" -> "已预订";
            case "sold" -> "已售出";
            case "off_shelf" -> "已下架";
            default -> "在售";
        };
    }

    /**
     * 根据输入计算并返回 `lostFoundStatusText` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private String lostFoundStatusText(String value) {
        return switch (value == null ? "" : value) {
            case "claiming" -> "认领中";
            case "completed" -> "已找回";
            case "closed" -> "已关闭";
            default -> "待认领";
        };
    }

    /**
     * 根据输入计算并返回 `activityStatusText` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private String activityStatusText(String value) {
        return switch (value == null ? "" : value) {
            case "closed" -> "已截止";
            case "ongoing" -> "进行中";
            case "finished" -> "已结束";
            default -> "报名中";
        };
    }

    /**
     * 根据输入计算并返回 `noticeTypeText` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private String noticeTypeText(String value) {
        return switch (value == null ? "" : value) {
            case "teaching" -> "教务公告";
            case "life" -> "生活公告";
            case "activity" -> "活动公告";
            case "urgent" -> "紧急公告";
            default -> "系统公告";
        };
    }

    /**
     * 根据输入计算并返回 `formatStartTime` 的处理结果。
     *
     * @param startTime 开始时间
     * @return 方法处理结果
     */
    private String formatStartTime(LocalDateTime startTime) {
        return startTime == null
                ? "时间待定"
                : startTime.format(EXTRA_TIME_FORMATTER);
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

    @FunctionalInterface
    private interface RowMapper {
        /**
         * 将数据库结果映射为`RowMapper`。
         *
         * @param resultSet 数据库查询结果集
         * @return 方法处理结果
         * @throws SQLException 数据库访问失败时抛出
         */
        SearchResultVO map(ResultSet resultSet) throws SQLException;
    }
}
