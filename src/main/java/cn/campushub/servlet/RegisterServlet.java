package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.ServiceResult;
import cn.campushub.service.UserService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 处理注册页面展示、验证码校验和新用户创建。
 */
public class RegisterServlet extends HttpServlet {
    private final UserService userService = new UserService();

    /**
     * 处理`Register`相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtils.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    /**
     * 处理`Register`相关的 HTTP POST 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("nickname", nickname);

        try {
            ServiceResult<SessionUser> result = userService.register(
                    username, email, nickname, password, confirmPassword
            );
            if (!result.success()) {
                request.setAttribute("errorMessage", result.message());
                request.getRequestDispatcher("/WEB-INF/views/register.jsp")
                        .forward(request, response);
                return;
            }

            SessionUtils.login(request, result.data());
            response.sendRedirect(request.getContextPath() + "/");
        } catch (SQLException exception) {
            log("注册时访问数据库失败", exception);
            request.setAttribute("errorMessage", "注册失败，请检查信息或稍后再试");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}
