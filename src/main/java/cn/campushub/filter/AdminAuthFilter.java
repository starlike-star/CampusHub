package cn.campushub.filter;

import cn.campushub.constant.SessionConstants;
import cn.campushub.model.SessionUser;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 拦截后台请求并校验当前用户是否具有管理员权限。
 */
public class AdminAuthFilter implements Filter {
    /**
     * 对`AdminAuth`相关请求执行前置校验并决定是否继续过滤器链。
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
        HttpSession session = httpRequest.getSession(false);
        SessionUser user = session == null
                ? null
                : (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);

        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        if (!"admin".equalsIgnoreCase(user.role())) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpRequest.getRequestDispatcher("/WEB-INF/views/admin/forbidden.jsp")
                    .forward(httpRequest, httpResponse);
            return;
        }
        chain.doFilter(request, response);
    }
}
