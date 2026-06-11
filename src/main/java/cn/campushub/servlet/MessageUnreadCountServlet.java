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

public class MessageUnreadCountServlet extends HttpServlet {
    private final MessageService messageService = new MessageService();
    private final PrivateMessageService privateMessageService =
            new PrivateMessageService();

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
