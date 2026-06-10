package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.GoodsService;
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
            Set.of("lostfound", "activity", "profile");
    private static final Map<String, String> DEVELOPMENT_TITLES = Map.of(
            "lostfound", "失物招领",
            "activity", "校园活动",
            "profile", "个人中心"
    );

    private final PostService postService = new PostService();
    private final SquareService squareService = new SquareService();
    private final GoodsService goodsService = new GoodsService();

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
            if ("market".equals(page)) {
                String keyword =
                        goodsService.normalizeKeyword(
                                request.getParameter("keyword")
                        );
                Long categoryId =
                        goodsService.normalizeCategoryId(
                                request.getParameter("categoryId")
                        );
                String status =
                        goodsService.normalizeListStatus(
                                request.getParameter("status")
                        );
                String tradeMethod =
                        goodsService.normalizeTradeMethodFilter(
                                request.getParameter("tradeMethod")
                        );
                String sort =
                        goodsService.normalizeSort(request.getParameter("sort"));
                request.setAttribute("keyword", keyword);
                request.setAttribute("selectedCategoryId", categoryId);
                request.setAttribute("selectedStatus", status);
                request.setAttribute("selectedTradeMethod", tradeMethod);
                request.setAttribute("selectedSort", sort);
                request.setAttribute(
                        "goodsCategories",
                        goodsService.listCategories()
                );
                request.setAttribute(
                        "goodsList",
                        goodsService.list(
                                userId,
                                keyword,
                                categoryId == null
                                        ? null
                                        : categoryId.toString(),
                                status,
                                tradeMethod,
                                sort
                        )
                );
                forward(request, response, "market.jsp");
                return;
            }
            if ("myGoods".equals(page)) {
                request.setAttribute("loginRequired", user == null);
                request.setAttribute(
                        "goodsCategories",
                        goodsService.listCategories()
                );
                request.setAttribute(
                        "goodsList",
                        user == null
                                ? java.util.List.of()
                                : goodsService.listOwnGoods(user.id())
                );
                forward(request, response, "my-goods.jsp");
                return;
            }
            if ("favorites".equals(page)) {
                request.setAttribute("loginRequired", user == null);
                request.setAttribute(
                        "goodsList",
                        user == null
                                ? java.util.List.of()
                                : goodsService.listFavoriteGoods(user.id())
                );
                forward(request, response, "favorites.jsp");
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
