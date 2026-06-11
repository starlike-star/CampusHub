package cn.campushub.dao;

import cn.campushub.model.Category;
import cn.campushub.model.Goods;
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
 * 使用 JDBC 实现商品数据的查询与持久化操作。
 */
public class JdbcGoodsDao implements GoodsDao {
    public static final String BASE_LIST_SQL = """
            SELECT g.id, g.user_id, g.category_id, g.title, g.description,
                   g.price, g.condition_level, g.images, g.trade_place,
                   g.trade_method, g.contact, g.status, g.created_at,
                   g.updated_at,
                   u.nickname AS seller_nickname, u.avatar AS seller_avatar,
                   u.college AS seller_college, c.name AS category_name,
                   COUNT(DISTINCT favorite.id) AS favorite_count,
                   MAX(CASE WHEN current_favorite.id IS NULL THEN 0 ELSE 1 END)
                       AS favorited
            FROM goods g
            JOIN users u ON u.id = g.user_id
            LEFT JOIN categories c ON c.id = g.category_id
            LEFT JOIN favorites favorite
              ON favorite.target_id = g.id
             AND favorite.target_type = 'goods'
            LEFT JOIN favorites current_favorite
              ON current_favorite.target_id = g.id
             AND current_favorite.target_type = 'goods'
             AND current_favorite.user_id = ?
            WHERE g.status != 'off_shelf'
            """;
    public static final String LATEST_ORDER = "ORDER BY g.created_at DESC";
    public static final String PRICE_ASC_ORDER =
            "ORDER BY g.price ASC, g.created_at DESC";
    public static final String PRICE_DESC_ORDER =
            "ORDER BY g.price DESC, g.created_at DESC";
    public static final String HOT_ORDER =
            "ORDER BY favorite_count DESC, g.created_at DESC";

    private static final String GROUP_BY = """
            GROUP BY g.id, g.user_id, g.category_id, g.title, g.description,
                     g.price, g.condition_level, g.images, g.trade_place,
                     g.trade_method, g.contact, g.status, g.created_at,
                     g.updated_at,
                     u.nickname, u.avatar, u.college, c.name
            """;

    @Override
    public List<Goods> findGoods(
            Long currentUserId,
            String keyword,
            Long categoryId,
            String status,
            String tradeMethod,
            String sort
    ) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_LIST_SQL);
        if (keyword != null) {
            sql.append("""

                    AND (g.title LIKE ? OR g.description LIKE ?
                         OR g.trade_place LIKE ?)
                    """);
        }
        if (categoryId != null) {
            sql.append("\nAND g.category_id = ?");
        }
        if (status != null) {
            sql.append("\nAND g.status = ?");
        }
        if (tradeMethod != null) {
            sql.append("\nAND g.trade_method = ?");
        }
        sql.append('\n').append(GROUP_BY).append('\n').append(orderBy(sort));

        List<Goods> goodsList = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {
            int index = 1;
            statement.setLong(index++, currentUserId == null ? 0L : currentUserId);
            if (keyword != null) {
                String pattern = "%" + keyword + "%";
                statement.setString(index++, pattern);
                statement.setString(index++, pattern);
                statement.setString(index++, pattern);
            }
            if (categoryId != null) {
                statement.setLong(index++, categoryId);
            }
            if (status != null) {
                statement.setString(index++, status);
            }
            if (tradeMethod != null) {
                statement.setString(index, tradeMethod);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    goodsList.add(mapGoods(resultSet));
                }
            }
        }
        return goodsList;
    }

    @Override
    public List<Goods> findOwnGoods(long userId) throws SQLException {
        String sql = BASE_LIST_SQL.replace(
                "WHERE g.status != 'off_shelf'",
                "WHERE g.user_id = ?"
        ) + "\n" + GROUP_BY + "\n" + LATEST_ORDER;
        return queryByUser(sql, userId, true);
    }

    @Override
    public List<Goods> findFavoriteGoods(long userId) throws SQLException {
        String sql = BASE_LIST_SQL + """

                AND EXISTS (
                    SELECT 1
                    FROM favorites selected_favorite
                    WHERE selected_favorite.user_id = ?
                      AND selected_favorite.target_id = g.id
                      AND selected_favorite.target_type = 'goods'
                )
                """ + "\n" + GROUP_BY + """

                ORDER BY (
                    SELECT MAX(selected_favorite.created_at)
                    FROM favorites selected_favorite
                    WHERE selected_favorite.user_id = ?
                      AND selected_favorite.target_id = g.id
                      AND selected_favorite.target_type = 'goods'
                ) DESC
                """;
        List<Goods> goodsList = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            statement.setLong(3, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    goodsList.add(mapGoods(resultSet));
                }
            }
        }
        return goodsList;
    }

    @Override
    public List<Category> findActiveGoodsCategories() throws SQLException {
        String sql = """
                SELECT id, name, type, description, sort_order, status, created_at
                FROM categories
                WHERE type = 'goods' AND status = 1
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

    @Override
    public boolean isActiveGoodsCategory(long categoryId) throws SQLException {
        String sql = """
                SELECT 1
                FROM categories
                WHERE id = ? AND type = 'goods' AND status = 1
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

    @Override
    public long create(Goods goods) throws SQLException {
        String sql = """
                INSERT INTO goods
                    (user_id, category_id, title, description, price,
                     condition_level, images, trade_place, trade_method,
                     contact, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'on_sale')
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, goods.getUserId());
            statement.setLong(2, goods.getCategoryId());
            statement.setString(3, goods.getTitle());
            statement.setString(4, goods.getDescription());
            statement.setBigDecimal(5, goods.getPrice());
            statement.setString(6, goods.getConditionLevel());
            statement.setString(7, goods.getImages());
            statement.setString(8, goods.getTradePlace());
            statement.setString(9, goods.getTradeMethod());
            statement.setString(10, goods.getContact());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("创建商品后未获得主键");
                }
                return keys.getLong(1);
            }
        }
    }

    @Override
    public Optional<Goods> findVisibleById(long goodsId, Long currentUserId)
            throws SQLException {
        String sql = BASE_LIST_SQL + """

                AND g.id = ?
                GROUP BY g.id, g.user_id, g.category_id, g.title, g.description,
                         g.price, g.condition_level, g.images, g.trade_place,
                         g.trade_method, g.contact, g.status, g.created_at,
                         g.updated_at,
                         u.nickname, u.avatar, u.college, c.name
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, currentUserId == null ? 0L : currentUserId);
            statement.setLong(2, goodsId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapGoods(resultSet))
                        : Optional.empty();
            }
        }
    }

    @Override
    public PostToggleResult toggleFavorite(long goodsId, long userId)
            throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!lockVisibleGoods(connection, goodsId)) {
                    connection.rollback();
                    throw new SQLException("商品不存在或已下架");
                }
                boolean favorited = hasFavorite(connection, goodsId, userId);
                if (favorited) {
                    try (PreparedStatement statement = connection.prepareStatement("""
                            DELETE FROM favorites
                            WHERE user_id = ? AND target_id = ?
                              AND target_type = 'goods'
                            """)) {
                        statement.setLong(1, userId);
                        statement.setLong(2, goodsId);
                        statement.executeUpdate();
                    }
                } else {
                    try (PreparedStatement statement = connection.prepareStatement("""
                            INSERT INTO favorites (user_id, target_id, target_type)
                            VALUES (?, ?, 'goods')
                            """)) {
                        statement.setLong(1, userId);
                        statement.setLong(2, goodsId);
                        statement.executeUpdate();
                    }
                }
                int count = countFavorites(connection, goodsId);
                connection.commit();
                return new PostToggleResult(!favorited, count);
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public Optional<Goods> update(Goods goods, boolean admin) throws SQLException {
        String sql = """
                UPDATE goods
                SET title = ?, description = ?, price = ?, category_id = ?,
                    condition_level = ?, images = ?, trade_place = ?,
                    trade_method = ?, contact = ?
                WHERE id = ?
                """ + (admin ? "" : " AND user_id = ?");
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, goods.getTitle());
            statement.setString(2, goods.getDescription());
            statement.setBigDecimal(3, goods.getPrice());
            statement.setLong(4, goods.getCategoryId());
            statement.setString(5, goods.getConditionLevel());
            statement.setString(6, goods.getImages());
            statement.setString(7, goods.getTradePlace());
            statement.setString(8, goods.getTradeMethod());
            statement.setString(9, goods.getContact());
            statement.setLong(10, goods.getId());
            if (!admin) {
                statement.setLong(11, goods.getUserId());
            }
            if (statement.executeUpdate() != 1) {
                return Optional.empty();
            }
        }
        return findByIdIncludingOffShelf(goods.getId(), goods.getUserId());
    }

    @Override
    public boolean updateStatus(
            long goodsId,
            long userId,
            boolean admin,
            String status
    ) throws SQLException {
        String sql = """
                UPDATE goods
                SET status = ?
                WHERE id = ?
                """ + (admin ? "" : " AND user_id = ? AND status != 'sold'");
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setLong(2, goodsId);
            if (!admin) {
                statement.setLong(3, userId);
            }
            return statement.executeUpdate() == 1;
        }
    }

    private String orderBy(String sort) {
        return switch (sort) {
            case "price_asc" -> PRICE_ASC_ORDER;
            case "price_desc" -> PRICE_DESC_ORDER;
            case "hot" -> HOT_ORDER;
            default -> LATEST_ORDER;
        };
    }

    private boolean lockVisibleGoods(Connection connection, long goodsId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT status
                FROM goods
                WHERE id = ?
                FOR UPDATE
                """)) {
            statement.setLong(1, goodsId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        && !"off_shelf".equals(resultSet.getString("status"));
            }
        }
    }

    private boolean hasFavorite(
            Connection connection,
            long goodsId,
            long userId
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT 1
                FROM favorites
                WHERE user_id = ? AND target_id = ? AND target_type = 'goods'
                LIMIT 1
                """)) {
            statement.setLong(1, userId);
            statement.setLong(2, goodsId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private int countFavorites(Connection connection, long goodsId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT COUNT(*) AS favorite_count
                FROM favorites
                WHERE target_id = ? AND target_type = 'goods'
                """)) {
            statement.setLong(1, goodsId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("favorite_count");
            }
        }
    }

    private Optional<Goods> findByIdIncludingOffShelf(
            long goodsId,
            Long currentUserId
    ) throws SQLException {
        String sql = BASE_LIST_SQL.replace(
                "WHERE g.status != 'off_shelf'",
                "WHERE g.id = ?"
        ) + "\n" + GROUP_BY + "\nLIMIT 1";
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, currentUserId == null ? 0L : currentUserId);
            statement.setLong(2, goodsId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapGoods(resultSet))
                        : Optional.empty();
            }
        }
    }

    private Goods mapGoods(ResultSet resultSet) throws SQLException {
        Goods goods = new Goods();
        goods.setId(resultSet.getLong("id"));
        goods.setUserId(resultSet.getLong("user_id"));
        long categoryId = resultSet.getLong("category_id");
        goods.setCategoryId(resultSet.wasNull() ? null : categoryId);
        goods.setTitle(resultSet.getString("title"));
        goods.setDescription(resultSet.getString("description"));
        goods.setPrice(resultSet.getBigDecimal("price"));
        goods.setConditionLevel(resultSet.getString("condition_level"));
        goods.setImages(resultSet.getString("images"));
        goods.setTradePlace(resultSet.getString("trade_place"));
        goods.setTradeMethod(resultSet.getString("trade_method"));
        goods.setContact(resultSet.getString("contact"));
        goods.setStatus(resultSet.getString("status"));
        goods.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        goods.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        goods.setSellerNickname(resultSet.getString("seller_nickname"));
        goods.setSellerAvatar(resultSet.getString("seller_avatar"));
        goods.setSellerCollege(resultSet.getString("seller_college"));
        goods.setCategoryName(resultSet.getString("category_name"));
        goods.setFavoriteCount(resultSet.getInt("favorite_count"));
        goods.setFavorited(resultSet.getBoolean("favorited"));
        return goods;
    }

    private List<Goods> queryByUser(
            String sql,
            long userId,
            boolean ownerQuery
    ) throws SQLException {
        List<Goods> goodsList = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            if (ownerQuery) {
                statement.setLong(2, userId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    goodsList.add(mapGoods(resultSet));
                }
            }
        }
        return goodsList;
    }

    private Category mapCategory(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getLong("id"));
        category.setName(resultSet.getString("name"));
        category.setType(resultSet.getString("type"));
        category.setDescription(resultSet.getString("description"));
        category.setSortOrder(resultSet.getInt("sort_order"));
        category.setStatus(resultSet.getInt("status"));
        category.setCreatedAt(
                toLocalDateTime(resultSet.getTimestamp("created_at"))
        );
        return category;
    }

    private java.time.LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
