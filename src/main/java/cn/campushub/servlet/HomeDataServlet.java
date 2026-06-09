package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HomeDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SessionUser user = SessionUtils.currentUser(request);

        // 当前返回模拟数据，后续可分别接入帖子、公告、活动和失物招领业务服务。
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
                "tabs", List.of("latest", "hot", "following"),
                "posts", List.of(),
                "announcements", List.of(),
                "activities", List.of(),
                "lostAndFound", List.of()
        );
        JsonUtils.write(response, HttpServletResponse.SC_OK, data);
    }
}
