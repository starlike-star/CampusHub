package cn.campushub.dao;

import cn.campushub.model.Category;
import cn.campushub.model.LostFound;
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
 * 使用 JDBC 实现失物招领数据的查询与持久化操作。
 */
public class JdbcLostFoundDao implements LostFoundDao {
    private static final String SELECT_FIELDS = """
            SELECT lf.id, lf.user_id, lf.category_id, lf.type, lf.item_name,
                   lf.title, lf.description, lf.place, lf.event_time, lf.images,
                   lf.contact, lf.status, lf.created_at, lf.updated_at,
                   u.nickname AS publisher_nickname,
                   u.avatar AS publisher_avatar,
                   u.college AS publisher_college,
                   c.name AS category_name
            FROM lost_found lf
            JOIN users u ON u.id = lf.user_id
            LEFT JOIN categories c ON c.id = lf.category_id
            """;

    @Override
    public List<LostFound> findAll(
            String type,
            String status,
            String keyword,
            Long categoryId,
            String sort
    ) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_FIELDS).append(" WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        if (type != null) {
            sql.append(" AND lf.type = ?");
            parameters.add(type);
        }
        if (status != null) {
            sql.append(" AND lf.status = ?");
            parameters.add(status);
        }
        if (categoryId != null) {
            sql.append(" AND lf.category_id = ?");
            parameters.add(categoryId);
        }
        if (keyword != null) {
            sql.append("""
                     AND (lf.title LIKE ? OR lf.item_name LIKE ?
                          OR lf.description LIKE ? OR lf.place LIKE ?)
                    """);
            String pattern = "%" + keyword + "%";
            parameters.add(pattern);
            parameters.add(pattern);
            parameters.add(pattern);
            parameters.add(pattern);
        }
        sql.append(" ORDER BY lf.created_at ")
                .append("oldest".equals(sort) ? "ASC" : "DESC")
                .append(", lf.id ")
                .append("oldest".equals(sort) ? "ASC" : "DESC");
        return query(sql.toString(), parameters);
    }

    @Override
    public List<LostFound> findByUser(long userId) throws SQLException {
        return query(
                SELECT_FIELDS + " WHERE lf.user_id = ? ORDER BY lf.created_at DESC",
                List.of(userId)
        );
    }

    @Override
    public List<Category> findActiveCategories() throws SQLException {
        String sql = """
                SELECT id, name, type, description, sort_order, status, created_at
                FROM categories
                WHERE type = 'lost_found' AND status = 1
                ORDER BY sort_order, id
                """;
        List<Category> categories = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getLong("id"));
                category.setName(resultSet.getString("name"));
                category.setType(resultSet.getString("type"));
                category.setDescription(resultSet.getString("description"));
                category.setSortOrder(resultSet.getInt("sort_order"));
                category.setStatus(resultSet.getInt("status"));
                category.setCreatedAt(toLocalDateTime(
                        resultSet.getTimestamp("created_at")
                ));
                categories.add(category);
            }
        }
        return categories;
    }

    @Override
    public boolean isActiveCategory(long categoryId) throws SQLException {
        String sql = """
                SELECT 1 FROM categories
                WHERE id = ? AND type = 'lost_found' AND status = 1
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
    public long create(LostFound lostFound) throws SQLException {
        String sql = """
                INSERT INTO lost_found
                    (user_id, category_id, type, item_name, title, description,
                     place, event_time, images, contact, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending')
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            setFields(statement, lostFound, false);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("发布后未获得主键");
                }
                return keys.getLong(1);
            }
        }
    }

    @Override
    public Optional<LostFound> findById(long id) throws SQLException {
        List<LostFound> result = query(
                SELECT_FIELDS + " WHERE lf.id = ? LIMIT 1",
                List.of(id)
        );
        return result.stream().findFirst();
    }

    @Override
    public boolean update(LostFound lostFound, boolean admin) throws SQLException {
        String sql = """
                UPDATE lost_found
                SET category_id = ?, type = ?, item_name = ?, title = ?,
                    description = ?, place = ?, event_time = ?, images = ?,
                    contact = ?
                WHERE id = ?
                """ + (admin ? "" : " AND user_id = ?");
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = setFields(statement, lostFound, true);
            statement.setLong(index++, lostFound.getId());
            if (!admin) {
                statement.setLong(index, lostFound.getUserId());
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
                UPDATE lost_found SET status = ? WHERE id = ?
                """ + (admin ? "" : " AND user_id = ?");
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

    private int setFields(
            PreparedStatement statement,
            LostFound lostFound,
            boolean update
    ) throws SQLException {
        int index = 1;
        if (!update) {
            statement.setLong(index++, lostFound.getUserId());
        }
        if (lostFound.getCategoryId() == null) {
            statement.setNull(index++, java.sql.Types.BIGINT);
        } else {
            statement.setLong(index++, lostFound.getCategoryId());
        }
        statement.setString(index++, lostFound.getType());
        statement.setString(index++, lostFound.getItemName());
        statement.setString(index++, lostFound.getTitle());
        statement.setString(index++, lostFound.getDescription());
        statement.setString(index++, lostFound.getPlace());
        statement.setTimestamp(
                index++,
                lostFound.getEventTime() == null
                        ? null
                        : Timestamp.valueOf(lostFound.getEventTime())
        );
        statement.setString(index++, lostFound.getImages());
        statement.setString(index++, lostFound.getContact());
        return index;
    }

    private List<LostFound> query(String sql, List<Object> parameters)
            throws SQLException {
        List<LostFound> result = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(map(resultSet));
                }
            }
        }
        return result;
    }

    private LostFound map(ResultSet resultSet) throws SQLException {
        LostFound item = new LostFound();
        item.setId(resultSet.getLong("id"));
        item.setUserId(resultSet.getLong("user_id"));
        long categoryId = resultSet.getLong("category_id");
        item.setCategoryId(resultSet.wasNull() ? null : categoryId);
        item.setType(resultSet.getString("type"));
        item.setItemName(resultSet.getString("item_name"));
        item.setTitle(resultSet.getString("title"));
        item.setDescription(resultSet.getString("description"));
        item.setPlace(resultSet.getString("place"));
        item.setEventTime(toLocalDateTime(resultSet.getTimestamp("event_time")));
        item.setImages(resultSet.getString("images"));
        item.setContact(resultSet.getString("contact"));
        item.setStatus(resultSet.getString("status"));
        item.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        item.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        item.setPublisherNickname(resultSet.getString("publisher_nickname"));
        item.setPublisherAvatar(resultSet.getString("publisher_avatar"));
        item.setPublisherCollege(resultSet.getString("publisher_college"));
        item.setCategoryName(resultSet.getString("category_name"));
        return item;
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
