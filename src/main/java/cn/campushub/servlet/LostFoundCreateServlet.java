package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.LostFoundService;
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

public class LostFoundCreateServlet extends HttpServlet {
    private final LostFoundService service = new LostFoundService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            LostFoundJsonSupport.writeNeedLogin(response);
            return;
        }
        try {
            ServiceResult<Long> result = service.create(
                    user.id(),
                    request.getParameter("type"),
                    request.getParameter("itemName"),
                    request.getParameter("title"),
                    request.getParameter("categoryId"),
                    request.getParameter("description"),
                    request.getParameter("place"),
                    request.getParameter("eventTime"),
                    request.getParameter("images"),
                    request.getParameter("contact")
            );
            if (!result.success()) {
                LostFoundJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        result.message()
                );
                return;
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "message", result.message(),
                            "id", result.data()
                    )
            );
        } catch (SQLException exception) {
            log("发布失物招领失败", exception);
            LostFoundJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "发布失败"
            );
        }
    }
}
