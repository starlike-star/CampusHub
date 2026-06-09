package cn.campushub.filter;

import cn.campushub.constant.SessionConstants;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (SessionUtils.isLoggedIn(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        if (isApiRequest(httpRequest)) {
            JsonUtils.write(
                    httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    Map.of("success", false, "message", "请先登录")
            );
            return;
        }

        if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                    SessionConstants.REDIRECT_AFTER_LOGIN,
                    buildCurrentPath(httpRequest)
            );
        }
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.startsWith("/api/");
    }

    private String buildCurrentPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        return query == null ? path : path + "?" + query;
    }
}
