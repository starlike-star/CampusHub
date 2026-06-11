package cn.campushub.servlet;

import cn.campushub.util.JsonUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 为帖子接口提供统一的 JSON 响应和参数处理辅助能力。
 */
final class PostJsonSupport {
    private PostJsonSupport() {
    }

    static Long parsePostId(HttpServletRequest request) {
        String value = request.getParameter("postId");
        if (value == null) {
            value = request.getParameter("post_id");
        }
        try {
            long postId = Long.parseLong(value);
            return postId > 0 ? postId : null;
        } catch (NumberFormatException exception) {
            return null;
        }
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

    static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
