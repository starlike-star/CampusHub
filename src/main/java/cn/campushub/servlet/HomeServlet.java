package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.HomeService;
import cn.campushub.service.PostService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 渲染应用主页面并准备当前登录用户等基础数据。
 */
public class HomeServlet extends HttpServlet {
    private final PostService postService = new PostService();
    private final HomeService homeService = new HomeService();

    /**
     * 处理首页相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
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
            request.setAttribute(
                    "sidebar",
                    homeService.loadSidebar(user == null ? null : user.id())
            );
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
        } catch (SQLException exception) {
            log("加载首页数据失败", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "首页数据加载失败");
        }
    }
}
