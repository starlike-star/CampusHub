package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.AdminService;
import cn.campushub.service.ServiceResult;
import cn.campushub.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * 处理后台管理页面请求并聚合管理端所需数据。
 */
public class AdminServlet extends HttpServlet {
    private static final Set<String> SECTIONS = Set.of(
            "dashboard",
            "users",
            "posts",
            "goods",
            "lostfound",
            "activities",
            "notices",
            "reports"
    );

    private final AdminService adminService = new AdminService();

    /**
     * 处理管理员相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String section = section(request);
        if (section == null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        request.setAttribute("section", section);
        request.setAttribute("filters", Map.of(
                "keyword", value(request, "keyword"),
                "role", value(request, "role"),
                "status", value(request, "status"),
                "categoryId", value(request, "categoryId"),
                "tradeMethod", value(request, "tradeMethod"),
                "type", value(request, "type"),
                "targetType", value(request, "targetType")
        ));
        try {
            loadSection(request, section);
        } catch (SQLException exception) {
            log("加载后台管理数据失败", exception);
            request.setAttribute("loadError", "后台数据暂时无法加载，请稍后重试");
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp")
                .forward(request, response);
    }

    /**
     * 处理管理员相关的 HTTP POST 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String action = action(request);
        SessionUser admin = SessionUtils.currentUser(request);
        if (action == null || admin == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String returnSection = action.substring(0, action.indexOf('/'));
        ServiceResult<Void> result;
        try {
            result = execute(request, action, admin);
        } catch (SQLException exception) {
            log("执行后台管理操作失败: " + action, exception);
            result = ServiceResult.failure("数据库操作失败，请稍后重试");
        }
        String parameter = result.success() ? "message" : "error";
        response.sendRedirect(
                request.getContextPath()
                        + "/admin/"
                        + returnSection
                        + "?"
                        + parameter
                        + "="
                        + URLEncoder.encode(result.message(), StandardCharsets.UTF_8)
        );
    }

    /**
     * 加载`Section`。
     *
     * @param request HTTP 请求对象
     * @param section 参数 `section`
     * @throws SQLException 数据库访问失败时抛出
     */
    private void loadSection(HttpServletRequest request, String section)
            throws SQLException {
        switch (section) {
            case "dashboard" -> request.setAttribute(
                    "stats",
                    adminService.dashboard()
            );
            case "users" -> request.setAttribute(
                    "rows",
                    adminService.users(
                            request.getParameter("keyword"),
                            request.getParameter("role"),
                            request.getParameter("status")
                    )
            );
            case "posts" -> {
                request.setAttribute(
                        "rows",
                        adminService.posts(
                                request.getParameter("keyword"),
                                request.getParameter("status"),
                                request.getParameter("categoryId")
                        )
                );
                request.setAttribute("categories", adminService.postCategories());
            }
            case "goods" -> request.setAttribute(
                    "rows",
                    adminService.goods(
                            request.getParameter("keyword"),
                            request.getParameter("status"),
                            request.getParameter("tradeMethod")
                    )
            );
            case "lostfound" -> request.setAttribute(
                    "rows",
                    adminService.lostFound(
                            request.getParameter("keyword"),
                            request.getParameter("type"),
                            request.getParameter("status")
                    )
            );
            case "activities" -> request.setAttribute(
                    "rows",
                    adminService.activities(
                            request.getParameter("keyword"),
                            request.getParameter("status")
                    )
            );
            case "notices" -> request.setAttribute(
                    "rows",
                    adminService.notices(request.getParameter("type"))
            );
            case "reports" -> request.setAttribute(
                    "rows",
                    adminService.reports(
                            request.getParameter("status"),
                            request.getParameter("targetType")
                    )
            );
            default -> throw new IllegalArgumentException("未知后台模块");
        }
    }

    /**
     * 根据输入计算并返回 `execute` 的处理结果。
     *
     * @param request HTTP 请求对象
     * @param action 参数 `action`
     * @param admin 是否具有管理员权限
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private ServiceResult<Void> execute(
            HttpServletRequest request,
            String action,
            SessionUser admin
    ) throws SQLException {
        return switch (action) {
            case "users/status" -> adminService.updateUserStatus(
                    admin.id(),
                    request.getParameter("id"),
                    request.getParameter("status")
            );
            case "users/reset-password" -> adminService.resetPassword(
                    request.getParameter("id"),
                    request.getParameter("password")
            );
            case "posts/status" -> adminService.updatePostStatus(
                    request.getParameter("id"),
                    request.getParameter("status")
            );
            case "goods/status" -> adminService.updateGoodsStatus(
                    request.getParameter("id"),
                    request.getParameter("status")
            );
            case "lostfound/status" -> adminService.updateLostFoundStatus(
                    request.getParameter("id"),
                    request.getParameter("status")
            );
            case "activities/status" -> adminService.updateActivityStatus(
                    request.getParameter("id"),
                    request.getParameter("status")
            );
            case "notices/create" -> adminService.createNotice(
                    request.getParameter("title"),
                    request.getParameter("content"),
                    request.getParameter("type"),
                    admin.id()
            );
            case "notices/update" -> adminService.updateNotice(
                    request.getParameter("id"),
                    request.getParameter("title"),
                    request.getParameter("content"),
                    request.getParameter("type")
            );
            case "notices/status" -> adminService.updateNoticeStatus(
                    request.getParameter("id"),
                    request.getParameter("status")
            );
            case "notices/top" -> adminService.updateNoticeTop(
                    request.getParameter("id"),
                    request.getParameter("top")
            );
            case "reports/handle" -> adminService.handleReport(
                    request.getParameter("id"),
                    admin.id()
            );
            case "reports/reject" -> adminService.rejectReport(
                    request.getParameter("id"),
                    admin.id()
            );
            default -> ServiceResult.failure("未知后台操作");
        };
    }

    /**
     * 根据输入计算并返回 `section` 的处理结果。
     *
     * @param request HTTP 请求对象
     * @return 方法处理结果
     */
    private String section(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null || "/".equals(path)) {
            return null;
        }
        String section = path.substring(1);
        return SECTIONS.contains(section) ? section : null;
    }

    /**
     * 根据输入计算并返回 `action` 的处理结果。
     *
     * @param request HTTP 请求对象
     * @return 方法处理结果
     */
    private String action(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null || path.length() <= 1) {
            return null;
        }
        String action = path.substring(1);
        return action.contains("/") ? action : null;
    }

    /**
     * 根据输入计算并返回 `value` 的处理结果。
     *
     * @param request HTTP 请求对象
     * @param name 参数 `name`
     * @return 方法处理结果
     */
    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value;
    }
}
