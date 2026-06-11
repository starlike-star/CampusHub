package cn.campushub.servlet;

import cn.campushub.model.LostFound;
import cn.campushub.model.SessionUser;
import cn.campushub.service.ClaimRequestService;
import cn.campushub.service.LostFoundService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 接收失物招领的详情查询请求，调用业务层并生成 HTTP 响应。
 */
public class LostFoundDetailServlet extends HttpServlet {
    private final LostFoundService lostFoundService = new LostFoundService();
    private final ClaimRequestService claimService = new ClaimRequestService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = LostFoundJsonSupport.parsePositiveId(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数无效");
            return;
        }
        SessionUser user = SessionUtils.currentUser(request);
        try {
            Optional<LostFound> result = lostFoundService.detail(id);
            if (result.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "信息不存在");
                return;
            }
            LostFound item = result.get();
            boolean owner = user != null
                    && (user.id() == item.getUserId()
                    || LostFoundJsonSupport.isAdmin(user));
            request.setAttribute("lostFound", item);
            request.setAttribute("lostFoundOwner", owner);
            request.setAttribute(
                    "lostFoundCategories",
                    lostFoundService.listCategories()
            );
            if (owner) {
                request.setAttribute(
                        "claimRequests",
                        claimService.list(item.getId(), item.getUserId())
                );
            }
            request.getRequestDispatcher("/WEB-INF/views/lostFoundDetail.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            log("加载失物招领详情失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "详情加载失败"
            );
        }
    }
}
