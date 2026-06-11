package cn.campushub.servlet;

import cn.campushub.service.RememberMeService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 清理登录会话与持久登录令牌并完成退出跳转。
 */
public class LogoutServlet extends HttpServlet {
    private final RememberMeService rememberMeService = new RememberMeService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            rememberMeService.logout(request, response);
        } catch (SQLException exception) {
            log("删除记住我凭证失败", exception);
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "请使用 POST 退出登录");
    }
}
