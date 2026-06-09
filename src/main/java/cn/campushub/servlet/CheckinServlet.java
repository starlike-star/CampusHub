package cn.campushub.servlet;

import cn.campushub.model.CheckinResult;
import cn.campushub.model.SessionUser;
import cn.campushub.service.HomeService;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CheckinServlet extends HttpServlet {
    private final HomeService homeService = new HomeService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    Map.of(
                            "success", false,
                            "checkedIn", false,
                            "needLogin", true,
                            "message", "请先登录"
                    )
            );
            return;
        }

        try {
            CheckinResult result = homeService.checkIn(user.id());
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("success", result.success());
            data.put("checkedIn", result.checkedIn());
            if (result.success()) {
                data.put("points", result.points());
                data.put("continuousDays", result.continuousDays());
            }
            data.put("message", result.message());
            JsonUtils.write(response, HttpServletResponse.SC_OK, data);
        } catch (SQLException exception) {
            log("每日签到失败", exception);
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of(
                            "success", false,
                            "checkedIn", false,
                            "message", "签到失败，请稍后重试"
                    )
            );
        }
    }
}
