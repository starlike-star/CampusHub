package cn.campushub.servlet;

import cn.campushub.model.GoodsOrder;
import cn.campushub.model.SessionUser;
import cn.campushub.service.TradeOrderService;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

/**
 * 接收交易订单的状态变更请求，调用业务层并生成 HTTP 响应。
 */
public class TradeOrderStatusServlet extends HttpServlet {
    private final TradeOrderService tradeOrderService = new TradeOrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            GoodsJsonSupport.writeNeedLogin(response);
            return;
        }
        try {
            Optional<GoodsOrder> order = tradeOrderService.findForBuyer(
                    request.getParameter("orderNo"),
                    user.id()
            );
            if (order.isEmpty()) {
                GoodsJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "订单不存在"
                );
                return;
            }
            String status = order.get().getStatus();
            String message = switch (status) {
                case "paid" -> "支付成功";
                case "expired" -> "订单已过期，请重新下单";
                case "cancelled" -> "订单已取消";
                default -> "等待扫码支付";
            };
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "status", status,
                            "message", message
                    )
            );
        } catch (SQLException exception) {
            log("查询模拟订单状态失败", exception);
            GoodsJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "订单状态查询失败"
            );
        }
    }
}
