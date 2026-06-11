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

    private void showUnavailable(
            HttpServletRequest request,
            HttpServletResponse response,
            int status
    ) throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("noticeError", "公告不存在或已隐藏");
        forward(request, response);
    }

    private void forward(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/noticeDetail.jsp")
                .forward(request, response);
    }

    private Long parseId(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
