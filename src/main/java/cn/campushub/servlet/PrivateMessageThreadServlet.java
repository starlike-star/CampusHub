package cn.campushub.servlet;

import cn.campushub.model.PrivateConversation;
import cn.campushub.model.SessionUser;
import cn.campushub.service.PrivateMessageService;
import cn.campushub.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 接收私信消息的会话详情请求，调用业务层并生成 HTTP 响应。
 */
public class PrivateMessageThreadServlet extends HttpServlet {
    private final PrivateMessageService service = new PrivateMessageService();

    /**
     * 处理`PrivateMessageThread`相关的 HTTP GET 请求并生成响应。
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
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Long conversationId = PrivateMessageJsonSupport.parsePositiveLong(
                request.getParameter("conversationId")
        );
        Long receiverId = PrivateMessageJsonSupport.parsePositiveLong(
                request.getParameter("receiverId")
        );
        try {
            if (conversationId == null && receiverId != null) {
                conversationId = service.getOrCreateConversation(
                        user.id(),
                        receiverId
                );
            }
            if (conversationId == null) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "私信会话参数无效"
                );
                return;
            }
            Optional<PrivateConversation> conversation =
                    service.findConversation(conversationId, user.id());
            if (conversation.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "无权访问该私信会话"
                );
                return;
            }
            service.markConversationRead(conversationId, user.id());
            request.setAttribute("conversation", conversation.get());
            request.setAttribute(
                    "privateMessages",
                    service.listMessages(conversationId, user.id())
            );
            request.setAttribute("currentUserId", user.id());
            request.getRequestDispatcher("/WEB-INF/views/privateMessageThread.jsp")
                    .forward(request, response);
        } catch (IllegalArgumentException exception) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );
        } catch (SecurityException exception) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    exception.getMessage()
            );
        } catch (SQLException exception) {
            log("加载私信消息失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "私信消息加载失败"
            );
        }
    }
}
