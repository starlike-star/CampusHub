package cn.campushub.service;

import cn.campushub.dao.JdbcUserDao;
import cn.campushub.dao.UserDao;
import cn.campushub.model.SessionUser;
import cn.campushub.model.User;
import cn.campushub.util.PasswordUtils;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Locale;
import java.util.Optional;

public class UserService {
    private static final String DEFAULT_AVATAR = "images/default-user.png";

    private final UserDao userDao;

    public UserService() {
        this(new JdbcUserDao());
    }

    UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public ServiceResult<SessionUser> register(
            String username,
            String email,
            String nickname,
            String password,
            String confirmPassword
    ) throws SQLException {
        if (ValidationUtils.containsWhitespace(username)) {
            return ServiceResult.failure("用户名不能包含空格字符");
        }
        if (ValidationUtils.containsWhitespace(nickname)) {
            return ServiceResult.failure("昵称不能包含空格字符");
        }

        username = ValidationUtils.trimToNull(username);
        email = ValidationUtils.trimToNull(email);
        nickname = ValidationUtils.trimToNull(nickname);

        if (!ValidationUtils.isValidUsername(username)) {
            return ServiceResult.failure("用户名不能包含空格，需以字母开头，由 4-50 位字母、数字或下划线组成");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            return ServiceResult.failure("请输入有效的邮箱地址");
        }
        if (!ValidationUtils.isValidNickname(nickname)) {
            return ServiceResult.failure("昵称不能包含空格，需为 2-50 位中文、字母、数字或常用连接符");
        }
        if (!ValidationUtils.isValidPassword(password)) {
            return ServiceResult.failure("密码需为 8-72 位，并同时包含字母和数字");
        }
        if (!password.equals(confirmPassword)) {
            return ServiceResult.failure("两次输入的密码不一致");
        }

        String normalizedUsername = username.toLowerCase(Locale.ROOT);
        String normalizedEmail = email.toLowerCase(Locale.ROOT);
        if (userDao.existsByUsername(normalizedUsername)) {
            return ServiceResult.failure("该用户名已被使用");
        }
        if (userDao.existsByEmail(normalizedEmail)) {
            return ServiceResult.failure("该邮箱已被注册");
        }

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setPassword(PasswordUtils.hash(password));
        user.setNickname(nickname);
        user.setAvatar(DEFAULT_AVATAR);
        user.setStudentNo(null);
        user.setCollege(null);
        user.setMajor(null);
        user.setGrade(null);
        user.setEmail(normalizedEmail);
        user.setPhone(null);
        user.setRole("student");
        user.setStatus(1);
        try {
            userDao.create(user);
        } catch (SQLIntegrityConstraintViolationException exception) {
            // 防止“先检查后写入”之间出现并发注册竞态。
            return ServiceResult.failure("用户名或邮箱已被注册");
        }
        return ServiceResult.success("注册成功", SessionUser.from(user));
    }

    public ServiceResult<SessionUser> login(String username, String password)
            throws SQLException {
        username = ValidationUtils.trimToNull(username);
        if (username == null || password == null || password.isEmpty()) {
            return ServiceResult.failure("请输入用户名和密码");
        }

        Optional<User> optionalUser =
                userDao.findByUsername(username.toLowerCase(Locale.ROOT));
        if (optionalUser.isEmpty()
                || !PasswordUtils.matches(password, optionalUser.get().getPassword())) {
            return ServiceResult.failure("用户名或密码错误");
        }

        User user = optionalUser.get();
        if (user.getStatus() != null && user.getStatus() == 2) {
            return ServiceResult.failure("该账号已注销，无法登录");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            return ServiceResult.failure("当前账号不可用，请联系管理员");
        }

        return ServiceResult.success("登录成功", SessionUser.from(user));
    }
}
