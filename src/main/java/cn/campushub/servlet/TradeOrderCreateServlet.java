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

/**
 * 接收交易订单的创建请求，调用业务层并生成 HTTP 响应。
 */
public class TradeOrderCreateServlet extends HttpServlet {
    private final TradeOrderService tradeOrderService = new TradeOrderService();

    /**
     * 处理交易订单创建相关的 HTTP POST 请求并生成响应。
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
            boolean localhost = TradeUrlUtils.isLocalhost(request);
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
                    localhost
                            ? ""
                            : request.getContextPath()
                                    + "/trade/qrcode?orderNo="
                                    + order.getOrderNo()
            );
            body.put(
                    "expireAt",
                    order.getExpireAt().atZone(ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
            );
            body.put("localhostWarning", localhost);
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
