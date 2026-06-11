package cn.campushub.filter;

import cn.campushub.model.SessionUser;
import cn.campushub.service.AccountService;
import cn.campushub.service.RememberMeService;
import cn.campushub.util.SessionUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 在会话缺少登录用户时尝试通过持久令牌恢复登录状态。
 */
public class RememberMeFilter implements Filter {
    private final RememberMeService rememberMeService = new RememberMeService();
    private final AccountService accountService = new AccountService();

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        SessionUser currentUser = SessionUtils.currentUser(httpRequest);
        boolean invalidated = false;
        if (currentUser != null && shouldValidateSession(httpRequest)) {
            try {
                if (!accountService.isActive(currentUser.id())) {
                    httpRequest.getSession().invalidate();
                    rememberMeService.clearRememberCookie(
                            httpRequest,
                            httpResponse
                    );
                    invalidated = true;
                }
            } catch (SQLException exception) {
                httpRequest.getServletContext().log(
                        "Account session validation failed",
                        exception
                );
            }
        }
        if (!invalidated && !SessionUtils.isLoggedIn(httpRequest)) {
            try {
                rememberMeService.autoLogin(httpRequest, httpResponse)
                        .ifPresent(user -> SessionUtils.login(httpRequest, user));
            } catch (SQLException exception) {
                httpRequest.getServletContext().log(
                        "Remember-me auto login failed",
                        exception
                );
            }
        }
        chain.doFilter(request, response);
    }

    private boolean shouldValidateSession(HttpServletRequest request) {
        String path = request.getRequestURI()
                .substring(request.getContextPath().length());
        return !(path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/uploads/")
                || path.equals("/captcha"));
    }
}
