package cn.campushub.servlet;

import cn.campushub.model.ActivityVO;
import cn.campushub.model.SessionUser;
import cn.campushub.service.ActivityRegistrationService;
import cn.campushub.service.ActivityService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 接收活动的详情查询请求，调用业务层并生成 HTTP 响应。
 */
public class ActivityDetailServlet extends HttpServlet {
    private final ActivityService activityService = new ActivityService();
    private final ActivityRegistrationService registrationService =
            new ActivityRegistrationService();

    /**
     * 处理活动详情相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = ActivityJsonSupport.parseId(request);
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "活动参数无效");
            return;
        }
        SessionUser user = SessionUtils.currentUser(request);
        try {
            Optional<ActivityVO> optional = activityService.detail(id);
            if (optional.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "活动不存在");
                return;
            }
            ActivityVO activity = optional.get();
            boolean owner = user != null
                    && activity.activity().getCreatedBy() != null
                    && activity.activity().getCreatedBy() == user.id();
            boolean canManage = owner || ActivityJsonSupport.isAdmin(user);
            request.setAttribute("activity", activity);
            request.setAttribute("activityOwner", owner);
            request.setAttribute(
                    "registered",
                    user != null && registrationService.isRegistered(id, user.id())
            );
            request.setAttribute("canManageActivity", canManage);
            if (canManage) {
                request.setAttribute(
                        "activityRegistrations",
                        registrationService.registrations(id)
                );
            }
            request.getRequestDispatcher("/WEB-INF/views/activityDetail.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            log("加载活动详情失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "活动详情加载失败"
            );
        }
    }
}
