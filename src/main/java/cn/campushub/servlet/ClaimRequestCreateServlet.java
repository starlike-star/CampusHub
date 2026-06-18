package cn.campushub.servlet;

import cn.campushub.model.ClaimCreateResult;
import cn.campushub.model.SessionUser;
import cn.campushub.service.ClaimRequestService;
import cn.campushub.service.MessageService;
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
 * 接收认领申请的创建请求，调用业务层并生成 HTTP 响应。
 */
public class ClaimRequestCreateServlet extends HttpServlet {
    private final ClaimRequestService service = new ClaimRequestService();
    private final MessageService messageService = new MessageService();

    /**
     * 处理`ClaimRequestCreate`相关的 HTTP POST 请求并生成响应。
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
        Long id = LostFoundJsonSupport.parsePositiveId(
                request.getParameter("lostFoundId")
        );
        if (id == null) {
            LostFoundJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "失物招领参数无效"
            );
            return;
        }
        try {
            ServiceResult<ClaimCreateResult> result = service.create(
                    id,
                    user.id(),
                    request.getParameter("message"),
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
            ClaimCreateResult data = result.data();
            try {
                messageService.createMessage(
                        data.ownerId(),
                        "有人申请认领你的物品",
                        user.nickname() + " 申请认领《"
                                + data.lostFoundTitle() + "》，请及时处理。",
                        "claim"
                );
            } catch (SQLException notificationError) {
                log("创建认领申请通知失败", notificationError);
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of("success", true, "message", result.message())
            );
        } catch (SQLException exception) {
            log("提交认领申请失败", exception);
            LostFoundJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "认领申请提交失败"
            );
        }
    }
}
