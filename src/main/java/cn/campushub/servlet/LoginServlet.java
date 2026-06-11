package cn.campushub.servlet;

import cn.campushub.constant.SessionConstants;
import cn.campushub.model.SessionUser;
import cn.campushub.service.RememberMeService;
import cn.campushub.service.ServiceResult;
import cn.campushub.service.UserService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserService();
    private final RememberMeService rememberMeService = new RememberMeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtils.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean rememberMe = "on".equals(request.getParameter("rememberMe"));
        request.setAttribute("username", username);
        request.setAttribute("rememberMe", rememberMe);

        if (!captchaMatches(request)) {
            request.setAttribute("errorMessage", "验证码错误");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp")
                    .forward(request, response);
            return;
        }

        try {
            ServiceResult<SessionUser> result = userService.login(username, password);
            if (!result.success()) {
                request.setAttribute("errorMessage", result.message());
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            SessionUtils.login(request, result.data());
            updateRememberMe(request, response, result.data(), rememberMe);
            response.sendRedirect(resolveRedirectTarget(request));
        } catch (SQLException exception) {
            log("登录时访问数据库失败", exception);
            request.setAttribute("errorMessage", "服务暂时不可用，请稍后再试");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    private boolean captchaMatches(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object expected = session == null
                ? null
                : session.getAttribute(SessionConstants.LOGIN_CAPTCHA);
        if (session != null) {
            session.removeAttribute(SessionConstants.LOGIN_CAPTCHA);
        }
        String submitted = request.getParameter("captcha");
        return expected instanceof String code
                && submitted != null
                && code.equalsIgnoreCase(submitted.trim());
    }

    private void updateRememberMe(
            HttpServletRequest request,
            HttpServletResponse response,
            SessionUser user,
            boolean rememberMe
    ) {
        try {
            if (rememberMe) {
                rememberMeService.createRememberToken(user, request, response);
            } else {
                rememberMeService.logout(request, response);
            }
        } catch (SQLException exception) {
            log("更新记住我凭证失败", exception);
        }
    }

    private String resolveRedirectTarget(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();
        if (session == null) {
            return contextPath + "/";
        }
        Object savedRedirect = session.getAttribute(SessionConstants.REDIRECT_AFTER_LOGIN);
        session.removeAttribute(SessionConstants.REDIRECT_AFTER_LOGIN);
        if (savedRedirect instanceof String path && isLocalPath(path, contextPath)) {
            return path;
        }
        return contextPath + "/";
    }

    private boolean isLocalPath(String path, String contextPath) {
        return path.startsWith(contextPath + "/")
                && !path.startsWith("//")
                && !path.contains("\r")
                && !path.contains("\n");
    }
}
