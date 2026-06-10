package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.ReportService;
import cn.campushub.service.ServiceResult;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ReportCreateServlet extends HttpServlet {
    private final ReportService reportService = new ReportService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    Map.of(
                            "success", false,
                            "needLogin", true,
                            "message", "请先登录后再举报"
                    )
            );
            return;
        }

        try {
            ServiceResult<Void> result = reportService.create(
                    user.id(),
                    request.getParameter("targetId"),
                    request.getParameter("targetType"),
                    request.getParameter("reason")
            );
            JsonUtils.write(
                    response,
                    result.success()
                            ? HttpServletResponse.SC_OK
                            : HttpServletResponse.SC_BAD_REQUEST,
                    Map.of(
                            "success", result.success(),
                            "message", result.message()
                    )
            );
        } catch (SQLException exception) {
            log("提交举报时访问数据库失败", exception);
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of(
                            "success", false,
                            "message", "举报服务暂时不可用，请稍后重试"
                    )
            );
        }
    }
}
