package cn.campushub.servlet;

import cn.campushub.util.JsonUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

final class PrivateMessageJsonSupport {
    private PrivateMessageJsonSupport() {
    }

    static Long parsePositiveLong(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
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
