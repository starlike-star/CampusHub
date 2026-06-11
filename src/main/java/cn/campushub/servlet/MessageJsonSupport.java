package cn.campushub.servlet;

import cn.campushub.util.JsonUtils;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 为站内通知接口提供统一的 JSON 响应和参数处理辅助能力。
 */
final class MessageJsonSupport {
    private MessageJsonSupport() {
    }

    static void writeNeedLogin(HttpServletResponse response) throws IOException {
        JsonUtils.write(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                Map.of(
                        "success", false,
                        "needLogin", true,
                        "unreadCount", 0,
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

    static Long parsePositiveId(String value) {
        try {
            long id = Long.parseLong(value);
            return id > 0 ? id : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
