package cn.campushub.servlet;

import cn.campushub.model.PostToggleResult;
import cn.campushub.model.SessionUser;
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

public class CommentLikeServlet extends HttpServlet {
    private final PostService postService = new PostService();
    private final MessageService messageService = new MessageService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            PostJsonSupport.writeNeedLogin(response);
            return;
        }
        Long commentId = MessageJsonSupport.parsePositiveId(
                request.getParameter("commentId")
        );
        if (commentId == null) {
            PostJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "评论参数无效"
            );
            return;
        }
        try {
            ServiceResult<PostToggleResult> result =
                    postService.toggleCommentLike(commentId, user.id());
            if (result.data().active()) {
                try {
                    messageService.notifyCommentLike(
                            commentId,
                            user.id(),
                            user.nickname()
                    );
                } catch (SQLException notificationError) {
                    log("创建评论点赞通知失败", notificationError);
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
            log("切换评论点赞状态失败", exception);
            PostJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "评论不存在或不可点赞"
            );
        }
    }
}
