package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.PrivateMessageService;
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

/**
 * 接收私信消息的发送请求，调用业务层并生成 HTTP 响应。
 */
public class PrivateMessageSendServlet extends HttpServlet {
    private final PrivateMessageService service = new PrivateMessageService();

    /**
     * 处理`PrivateMessageSend`相关的 HTTP POST 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            PrivateMessageJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "请先登录"
            );
            return;
        }
        Long receiverId = PrivateMessageJsonSupport.parsePositiveLong(
                request.getParameter("receiverId")
        );
        if (receiverId == null) {
            PrivateMessageJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "收件人参数无效"
            );
            return;
        }
        try {
            ServiceResult<Long> result = service.sendMessage(
                    user.id(),
                    receiverId,
                    request.getParameter("content")
            );
            if (!result.success()) {
                PrivateMessageJsonSupport.writeError(
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
                            "conversationId", result.data()
                    )
            );
        } catch (SQLException exception) {
            log("发送私信失败", exception);
            PrivateMessageJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "发送失败，请稍后重试"
            );
        }
    }
}
