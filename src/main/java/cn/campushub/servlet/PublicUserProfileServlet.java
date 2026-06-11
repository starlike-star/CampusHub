package cn.campushub.servlet;

import cn.campushub.model.PublicUserProfile;
import cn.campushub.model.SessionUser;
import cn.campushub.service.PublicUserProfileService;
import cn.campushub.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class PublicUserProfileServlet extends HttpServlet {
    private final PublicUserProfileService service =
            new PublicUserProfileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long userId = parsePositiveLong(request.getParameter("id"));
        if (userId == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "用户参数无效"
            );
            return;
        }
        try {
            Optional<PublicUserProfile> profile = service.find(userId);
            if (profile.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "用户不存在或账号不可用"
                );
                return;
            }
            SessionUser currentUser = SessionUtils.currentUser(request);
            request.setAttribute("publicUser", profile.get());
            request.setAttribute(
                    "viewingSelf",
                    currentUser != null && currentUser.id() == userId
            );
            request.getRequestDispatcher("/WEB-INF/views/publicUserProfile.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            log("加载公开用户资料失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "用户资料加载失败"
            );
        }
    }

    private Long parsePositiveLong(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
