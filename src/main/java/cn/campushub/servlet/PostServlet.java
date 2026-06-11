package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.PostService;
import cn.campushub.service.ServiceResult;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 接收帖子的请求处理请求，调用业务层并生成 HTTP 响应。
 */
public class PostServlet extends HttpServlet {
    private final PostService postService = new PostService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showPublishPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryId = request.getParameter("category_id");
        String topic = request.getParameter("topic");
        String images = request.getParameter("images");
        request.setAttribute("title", title);
        request.setAttribute("content", content);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("topic", topic);
        request.setAttribute("images", images);

        try {
            ServiceResult<Long> result =
                    postService.publish(
                            user.id(), title, content, categoryId, topic, images
                    );
            if (!result.success()) {
                request.setAttribute("errorMessage", result.message());
                showPublishPage(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (SQLException exception) {
            log("发布帖子失败", exception);
            request.setAttribute("errorMessage", "发布失败，请稍后重试");
            showPublishPage(request, response);
        }
    }

    private void showPublishPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("categories", postService.listCategories());
            request.getRequestDispatcher("/WEB-INF/views/publishPost.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            log("加载帖子分类失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "帖子分类加载失败"
            );
        }
    }
}
