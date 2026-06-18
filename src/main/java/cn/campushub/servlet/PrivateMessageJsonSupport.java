package cn.campushub.servlet;

import cn.campushub.util.JsonUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 为私信消息接口提供统一的 JSON 响应和参数处理辅助能力。
 */
final class PrivateMessageJsonSupport {
    /**
     * 初始化`PrivateMessageJsonSupport`对象及其运行所需依赖。
     */
    private PrivateMessageJsonSupport() {
    }

    /**
     * 解析`PositiveLong`。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    static Long parsePositiveLong(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
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
}
