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

public class ActivityCreateServlet extends HttpServlet {
    private final ActivityService activityService = new ActivityService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            ActivityJsonSupport.writeNeedLogin(response);
            return;
        }
        try {
            ServiceResult<Long> result = activityService.create(
                    user.id(),
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
                            "id", result.data()
                    )
            );
        } catch (SQLException exception) {
            log("发布活动失败", exception);
            ActivityJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "活动发布失败"
            );
        }
    }
}
