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

    /**
     * 对`RememberMe`相关请求执行前置校验并决定是否继续过滤器链。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param chain 过滤器链
     * @throws IOException 读取请求或写入响应失败时抛出
     * @throws ServletException Servlet 处理请求失败时抛出
     */
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

    /**
     * 判断是否需要`ValidateSession`。
     *
     * @param request HTTP 请求对象
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
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
