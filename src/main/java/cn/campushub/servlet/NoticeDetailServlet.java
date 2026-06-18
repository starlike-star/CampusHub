package cn.campushub.servlet;

import cn.campushub.model.Notice;
import cn.campushub.service.NoticeService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 接收公告的详情查询请求，调用业务层并生成 HTTP 响应。
 */
public class NoticeDetailServlet extends HttpServlet {
    private final NoticeService noticeService = new NoticeService();

    /**
     * 处理公告详情相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = parseId(request.getParameter("id"));
        if (id == null) {
            showUnavailable(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }
        try {
            Optional<Notice> notice = noticeService.findVisibleNoticeById(id);
            if (notice.isEmpty()) {
                showUnavailable(
                        request,
                        response,
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }
            request.setAttribute("notice", notice.get());
            forward(request, response);
        } catch (SQLException exception) {
            log("加载公告详情失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "公告详情加载失败"
            );
        }
    }

    /**
     * 处理 `showUnavailable` 对应的业务流程。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param status 业务状态
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    private void showUnavailable(
            HttpServletRequest request,
            HttpServletResponse response,
            int status
    ) throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("noticeError", "公告不存在或已隐藏");
        forward(request, response);
    }

    /**
     * 处理 `forward` 对应的业务流程。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    private void forward(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/noticeDetail.jsp")
                .forward(request, response);
    }

    /**
     * 解析编号。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    private Long parseId(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
