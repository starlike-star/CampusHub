package cn.campushub.dao;

import cn.campushub.model.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * 定义用户数据访问能力及业务层依赖的数据契约。
 */
public interface UserDao {
    /**
     * 根据编号查询用户。
     *
     * @param id 业务数据编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<User> findById(long id) throws SQLException;

    /**
     * 根据用户名查询用户。
     *
     * @param username 用户名
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<User> findByUsername(String username) throws SQLException;

    /**
     * 根据输入计算并返回 `existsByUsername` 的处理结果。
     *
     * @param username 用户名
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean existsByUsername(String username) throws SQLException;

    /**
     * 根据输入计算并返回 `existsByEmail` 的处理结果。
     *
     * @param email 电子邮箱
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean existsByEmail(String email) throws SQLException;

    /**
     * 创建用户。
     *
     * @param user 用户数据
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    User create(User user) throws SQLException;
}
