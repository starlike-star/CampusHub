package cn.campushub.servlet;

import cn.campushub.model.GoodsOrder;
import cn.campushub.model.SessionUser;
import cn.campushub.model.TradeOrderResult;
import cn.campushub.service.TradeOrderService;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;
import cn.campushub.util.TradeUrlUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

public class TradeOrderCreateServlet extends HttpServlet {
    private final TradeOrderService tradeOrderService = new TradeOrderService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            GoodsJsonSupport.writeNeedLogin(response);
            return;
        }
        Long goodsId = GoodsJsonSupport.parseGoodsId(request);
        if (goodsId == null) {
            GoodsJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "商品参数无效"
            );
            return;
        }
        try {
            TradeOrderResult result = tradeOrderService.create(goodsId, user.id());
            if (!result.success()) {
                GoodsJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        result.message()
                );
                return;
            }
            GoodsOrder order = result.order();
            String baseUrl = TradeUrlUtils.publicBaseUrl(request);
            String payUrl = baseUrl + "/trade/mock-pay?token="
                    + order.getPayToken();
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", true);
            body.put("message", result.message());
            body.put("orderNo", order.getOrderNo());
            body.put("title", order.getGoodsTitle());
            body.put("amount", order.getAmount());
            body.put("payUrl", payUrl);
            body.put(
                    "qrcodeUrl",
                    request.getContextPath() + "/trade/qrcode?orderNo="
                            + order.getOrderNo()
            );
            body.put(
                    "expireAt",
                    order.getExpireAt().atZone(ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
            );
            body.put("localhostWarning", TradeUrlUtils.isLocalhost(request));
            JsonUtils.write(response, HttpServletResponse.SC_OK, body);
        } catch (SQLException exception) {
            log("创建模拟交易订单失败", exception);
            GoodsJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "模拟订单创建失败"
            );
        }
    }
}
