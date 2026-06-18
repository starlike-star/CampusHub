package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.util.JsonUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 为商品接口提供统一的 JSON 响应和参数处理辅助能力。
 */
final class GoodsJsonSupport {
    /**
     * 初始化`GoodsJsonSupport`对象及其运行所需依赖。
     */
    private GoodsJsonSupport() {
    }

    /**
     * 解析商品编号。
     *
     * @param request HTTP 请求对象
     * @return 解析后的值；输入无效时返回 null
     */
    static Long parseGoodsId(HttpServletRequest request) {
        String value = request.getParameter("goodsId");
        if (value == null) {
            value = request.getParameter("id");
        }
        try {
            long goodsId = Long.parseLong(value);
            return goodsId > 0 ? goodsId : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * 判断是否管理员。
     *
     * @param user 用户数据
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    static boolean isAdmin(SessionUser user) {
        return user != null && "admin".equalsIgnoreCase(user.role());
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
}
