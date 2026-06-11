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
                        "#square?tab=notice",
                        toLocalDateTime(resultSet.getTimestamp("created_at"))
                )
        );
    }

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

    private String likePattern(String keyword) {
        return "%" + keyword + "%";
    }

    private Map<String, Integer> emptyCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("post", 0);
        counts.put("goods", 0);
        counts.put("lost_found", 0);
        counts.put("activity", 0);
        counts.put("notice", 0);
        return counts;
    }

    private String summary(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 100
                ? normalized
                : normalized.substring(0, 100) + "...";
    }

    private String firstImage(String images) {
        if (images == null || images.isBlank()) {
            return null;
        }
        String first = images.split(",", 2)[0].trim();
        return first.isEmpty() ? null : first;
    }

    private String valueOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String tradeMethodText(String value) {
        return switch (value == null ? "" : value) {
            case "online" -> "线上交易";
            case "both" -> "线上/线下均可";
            default -> "线下交易";
        };
    }

    private String goodsStatusText(String value) {
        return switch (value == null ? "" : value) {
            case "reserved" -> "已预订";
            case "sold" -> "已售出";
            default -> "在售";
        };
    }

    private String lostFoundStatusText(String value) {
        return switch (value == null ? "" : value) {
            case "claiming" -> "认领中";
            case "completed" -> "已找回";
            default -> "待认领";
        };
    }

    private String activityStatusText(String value) {
        return switch (value == null ? "" : value) {
            case "closed" -> "已截止";
            case "ongoing" -> "进行中";
            default -> "报名中";
        };
    }

    private String noticeTypeText(String value) {
        return switch (value == null ? "" : value) {
            case "teaching" -> "教务公告";
            case "life" -> "生活公告";
            case "activity" -> "活动公告";
            case "urgent" -> "紧急公告";
            default -> "系统公告";
        };
    }

    private String formatStartTime(LocalDateTime startTime) {
        return startTime == null
                ? "时间待定"
                : startTime.format(EXTRA_TIME_FORMATTER);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    @FunctionalInterface
    private interface RowMapper {
        SearchResultVO map(ResultSet resultSet) throws SQLException;
    }
}
