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

/**
 * 接收失物招领的状态变更请求，调用业务层并生成 HTTP 响应。
 */
public class LostFoundStatusServlet extends HttpServlet {
    private final LostFoundService service = new LostFoundService();

    /**
     * 处理`LostFoundStatus`相关的 HTTP POST 请求并生成响应。
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
            LostFoundJsonSupport.writeNeedLogin(response);
            return;
        }
        Long id = LostFoundJsonSupport.parseId(request);
        if (id == null) {
            LostFoundJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "失物招领参数无效"
            );
            return;
        }
        try {
            ServiceResult<Void> result = service.updateStatus(
                    id,
                    user.id(),
                    LostFoundJsonSupport.isAdmin(user),
                    request.getParameter("status")
            );
            if (!result.success()) {
                LostFoundJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        result.message()
                );
                return;
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of("success", true, "message", result.message())
            );
        } catch (SQLException exception) {
            log("更新失物招领状态失败", exception);
            LostFoundJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "状态更新失败"
            );
        }
    }
}
