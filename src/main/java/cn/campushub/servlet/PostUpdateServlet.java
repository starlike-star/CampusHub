package cn.campushub.servlet;

import cn.campushub.model.Post;
import cn.campushub.model.SessionUser;
import cn.campushub.service.PostService;
import cn.campushub.service.ServiceResult;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * 接收帖子的更新请求，调用业务层并生成 HTTP 响应。
 */
public class PostUpdateServlet extends HttpServlet {
    private final PostService postService = new PostService();

    /**
     * 处理帖子更新相关的 HTTP POST 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            PostJsonSupport.writeNeedLogin(response);
            return;
        }
        Long postId = PostJsonSupport.parsePostId(request);
        if (postId == null) {
            PostJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "帖子参数无效"
            );
            return;
        }
        try {
            ServiceResult<Post> result = postService.update(
                    postId,
                    user.id(),
                    request.getParameter("title"),
                    request.getParameter("content"),
                    request.getParameter("topic"),
                    request.getParameter("categoryId"),
                    request.getParameter("images")
            );
            if (!result.success()) {
                PostJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        result.message()
                );
                return;
            }
            Post post = result.data();
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "post", Map.of(
                                    "id", post.getId(),
                                    "title", post.getTitle(),
                                    "content", post.getContent(),
                                    "summary", post.getSummary(),
                                    "topic", post.getTopic() == null
                                            ? ""
                                            : "#" + post.getTopic(),
                                    "topicValue", PostJsonSupport.valueOrEmpty(
                                            post.getTopic()
                                    ),
                                    "categoryId", post.getCategoryId(),
                                    "categoryName", PostJsonSupport.valueOrEmpty(
                                            post.getCategoryName()
                                    ),
                                    "images", PostJsonSupport.valueOrEmpty(
                                            post.getImages()
                                    )
                            )
                    )
            );
        } catch (SQLException exception) {
            log("更新帖子失败", exception);
            PostJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "更新帖子失败"
            );
        }
    }
}
