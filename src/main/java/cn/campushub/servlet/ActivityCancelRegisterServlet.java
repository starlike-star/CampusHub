package cn.campushub.servlet;

import cn.campushub.model.ActivityRegistrationResult;
import cn.campushub.model.SessionUser;
import cn.campushub.service.ActivityRegistrationService;
import cn.campushub.service.ServiceResult;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 接收活动的取消报名请求，调用业务层并生成 HTTP 响应。
 */
public class ActivityCancelRegisterServlet extends HttpServlet {
    private final ActivityRegistrationService registrationService =
            new ActivityRegistrationService();

    /**
     * 处理`ActivityCancelRegister`相关的 HTTP POST 请求并生成响应。
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
        Long activityId = ActivityJsonSupport.parseId(request);
        if (activityId == null) {
            ActivityJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "活动参数无效"
            );
            return;
        }
        try {
            ServiceResult<ActivityRegistrationResult> result =
                    registrationService.cancel(activityId, user.id());
            ActivityRegisterServlet.writeResult(response, result);
        } catch (SQLException exception) {
            log("取消活动报名失败", exception);
            ActivityJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "取消报名失败"
            );
        }
    }
}
