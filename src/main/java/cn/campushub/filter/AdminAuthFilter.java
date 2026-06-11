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
