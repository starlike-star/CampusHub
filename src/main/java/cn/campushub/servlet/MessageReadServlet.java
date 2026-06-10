package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.MessageService;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class MessageReadServlet extends HttpServlet {
    private final MessageService messageService = new MessageService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            MessageJsonSupport.writeNeedLogin(response);
            return;
        }
        Long messageId =
                MessageJsonSupport.parsePositiveId(request.getParameter("messageId"));
        if (messageId == null) {
            MessageJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "消息参数无效"
            );
            return;
        }
        try {
            if (!messageService.markRead(user.id(), messageId)) {
                MessageJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "消息不存在"
                );
                return;
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "unreadCount", messageService.countUnread(user.id())
                    )
            );
        } catch (SQLException exception) {
            log("标记消息已读失败", exception);
            MessageJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "消息状态更新失败"
            );
        }
    }
}
