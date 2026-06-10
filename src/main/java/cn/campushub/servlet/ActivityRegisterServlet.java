package cn.campushub.servlet;

import cn.campushub.model.ActivityRegistrationResult;
import cn.campushub.model.SessionUser;
import cn.campushub.service.ActivityRegistrationService;
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

public class ActivityRegisterServlet extends HttpServlet {
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
                    registrationService.register(
                            activityId,
                            user.id(),
                            user.nickname()
                    );
            writeResult(response, result);
        } catch (SQLException exception) {
            log("活动报名失败", exception);
            ActivityJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "活动报名失败"
            );
        }
    }

    static void writeResult(
            HttpServletResponse response,
            ServiceResult<ActivityRegistrationResult> result
    ) throws IOException {
        if (!result.success()) {
            ActivityJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    result.message()
            );
            return;
        }
        JsonUtils.write(
                response,
                HttpServletResponse.SC_OK,
                Map.of(
                        "success", true,
                        "message", result.message(),
                        "registered", result.data().registered(),
                        "currentMembers", result.data().currentMembers()
                )
        );
    }
}
