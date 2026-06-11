package cn.campushub.servlet;

import cn.campushub.constant.SessionConstants;
import cn.campushub.model.SessionUser;
import cn.campushub.model.User;
import cn.campushub.service.ProfileService;
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
 * 接收个人主页的更新请求，调用业务层并生成 HTTP 响应。
 */
public class ProfileUpdateServlet extends HttpServlet {
    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser sessionUser = SessionUtils.currentUser(request);
        if (sessionUser == null) {
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
            ServiceResult<User> result = profileService.update(
                    sessionUser.id(),
                    request.getParameter("nickname"),
                    request.getParameter("avatar"),
                    request.getParameter("studentNo"),
                    request.getParameter("college"),
                    request.getParameter("major"),
                    request.getParameter("grade"),
                    request.getParameter("email"),
                    request.getParameter("phone")
            );
            if (!result.success()) {
                JsonUtils.write(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        Map.of("success", false, "message", result.message())
                );
                return;
            }

            User updated = result.data();
            SessionUser updatedSessionUser = SessionUser.from(updated);
            request.getSession().setAttribute(
                    SessionConstants.LOGIN_USER,
                    updatedSessionUser
            );
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "message", result.message(),
                            "nickname", updatedSessionUser.nickname(),
                            "avatar", updatedSessionUser.avatar()
                    )
            );
        } catch (SQLException exception) {
            log("更新个人资料失败", exception);
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("success", false, "message", "资料更新失败")
            );
        }
    }
}
