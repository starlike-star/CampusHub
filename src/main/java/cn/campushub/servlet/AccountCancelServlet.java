package cn.campushub.servlet;

import cn.campushub.model.AccountCancelResult;
import cn.campushub.model.SessionUser;
import cn.campushub.service.AccountService;
import cn.campushub.service.RememberMeService;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * 接收账号的请求处理请求，调用业务层并生成 HTTP 响应。
 */
public class AccountCancelServlet extends HttpServlet {
    private final AccountService accountService = new AccountService();
    private final RememberMeService rememberMeService = new RememberMeService();

    /**
     * 处理账号取消相关的 HTTP POST 请求并生成响应。
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
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    Map.of(
                            "success", false,
                            "needLogin", true,
                            "message", "请先登录"
                    )
            );
            return;
        }
        try {
            AccountCancelResult result = accountService.cancelAccount(
                    user,
                    request.getParameter("password"),
                    request.getParameter("reason"),
                    request.getParameter("confirm"),
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent")
            );
            if (!result.success()) {
                JsonUtils.write(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        Map.of("success", false, "message", result.message())
                );
                return;
            }

            rememberMeService.clearRememberCookie(request, response);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of("success", true, "message", result.message())
            );
        } catch (SQLException exception) {
            log("注销账号失败", exception);
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of(
                            "success", false,
                            "message", "注销失败，请稍后重试"
                    )
            );
        }
    }
}
