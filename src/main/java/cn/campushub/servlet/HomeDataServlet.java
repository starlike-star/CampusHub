package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.model.Post;
import cn.campushub.service.PostService;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 为首页异步请求聚合动态列表与侧栏统计数据。
 */
public class HomeDataServlet extends HttpServlet {
    private final PostService postService = new PostService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SessionUser user = SessionUtils.currentUser(request);

        try {
            List<Map<String, Object>> posts = postService.listPosts(
                            user == null ? null : user.id()
                    ).stream()
                    .map(this::toPostMap)
                    .toList();
            Map<String, Object> data = Map.of(
                    "success", true,
                    "authenticated", user != null,
                    "currentUser", user == null ? Map.of() : Map.of(
                            "id", user.id(),
                            "username", user.username(),
                            "nickname", user.nickname(),
                            "avatar", user.avatar() == null ? "" : user.avatar(),
                            "role", user.role()
                    ),
                    "posts", posts
            );
            JsonUtils.write(response, HttpServletResponse.SC_OK, data);
        } catch (SQLException exception) {
            log("加载首页帖子接口失败", exception);
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("success", false, "message", "首页数据加载失败")
            );
        }
    }

    private Map<String, Object> toPostMap(Post post) {
        return Map.ofEntries(
                Map.entry("id", post.getId()),
                Map.entry("authorAvatar", valueOrEmpty(post.getAuthorAvatar())),
                Map.entry("authorNickname", valueOrEmpty(post.getAuthorNickname())),
                Map.entry("authorCollege", valueOrEmpty(post.getAuthorCollege())),
                Map.entry("authorGrade", valueOrEmpty(post.getAuthorGrade())),
                Map.entry("categoryName", valueOrEmpty(post.getCategoryName())),
                Map.entry("topic", valueOrEmpty(post.getTopic())),
                Map.entry("title", valueOrEmpty(post.getTitle())),
                Map.entry("summary", post.getSummary()),
                Map.entry("images", post.getImageList()),
                Map.entry("likeCount", post.getLikeCount()),
                Map.entry("commentCount", post.getCommentCount()),
                Map.entry("favoriteCount", post.getFavoriteCount()),
                Map.entry("viewCount", post.getViewCount()),
                Map.entry("liked", post.isLiked()),
                Map.entry("favorited", post.isFavorited()),
                Map.entry(
                        "createdAt",
                        post.getCreatedAt() == null ? "" : post.getCreatedAt().toString()
                )
        );
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
