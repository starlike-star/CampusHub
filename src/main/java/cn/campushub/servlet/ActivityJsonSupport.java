package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.util.JsonUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

final class ActivityJsonSupport {
    private ActivityJsonSupport() {
    }

    static Long parseId(HttpServletRequest request) {
        String value = request.getParameter("activityId");
        if (value == null) {
            value = request.getParameter("id");
        }
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    static boolean isAdmin(SessionUser user) {
        return user != null && "admin".equalsIgnoreCase(user.role());
    }

    static void writeNeedLogin(HttpServletResponse response) throws IOException {
        JsonUtils.write(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                Map.of(
                        "success", false,
                        "needLogin", true,
                        "message", "请先登录"
                )
        );
    }

    static void writeError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {
        JsonUtils.write(
                response,
                status,
                Map.of("success", false, "message", message)
        );
    }
}
