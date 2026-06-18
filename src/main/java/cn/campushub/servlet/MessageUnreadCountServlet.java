package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.MessageService;
import cn.campushub.service.PrivateMessageService;
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
 * 接收站内通知的未读数量查询请求，调用业务层并生成 HTTP 响应。
 */
public class MessageUnreadCountServlet extends HttpServlet {
    private final MessageService messageService = new MessageService();
    private final PrivateMessageService privateMessageService =
            new PrivateMessageService();

    /**
     * 处理消息未读数量相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            MessageJsonSupport.writeNeedLogin(response);
            return;
        }
        try {
            int systemUnreadCount = messageService.countUnread(user.id());
            int privateUnreadCount =
                    privateMessageService.countUnreadPrivateMessages(user.id());
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "unreadCount",
                            systemUnreadCount + privateUnreadCount,
                            "systemUnreadCount",
                            systemUnreadCount,
                            "privateUnreadCount",
                            privateUnreadCount
                    )
            );
        } catch (SQLException exception) {
            log("查询未读消息数失败", exception);
            MessageJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "未读消息加载失败"
            );
        }
    }
}
