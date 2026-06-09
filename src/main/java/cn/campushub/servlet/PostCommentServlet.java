package cn.campushub.servlet;

import cn.campushub.model.Comment;
import cn.campushub.model.CommentCreateResult;
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

public class PostCommentServlet extends HttpServlet {
    private final PostService postService = new PostService();

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
            ServiceResult<CommentCreateResult> result =
                    postService.comment(postId, user.id(), request.getParameter("content"));
            if (!result.success()) {
                PostJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        result.message()
                );
                return;
            }
            Comment comment = result.data().comment();
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "commentCount", result.data().commentCount(),
                            "comment", Map.of(
                                    "id", comment.getId(),
                                    "content", comment.getContent(),
                                    "nickname", PostJsonSupport.valueOrEmpty(
                                            comment.getAuthorNickname()
                                    ),
                                    "avatar", PostJsonSupport.valueOrEmpty(
                                            comment.getAuthorAvatar()
                                    ),
                                    "createdAt", "刚刚"
                            )
                    )
            );
        } catch (SQLException exception) {
            log("发表评论失败", exception);
            PostJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "发表评论失败"
            );
        }
    }
}
