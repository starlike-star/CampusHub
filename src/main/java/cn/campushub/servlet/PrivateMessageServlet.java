package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.PrivateMessageService;
import cn.campushub.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 接收私信消息的请求处理请求，调用业务层并生成 HTTP 响应。
 */
public class PrivateMessageServlet extends HttpServlet {
    private final PrivateMessageService service = new PrivateMessageService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            request.setAttribute(
                    "conversations",
                    service.listConversations(user.id())
            );
            request.setAttribute(
                    "privateUnreadCount",
                    service.countUnreadPrivateMessages(user.id())
            );
            request.getRequestDispatcher("/WEB-INF/views/privateMessages.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            log("加载私信会话失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "私信会话加载失败"
            );
        }
    }
}
