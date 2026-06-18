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
 * 接收失物招领的创建请求，调用业务层并生成 HTTP 响应。
 */
public class LostFoundCreateServlet extends HttpServlet {
    private final LostFoundService service = new LostFoundService();

    /**
     * 处理`LostFoundCreate`相关的 HTTP POST 请求并生成响应。
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
