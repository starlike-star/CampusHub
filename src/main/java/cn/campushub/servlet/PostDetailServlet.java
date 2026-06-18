package cn.campushub.servlet;

import cn.campushub.model.Post;
import cn.campushub.model.SessionUser;
import cn.campushub.service.PostService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 接收帖子的详情查询请求，调用业务层并生成 HTTP 响应。
 */
public class PostDetailServlet extends HttpServlet {
    private final PostService postService = new PostService();

    /**
     * 处理帖子详情相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long postId = parseId(request.getParameter("id"));
        if (postId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "帖子参数无效");
            return;
        }

        try {
            Optional<Post> post = postService.viewPost(postId);
            if (post.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "帖子不存在");
                return;
            }
            SessionUser user = SessionUtils.currentUser(request);
            request.setAttribute("post", post.get());
            request.setAttribute(
                    "comments",
                    postService.listComments(postId, user == null ? null : user.id())
            );
            request.setAttribute(
                    "liked",
                    user != null && postService.isLiked(postId, user.id())
            );
            request.setAttribute(
                    "favorited",
                    user != null && postService.isFavorited(postId, user.id())
            );
            request.getRequestDispatcher("/WEB-INF/views/postDetail.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            log("加载帖子详情失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "帖子详情加载失败"
            );
        }
    }

    /**
     * 解析编号。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    private Long parseId(String value) {
        try {
            long id = Long.parseLong(value);
            return id > 0 ? id : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
