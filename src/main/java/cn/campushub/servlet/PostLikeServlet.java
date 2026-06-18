package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.model.PostToggleResult;
import cn.campushub.service.MessageService;
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
 * 接收帖子的点赞切换请求，调用业务层并生成 HTTP 响应。
 */
public class PostLikeServlet extends HttpServlet {
    private final PostService postService = new PostService();
    private final MessageService messageService = new MessageService();

    /**
     * 处理帖子点赞相关的 HTTP POST 请求并生成响应。
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
            ServiceResult<PostToggleResult> result =
                    postService.toggleLike(postId, user.id());
            if (!result.success()) {
                PostJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        result.message()
                );
                return;
            }
            if (result.data().active()) {
                try {
                    messageService.notifyPostLike(
                            postId,
                            user.id(),
                            user.nickname()
                    );
                } catch (SQLException notificationError) {
                    log("创建帖子点赞通知失败", notificationError);
                }
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "liked", result.data().active(),
                            "likeCount", result.data().count()
                    )
            );
        } catch (SQLException exception) {
            log("切换帖子点赞状态失败", exception);
            PostJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "点赞操作失败"
            );
        }
    }
}
