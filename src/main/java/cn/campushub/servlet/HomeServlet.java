package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.PostService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class HomeServlet extends HttpServlet {
    private final PostService postService = new PostService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            SessionUser user = SessionUtils.currentUser(request);
            request.setAttribute(
                    "posts",
                    postService.listPosts(user == null ? null : user.id())
            );
            request.setAttribute("categories", postService.listCategories());
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
        } catch (SQLException exception) {
            log("加载首页帖子流失败", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "首页数据加载失败");
        }
    }
}
