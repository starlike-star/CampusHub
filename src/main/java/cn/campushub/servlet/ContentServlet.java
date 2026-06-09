package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.PostService;
import cn.campushub.service.SquareService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class ContentServlet extends HttpServlet {
    private static final Set<String> DEVELOPMENT_PAGES =
            Set.of("market", "lostfound", "activity", "favorites", "profile");
    private static final Map<String, String> DEVELOPMENT_TITLES = Map.of(
            "market", "二手市场",
            "lostfound", "失物招领",
            "activity", "校园活动",
            "favorites", "我的收藏",
            "profile", "个人中心"
    );

    private final PostService postService = new PostService();
    private final SquareService squareService = new SquareService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String page = request.getParameter("page");
        SessionUser user = SessionUtils.currentUser(request);
        Long userId = user == null ? null : user.id();
        try {
            if ("home".equals(page)) {
                request.setAttribute("posts", postService.listPosts(userId));
                forward(request, response, "home-feed.jsp");
                return;
            }
            if ("square".equals(page)) {
                String tab = squareService.normalizeTab(request.getParameter("tab"));
                String keyword =
                        squareService.normalizeKeyword(request.getParameter("q"));
                request.setAttribute("activeTab", tab);
                request.setAttribute("keyword", keyword);
                if ("notice".equals(tab)) {
                    request.setAttribute(
                            "notices",
                            squareService.listNotices(keyword)
                    );
                } else {
                    request.setAttribute(
                            "posts",
                            squareService.listPosts(tab, userId, keyword)
                    );
                }
                forward(request, response, "square.jsp");
                return;
            }
            if (page != null && DEVELOPMENT_PAGES.contains(page)) {
                request.setAttribute("moduleName", DEVELOPMENT_TITLES.get(page));
                forward(request, response, "development.jsp");
                return;
            }
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("moduleName", "未找到该模块");
            forward(request, response, "development.jsp");
        } catch (SQLException exception) {
            log("加载内容片段失败: " + page, exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "内容加载失败"
            );
        }
    }

    private void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            String fragment
    ) throws ServletException, IOException {
        request.getRequestDispatcher(
                "/WEB-INF/views/fragments/" + fragment
        ).forward(request, response);
    }
}
