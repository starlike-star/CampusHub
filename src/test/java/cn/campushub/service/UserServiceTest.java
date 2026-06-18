package cn.campushub.service;

import cn.campushub.dao.UserDao;
import cn.campushub.model.SessionUser;
import cn.campushub.model.User;
import cn.campushub.util.PasswordUtils;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 用户相关逻辑的正常路径、边界条件和失败场景。
 */
class UserServiceTest {
    /**
     * 验证 `registerNormalizesCredentialsAndHashesPassword` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void registerNormalizesCredentialsAndHashesPassword() throws SQLException {
        FakeUserDao userDao = new FakeUserDao();
        UserService service = new UserService(userDao);

        ServiceResult<SessionUser> result = service.register(
                "Campus_01",
                "Student@Example.com",
                "校园同学",
                "campus123",
                "campus123"
        );

        assertTrue(result.success());
        assertEquals("campus_01", result.data().username());
        assertEquals("校园同学", result.data().nickname());
        assertNotNull(userDao.lastCreated);
        assertEquals("student@example.com", userDao.lastCreated.getEmail());
        assertEquals("images/default-user.png", userDao.lastCreated.getAvatar());
        assertEquals("student", userDao.lastCreated.getRole());
        assertEquals(1, userDao.lastCreated.getStatus());
        assertTrue(PasswordUtils.matches("campus123", userDao.lastCreated.getPassword()));
    }

    /**
     * 验证 `loginAcceptsUsernameAndStoredPassword` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void loginAcceptsUsernameAndStoredPassword() throws SQLException {
        FakeUserDao userDao = new FakeUserDao();
        User user = activeUser();
        userDao.users.put(user.getUsername(), user);
        userDao.users.put(user.getEmail(), user);
        UserService service = new UserService(userDao);

        ServiceResult<SessionUser> result = service.login("CAMPUS_01", "campus123");

        assertTrue(result.success());
        assertEquals(user.getId(), result.data().id());
    }

    /**
     * 验证 `loginRejectsDisabledUser` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void loginRejectsDisabledUser() throws SQLException {
        FakeUserDao userDao = new FakeUserDao();
        User user = activeUser();
        user.setStatus(0);
        userDao.users.put(user.getUsername(), user);
        UserService service = new UserService(userDao);

        ServiceResult<SessionUser> result =
                service.login("campus_01", "campus123");

        assertFalse(result.success());
        assertEquals("当前账号不可用，请联系管理员", result.message());
    }

    /**
     * 验证 `registerRejectsDuplicateUsername` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void registerRejectsDuplicateUsername() throws SQLException {
        FakeUserDao userDao = new FakeUserDao();
        userDao.users.put("campus_01", activeUser());
        UserService service = new UserService(userDao);

        ServiceResult<SessionUser> result = service.register(
                "Campus_01",
                "new@example.com",
                "新同学",
                "campus123",
                "campus123"
        );

        assertFalse(result.success());
        assertEquals("该用户名已被使用", result.message());
    }

    /**
     * 验证 `registerRejectsWhitespaceInNickname` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void registerRejectsWhitespaceInNickname() throws SQLException {
        UserService service = new UserService(new FakeUserDao());

        ServiceResult<SessionUser> result = service.register(
                "campus_02",
                "space@example.com",
                "校园 同学",
                "campus123",
                "campus123"
        );

        assertFalse(result.success());
        assertTrue(result.message().contains("不能包含空格"));
    }

    /**
     * 验证 `registerRejectsWhitespaceAroundUsername` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void registerRejectsWhitespaceAroundUsername() throws SQLException {
        UserService service = new UserService(new FakeUserDao());

        ServiceResult<SessionUser> result = service.register(
                " campus_03",
                "space-user@example.com",
                "校园同学",
                "campus123",
                "campus123"
        );

        assertFalse(result.success());
        assertEquals("用户名不能包含空格字符", result.message());
    }

    /**
     * 验证 `activeUser` 场景下的业务行为与预期结果一致。
     *
     * @return `activeUser`
     */
    private User activeUser() {
        User user = new User();
        user.setId(7L);
        user.setUsername("campus_01");
        user.setEmail("student@example.com");
        user.setNickname("校园同学");
        user.setPassword(PasswordUtils.hash("campus123"));
        user.setAvatar("images/default-user.png");
        user.setRole("student");
        user.setStatus(1);
        return user;
    }

    private static class FakeUserDao implements UserDao {
        private final Map<String, User> users = new HashMap<>();
        private User lastCreated;

        /**
         * 根据编号查询模拟用户。
         *
         * @param id 业务数据编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<User> findById(long id) {
            return users.values().stream()
                    .filter(user -> user.getId() != null && user.getId() == id)
                    .findFirst();
        }

        /**
         * 根据用户名查询模拟用户。
         *
         * @param username 用户名
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<User> findByUsername(String username) {
            return Optional.ofNullable(users.get(username));
        }

        /**
         * 根据输入计算并返回 `existsByUsername` 的处理结果。
         *
         * @param username 用户名
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean existsByUsername(String username) {
            return users.containsKey(username);
        }

        /**
         * 根据输入计算并返回 `existsByEmail` 的处理结果。
         *
         * @param email 电子邮箱
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean existsByEmail(String email) {
            return users.containsKey(email);
        }

        /**
         * 创建模拟用户。
         *
         * @param user 用户数据
         * @return 方法处理结果
         */
        @Override
        public User create(User user) {
            user.setId(11L);
            lastCreated = user;
            users.put(user.getUsername(), user);
            users.put(user.getEmail(), user);
            return user;
        }
    }
}
