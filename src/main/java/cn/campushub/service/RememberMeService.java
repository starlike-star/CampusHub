package cn.campushub.service;

import cn.campushub.dao.JdbcRememberTokenDao;
import cn.campushub.dao.JdbcUserDao;
import cn.campushub.dao.RememberTokenDao;
import cn.campushub.dao.UserDao;
import cn.campushub.model.RememberToken;
import cn.campushub.model.SessionUser;
import cn.campushub.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * 编排RememberMe业务规则、参数校验与数据访问操作。
 */
public class RememberMeService {
    public static final String COOKIE_NAME = "CAMPUSHUB_REMEMBER_ME";
    private static final int MAX_AGE_SECONDS = 7 * 24 * 60 * 60;
    private static final int SELECTOR_BYTES = 18;
    private static final int TOKEN_BYTES = 32;
    private static final int MAX_USER_AGENT_LENGTH = 255;
    private static final int MAX_IP_ADDRESS_LENGTH = 64;

    private final RememberTokenDao rememberTokenDao;
    private final UserDao userDao;
    private final SecureRandom secureRandom;

    /**
     * 初始化`RememberMe`对象及其运行所需依赖。
     */
    public RememberMeService() {
        this(new JdbcRememberTokenDao(), new JdbcUserDao(), new SecureRandom());
    }

    RememberMeService(
            RememberTokenDao rememberTokenDao,
            UserDao userDao,
            SecureRandom secureRandom
    ) {
        this.rememberTokenDao = rememberTokenDao;
        this.userDao = userDao;
        this.secureRandom = secureRandom;
    }

    /**
     * 创建记住登录令牌。
     *
     * @param user 用户数据
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws SQLException 数据库访问失败时抛出
     */
    public void createRememberToken(
            SessionUser user,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException {
        revokeCurrentToken(request);
        rememberTokenDao.deleteExpiredTokens();

        String selector = randomValue(SELECTOR_BYTES);
        String token = randomValue(TOKEN_BYTES);
        rememberTokenDao.createToken(
                user.id(),
                selector,
                hashToken(token),
                LocalDateTime.now().plusSeconds(MAX_AGE_SECONDS),
                truncate(request.getHeader("User-Agent"), MAX_USER_AGENT_LENGTH),
                truncate(request.getRemoteAddr(), MAX_IP_ADDRESS_LENGTH)
        );
        addCookie(response, request, selector + ":" + token, MAX_AGE_SECONDS);
    }

    /**
     * 查询`autoLogin`并返回结果。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public Optional<SessionUser> autoLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException {
        String cookieValue = findCookieValue(request);
        if (cookieValue == null) {
            return Optional.empty();
        }

        ParsedCookie parsed = parseCookie(cookieValue);
        if (parsed == null) {
            clearCookie(response, request);
            return Optional.empty();
        }

        Optional<RememberToken> optionalToken =
                rememberTokenDao.findBySelector(parsed.selector());
        if (optionalToken.isEmpty()) {
            clearCookie(response, request);
            return Optional.empty();
        }

        RememberToken storedToken = optionalToken.get();
        if (storedToken.expiresAt() == null
                || !storedToken.expiresAt().isAfter(LocalDateTime.now())
                || !tokenMatches(parsed.token(), storedToken.tokenHash())) {
            rememberTokenDao.deleteBySelector(parsed.selector());
            clearCookie(response, request);
            return Optional.empty();
        }

        Optional<User> optionalUser = userDao.findById(storedToken.userId());
        if (optionalUser.isEmpty()
                || optionalUser.get().getStatus() == null
                || optionalUser.get().getStatus() != 1) {
            rememberTokenDao.deleteBySelector(parsed.selector());
            clearCookie(response, request);
            return Optional.empty();
        }

        rememberTokenDao.updateLastUsed(parsed.selector());
        return Optional.of(SessionUser.from(optionalUser.get()));
    }

    /**
     * 处理 `logout` 对应的业务流程。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws SQLException 数据库访问失败时抛出
     */
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException {
        revokeCurrentToken(request);
        clearCookie(response, request);
    }

    /**
     * 处理 `clearRememberCookie` 对应的业务流程。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     */
    public void clearRememberCookie(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        clearCookie(response, request);
    }

    /**
     * 处理 `revokeCurrentToken` 对应的业务流程。
     *
     * @param request HTTP 请求对象
     * @throws SQLException 数据库访问失败时抛出
     */
    private void revokeCurrentToken(HttpServletRequest request) throws SQLException {
        ParsedCookie parsed = parseCookie(findCookieValue(request));
        if (parsed == null) {
            return;
        }
        Optional<RememberToken> stored =
                rememberTokenDao.findBySelector(parsed.selector());
        if (stored.isPresent()
                && tokenMatches(parsed.token(), stored.get().tokenHash())) {
            rememberTokenDao.deleteBySelector(parsed.selector());
        }
    }

    /**
     * 转换为`kenMatches`。
     *
     * @param token 参数 `token`
     * @param storedHash 参数 `storedHash`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    private boolean tokenMatches(String token, String storedHash) {
        if (storedHash == null) {
            return false;
        }
        return MessageDigest.isEqual(
                hashToken(token).getBytes(StandardCharsets.US_ASCII),
                storedHash.getBytes(StandardCharsets.US_ASCII)
        );
    }

    /**
     * 判断是否具有`hToken`。
     *
     * @param token 参数 `token`
     * @return 方法处理结果
     */
    static String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    /**
     * 根据输入计算并返回 `randomValue` 的处理结果。
     *
     * @param byteCount 参数 `byteCount`
     * @return 方法处理结果
     */
    private String randomValue(int byteCount) {
        byte[] bytes = new byte[byteCount];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * 查询`CookieValue`。
     *
     * @param request HTTP 请求对象
     * @return 方法处理结果
     */
    private String findCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 解析`Cookie`。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    private ParsedCookie parseCookie(String value) {
        if (value == null) {
            return null;
        }
        int separator = value.indexOf(':');
        if (separator <= 0
                || separator == value.length() - 1
                || value.indexOf(':', separator + 1) >= 0) {
            return null;
        }
        return new ParsedCookie(
                value.substring(0, separator),
                value.substring(separator + 1)
        );
    }

    /**
     * 处理 `clearCookie` 对应的业务流程。
     *
     * @param response HTTP 响应对象
     * @param request HTTP 请求对象
     */
    private void clearCookie(
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        addCookie(response, request, "", 0);
    }

    /**
     * 新增`Cookie`。
     *
     * @param response HTTP 响应对象
     * @param request HTTP 请求对象
     * @param value 待处理的值
     * @param maxAge 参数 `maxAge`
     */
    private void addCookie(
            HttpServletResponse response,
            HttpServletRequest request,
            String value,
            int maxAge
    ) {
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setPath(cookiePath(request));
        // Secure is enabled automatically when the application is served over HTTPS.
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
    }

    /**
     * 根据输入计算并返回 `cookiePath` 的处理结果。
     *
     * @param request HTTP 请求对象
     * @return 方法处理结果
     */
    private String cookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return contextPath == null || contextPath.isEmpty() ? "/" : contextPath;
    }

    /**
     * 按长度限制截断`RememberMe`。
     *
     * @param value 待处理的值
     * @param maximumLength 参数 `maximumLength`
     * @return 方法处理结果
     */
    private String truncate(String value, int maximumLength) {
        if (value == null || value.length() <= maximumLength) {
            return value;
        }
        return value.substring(0, maximumLength);
    }

    /**
     * 根据输入计算并返回 `ParsedCookie` 的处理结果。
     *
     * @param selector 参数 `selector`
     * @param token 参数 `token`
     * @return 方法处理结果
     */
    private record ParsedCookie(String selector, String token) {
    }
}
