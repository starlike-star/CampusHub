package cn.campushub.dao;

import cn.campushub.model.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * 定义用户数据访问能力及业务层依赖的数据契约。
 */
public interface UserDao {
    Optional<User> findById(long id) throws SQLException;

    Optional<User> findByUsername(String username) throws SQLException;

    boolean existsByUsername(String username) throws SQLException;

    boolean existsByEmail(String email) throws SQLException;

    User create(User user) throws SQLException;
}
