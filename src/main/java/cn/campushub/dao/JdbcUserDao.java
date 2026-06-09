package cn.campushub.dao;

import cn.campushub.model.User;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class JdbcUserDao implements UserDao {
    private static final String USER_COLUMNS = """
            id, username, password, nickname, avatar, student_no,
            college, major, grade, email, phone, role, status,
            created_at, updated_at
            """;

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

    @Override
    public boolean existsByUsername(String username) throws SQLException {
        return exists("SELECT 1 FROM users WHERE username = ? LIMIT 1", username);
    }

    @Override
    public boolean existsByEmail(String email) throws SQLException {
        return exists("SELECT 1 FROM users WHERE email = ? LIMIT 1", email);
    }

    private boolean exists(String sql, String value) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

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

    private java.time.LocalDateTime toLocalDateTime(java.sql.Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
