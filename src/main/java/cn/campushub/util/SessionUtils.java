package cn.campushub.util;

import cn.campushub.constant.SessionConstants;
import cn.campushub.model.SessionUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 统一读取和维护当前会话中的登录用户信息。
 */
public final class SessionUtils {
    private SessionUtils() {
    }

    public static void login(HttpServletRequest request, SessionUser user) {
        HttpSession oldSession = request.getSession(false);
        String redirectAfterLogin = null;
        if (oldSession != null) {
            Object redirect = oldSession.getAttribute(SessionConstants.REDIRECT_AFTER_LOGIN);
            if (redirect instanceof String redirectValue) {
                redirectAfterLogin = redirectValue;
            }
            oldSession.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute(SessionConstants.LOGIN_USER, user);
        if (redirectAfterLogin != null) {
            newSession.setAttribute(SessionConstants.REDIRECT_AFTER_LOGIN, redirectAfterLogin);
        }
    }

    public static SessionUser currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute(SessionConstants.LOGIN_USER);
        return user instanceof SessionUser sessionUser ? sessionUser : null;
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return currentUser(request) != null;
    }
}
