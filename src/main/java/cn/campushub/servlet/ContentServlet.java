package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.GoodsService;
import cn.campushub.service.LostFoundService;
import cn.campushub.service.MessageService;
import cn.campushub.service.PrivateMessageService;
import cn.campushub.service.PostService;
import cn.campushub.service.ProfileService;
import cn.campushub.service.SearchService;
import cn.campushub.service.SquareService;
import cn.campushub.service.ActivityRegistrationService;
import cn.campushub.service.ActivityService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * 根据前端路由参数分发并渲染各业务页面片段。
 */
public class ContentServlet extends HttpServlet {
    private static final Set<String> DEVELOPMENT_PAGES =
            Set.of("activity");
    private static final Map<String, String> DEVELOPMENT_TITLES = Map.of(
            "activity", "校园活动"
    );

    private final PostService postService = new PostService();
    private final SquareService squareService = new SquareService();
    private final GoodsService goodsService = new GoodsService();
    private final LostFoundService lostFoundService = new LostFoundService();
    private final ProfileService profileService = new ProfileService();
    private final MessageService messageService = new MessageService();
    private final PrivateMessageService privateMessageService =
            new PrivateMessageService();
    private final ActivityService activityService = new ActivityService();
    private final SearchService searchService = new SearchService();
    private final ActivityRegistrationService activityRegistrationService =
            new ActivityRegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String page = request.getParameter("page");
        SessionUser user = SessionUtils.currentUser(request);
        Long userId = user == null ? null : user.id();
        try {
            if ("search".equals(page)) {
                request.setAttribute(
                        "searchPage",
                        searchService.search(
                                request.getParameter("keyword"),
                                request.getParameter("type")
                        )
                );
                forward(request, response, "search.jsp");
                return;
            }
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
            if ("lostfound".equals(page)) {
                String type = lostFoundService.normalizeTypeValue(
                        request.getParameter("type")
                );
                String status = lostFoundService.normalizeStatusValue(
                        request.getParameter("status")
                );
                String keyword = lostFoundService.normalizeKeyword(
                        request.getParameter("keyword")
                );
                String sort = lostFoundService.normalizeSort(
                        request.getParameter("sort")
                );
                Long categoryId = lostFoundService.normalizeCategoryId(
                        request.getParameter("categoryId")
                );
                request.setAttribute("selectedType", type);
                request.setAttribute("selectedStatus", status);
                request.setAttribute("keyword", keyword);
                request.setAttribute("selectedSort", sort);
                request.setAttribute("selectedCategoryId", categoryId);
                request.setAttribute(
                        "lostFoundCategories",
                        lostFoundService.listCategories()
                );
                request.setAttribute(
                        "lostFoundItems",
                        lostFoundService.list(
                                type,
                                status,
                                keyword,
                                categoryId == null
                                        ? null
                                        : categoryId.toString(),
                                sort
                        )
                );
                forward(request, response, "lostfound.jsp");
                return;
            }
            if ("activity".equals(page)) {
                String status = activityService.normalizeStatusValue(
                        request.getParameter("status")
                );
                String keyword = activityService.normalizeKeyword(
                        request.getParameter("keyword")
                );
                String sort = activityService.normalizeSort(
                        request.getParameter("sort")
                );
                request.setAttribute("selectedStatus", status);
                request.setAttribute("keyword", keyword);
                request.setAttribute("selectedSort", sort);
                request.setAttribute(
                        "activities",
                        activityService.list(status, keyword, sort)
                );
                request.setAttribute(
                        "registeredActivityIds",
                        user == null
                                ? java.util.Set.of()
                                : activityRegistrationService
                                        .registeredActivityIds(user.id())
                );
                request.setAttribute("activityLoggedIn", user != null);
                forward(request, response, "activity.jsp");
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
            if ("profile".equals(page)) {
                String tab = profileService.normalizeTab(
                        request.getParameter("tab")
                );
                request.setAttribute("activeTab", tab);
                request.setAttribute("loginRequired", user == null);
                if (user != null) {
                    request.setAttribute(
                            "profileOverview",
                            profileService.overview(user.id()).orElse(null)
                    );
                    switch (tab) {
                        case "posts" -> {
                            request.setAttribute(
                                    "posts",
                                    profileService.posts(user.id())
                            );
                            request.setAttribute("emptyTitle", "还没有发布帖子");
                            request.setAttribute(
                                    "emptyMessage",
                                    "发布第一条校园动态，记录你的校园生活。"
                            );
                        }
                        case "comments" -> request.setAttribute(
                                "profileComments",
                                profileService.comments(user.id())
                        );
                        case "favorites" -> request.setAttribute(
                                "profileFavorites",
                                profileService.favorites(user.id())
                        );
                        case "goods" -> {
                            request.setAttribute(
                                    "goodsCategories",
                                    goodsService.listCategories()
                            );
                            request.setAttribute(
                                    "goodsList",
                                    goodsService.listOwnGoods(user.id())
                            );
                        }
                        case "purchasedGoods" -> request.setAttribute(
                                "purchasedGoods",
                                profileService.purchasedGoods(user.id())
                        );
                        case "lostfound" -> request.setAttribute(
                                "profileLostFound",
                                lostFoundService.listOwn(user.id())
                        );
                        case "checkins" -> request.setAttribute(
                                "profileCheckins",
                                profileService.checkins(user.id())
                        );
                        case "activities" -> request.setAttribute(
                                "profileActivities",
                                profileService.activities(user.id())
                        );
                        default -> {
                        }
                    }
                }
                forward(request, response, "profile.jsp");
                return;
            }
            if ("messages".equals(page)) {
                String tab = messageService.normalizeTab(
                        request.getParameter("tab")
                );
                request.setAttribute("activeTab", tab);
                request.setAttribute("loginRequired", user == null);
                if (user != null) {
                    request.setAttribute(
                            "messages",
                            messageService.listMessages(user.id(), tab)
                    );
                    request.setAttribute(
                            "messageCount",
                            messageService.countMessages(user.id())
                    );
                    request.setAttribute(
                            "unreadCount",
                            messageService.countUnread(user.id())
                    );
                    request.setAttribute(
                            "privateUnreadCount",
                            privateMessageService.countUnreadPrivateMessages(user.id())
                    );
                }
                forward(request, response, "messages.jsp");
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
