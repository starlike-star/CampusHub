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
    /**
     * 初始化`PostJsonSupport`对象及其运行所需依赖。
     */
    private PostJsonSupport() {
    }

    /**
     * 解析帖子编号。
     *
     * @param request HTTP 请求对象
     * @return 解析后的值；输入无效时返回 null
     */
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

    /**
     * 写入`NeedLogin`。
     *
     * @param response HTTP 响应对象
     * @throws IOException 读取请求或写入响应失败时抛出
     */
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

    /**
     * 写入`Error`。
     *
     * @param response HTTP 响应对象
     * @param status 业务状态
     * @param message 消息数据
     * @throws IOException 读取请求或写入响应失败时抛出
     */
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

    /**
     * 根据输入计算并返回 `valueOrEmpty` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
