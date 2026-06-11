package cn.campushub.servlet;

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
 * 接收活动的删除请求，调用业务层并生成 HTTP 响应。
 */
public class ActivityDeleteServlet extends HttpServlet {
    private final ActivityService activityService = new ActivityService();

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
            ServiceResult<Void> result = activityService.updateStatus(
                    id,
                    user.id(),
                    ActivityJsonSupport.isAdmin(user),
                    "finished"
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
                    Map.of("success", true, "message", "活动已结束")
            );
        } catch (SQLException exception) {
            log("结束活动失败", exception);
            ActivityJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "活动结束失败"
            );
        }
    }
}
