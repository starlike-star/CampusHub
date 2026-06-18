package cn.campushub.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 接收失物招领的删除请求，调用业务层并生成 HTTP 响应。
 */
public class LostFoundDeleteServlet extends LostFoundStatusServlet {
    /**
     * 处理`LostFoundDelete`相关的 HTTP POST 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("requestedDelete", true);
        request.getParameterMap();
        HttpServletRequest wrapped = new StatusRequestWrapper(request, "closed");
        super.doPost(wrapped, response);
    }

    private static class StatusRequestWrapper
            extends javax.servlet.http.HttpServletRequestWrapper {
        private final String status;

        StatusRequestWrapper(HttpServletRequest request, String status) {
            super(request);
            this.status = status;
        }

        /**
         * 获取`Parameter`。
         *
         * @param name 参数 `name`
         * @return `Parameter`
         */
        @Override
        public String getParameter(String name) {
            return "status".equals(name) ? status : super.getParameter(name);
        }
    }
}
