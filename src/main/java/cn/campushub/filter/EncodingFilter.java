package cn.campushub.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 统一请求与响应字符编码，避免中文参数和页面内容乱码。
 */
public class EncodingFilter implements Filter {
    /**
     * 对`Encoding`相关请求执行前置校验并决定是否继续过滤器链。
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
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (response instanceof HttpServletResponse httpResponse) {
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        }
        chain.doFilter(request, response);
    }
}
