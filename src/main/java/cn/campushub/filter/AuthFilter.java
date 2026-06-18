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

/**
 * 拦截受保护请求，确保用户登录后才能继续访问。
 */
public class AuthFilter implements Filter {
    /**
     * 对`Auth`相关请求执行前置校验并决定是否继续过滤器链。
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

    /**
     * 判断是否`ApiRequest`。
     *
     * @param request HTTP 请求对象
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.startsWith("/api/");
    }

    /**
     * 构建`CurrentPath`。
     *
     * @param request HTTP 请求对象
     * @return 方法处理结果
     */
    private String buildCurrentPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        return query == null ? path : path + "?" + query;
    }
}
