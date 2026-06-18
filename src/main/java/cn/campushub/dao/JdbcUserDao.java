package cn.campushub.dao;

import cn.campushub.model.User;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * 使用 JDBC 实现用户数据的查询与持久化操作。
 */
public class JdbcUserDao implements UserDao {
    private static final String USER_COLUMNS = """
            id, username, password, nickname, avatar, student_no,
            college, major, grade, email, phone, role, status,
            experience, level, canceled_at, cancel_reason,
            created_at, updated_at
            """;

    /**
     * 根据编号查询`JdbcUser`。
     *
     * @param id 业务数据编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Optional<User> findById(long id) throws SQLException {
        String sql = "SELECT " + USER_COLUMNS
                + " FROM users WHERE id = ? LIMIT 1";
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapUser(resultSet)) : Optional.empty();
            }
        }
    }

    /**
     * 根据用户名查询`JdbcUser`。
     *
     * @param username 用户名
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT " + USER_COLUMNS
                + " FROM users WHERE username = ? LIMIT 1";
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapUser(resultSet)) : Optional.empty();
            }
        }
    }

    /**
     * 根据输入计算并返回 `existsByUsername` 的处理结果。
     *
     * @param username 用户名
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean existsByUsername(String username) throws SQLException {
        return exists("SELECT 1 FROM users WHERE username = ? LIMIT 1", username);
    }

    /**
     * 根据输入计算并返回 `existsByEmail` 的处理结果。
     *
     * @param email 电子邮箱
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean existsByEmail(String email) throws SQLException {
        return exists("SELECT 1 FROM users WHERE email = ? LIMIT 1", email);
    }

    /**
     * 根据输入计算并返回 `exists` 的处理结果。
     *
     * @param sql 参数 `sql`
     * @param value 待处理的值
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean exists(String sql, String value) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 创建`JdbcUser`。
     *
     * @param user 用户数据
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public User create(User user) throws SQLException {
        String sql = """
                INSERT INTO users
                    (username, password, nickname, avatar, student_no, college,
                     major, grade, email, phone, role, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getNickname());
            statement.setString(4, user.getAvatar());
            statement.setString(5, user.getStudentNo());
            statement.setString(6, user.getCollege());
            statement.setString(7, user.getMajor());
            statement.setString(8, user.getGrade());
            statement.setString(9, user.getEmail());
            statement.setString(10, user.getPhone());
            statement.setString(11, user.getRole());
            statement.setInt(12, user.getStatus());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("创建用户后未获得主键");
                }
                user.setId(keys.getLong(1));
            }
            return user;
        }
    }

    /**
     * 将数据库结果映射为用户。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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
        user.setExperience(resultSet.getInt("experience"));
        user.setLevel(resultSet.getInt("level"));
        user.setCanceledAt(toLocalDateTime(resultSet.getTimestamp("canceled_at")));
        user.setCancelReason(resultSet.getString("cancel_reason"));
        user.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        user.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        return user;
    }

    /**
     * 转换为`LocalDateTime`。
     *
     * @param timestamp 数据库时间戳
     * @return 方法处理结果
     */
    private java.time.LocalDateTime toLocalDateTime(java.sql.Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
