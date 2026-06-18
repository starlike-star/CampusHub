package cn.campushub.servlet;

import cn.campushub.util.JsonUtils;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 为站内通知接口提供统一的 JSON 响应和参数处理辅助能力。
 */
final class MessageJsonSupport {
    /**
     * 初始化`MessageJsonSupport`对象及其运行所需依赖。
     */
    private MessageJsonSupport() {
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
                        "unreadCount", 0,
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
     * 解析`PositiveId`。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    static Long parsePositiveId(String value) {
        try {
            long id = Long.parseLong(value);
            return id > 0 ? id : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
