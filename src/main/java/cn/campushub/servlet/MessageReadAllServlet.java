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
 * 接收站内通知的全部标记已读请求，调用业务层并生成 HTTP 响应。
 */
public class MessageReadAllServlet extends HttpServlet {
    private final MessageService messageService = new MessageService();
    private final PrivateMessageService privateMessageService =
            new PrivateMessageService();

    /**
     * 处理消息已读状态全部数据相关的 HTTP POST 请求并生成响应。
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
            MessageJsonSupport.writeNeedLogin(response);
            return;
        }
        try {
            messageService.markAllRead(user.id(), request.getParameter("type"));
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
        } catch (IllegalArgumentException exception) {
            MessageJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );
        } catch (SQLException exception) {
            log("全部标记消息已读失败", exception);
            MessageJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "消息状态更新失败"
            );
        }
    }
}
