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

public class ActivityCancelRegisterServlet extends HttpServlet {
    private final ActivityRegistrationService registrationService =
            new ActivityRegistrationService();

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
