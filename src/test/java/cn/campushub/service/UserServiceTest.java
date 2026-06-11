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

class UserServiceTest {
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

        @Override
        public Optional<User> findById(long id) {
            return users.values().stream()
                    .filter(user -> user.getId() != null && user.getId() == id)
                    .findFirst();
        }

        @Override
        public Optional<User> findByUsername(String username) {
            return Optional.ofNullable(users.get(username));
        }

        @Override
        public boolean existsByUsername(String username) {
            return users.containsKey(username);
        }

        @Override
        public boolean existsByEmail(String email) {
            return users.containsKey(email);
        }

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
