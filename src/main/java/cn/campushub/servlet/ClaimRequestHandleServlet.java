package cn.campushub.servlet;

import cn.campushub.model.ClaimHandleResult;
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
 * 接收认领申请的审核处理请求，调用业务层并生成 HTTP 响应。
 */
public class ClaimRequestHandleServlet extends HttpServlet {
    private final ClaimRequestService service = new ClaimRequestService();
    private final MessageService messageService = new MessageService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            LostFoundJsonSupport.writeNeedLogin(response);
            return;
        }
        Long claimId = LostFoundJsonSupport.parsePositiveId(
                request.getParameter("claimId")
        );
        if (claimId == null) {
            LostFoundJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "认领申请参数无效"
            );
            return;
        }
        try {
            ServiceResult<ClaimHandleResult> result = service.handle(
                    claimId,
                    user.id(),
                    request.getParameter("action")
            );
            if (!result.success()) {
                LostFoundJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        result.message()
                );
                return;
            }
            ClaimHandleResult data = result.data();
            try {
                messageService.createMessage(
                        data.applicantId(),
                        data.approved()
                                ? "你的认领申请已通过"
                                : "你的认领申请被拒绝",
                        "你对《" + data.lostFoundTitle() + "》的认领申请"
                                + (data.approved()
                                ? "已通过，请联系发布人完成认领。"
                                : "未通过。"),
                        "claim"
                );
            } catch (SQLException notificationError) {
                log("创建认领处理通知失败", notificationError);
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of("success", true, "message", result.message())
            );
        } catch (SQLException exception) {
            log("处理认领申请失败", exception);
            LostFoundJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "认领申请处理失败"
            );
        }
    }
}
