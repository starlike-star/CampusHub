package cn.campushub.servlet;

import cn.campushub.model.Activity;
import cn.campushub.model.SessionUser;
import cn.campushub.service.ActivityService;
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

/**
 * 接收活动的更新请求，调用业务层并生成 HTTP 响应。
 */
public class ActivityUpdateServlet extends HttpServlet {
    private final ActivityService activityService = new ActivityService();

    /**
     * 处理活动更新相关的 HTTP POST 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            ActivityJsonSupport.writeNeedLogin(response);
            return;
        }
        Long id = ActivityJsonSupport.parseId(request);
        if (id == null) {
            ActivityJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "活动参数无效"
            );
            return;
        }
        try {
            ServiceResult<Activity> result = activityService.update(
                    id,
                    user.id(),
                    ActivityJsonSupport.isAdmin(user),
                    request.getParameter("title"),
                    request.getParameter("content"),
                    request.getParameter("coverImage"),
                    request.getParameter("location"),
                    request.getParameter("startTime"),
                    request.getParameter("endTime"),
                    request.getParameter("deadline"),
                    request.getParameter("maxMembers")
            );
            if (!result.success()) {
                ActivityJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        result.message()
                );
                return;
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of("success", true, "message", result.message())
            );
        } catch (SQLException exception) {
            log("更新活动失败", exception);
            ActivityJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "活动更新失败"
            );
        }
    }
}
