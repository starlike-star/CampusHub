package cn.campushub.servlet;

import cn.campushub.model.TradeOrderResult;
import cn.campushub.service.MockPayService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 接收MockPay的确认请求，调用业务层并生成 HTTP 响应。
 */
public class MockPayConfirmServlet extends HttpServlet {
    private final MockPayService mockPayService = new MockPayService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            TradeOrderResult result = mockPayService.confirm(
                    request.getParameter("token")
            );
            request.setAttribute("order", result.order());
            request.setAttribute("payResult", result);
            request.getRequestDispatcher("/WEB-INF/views/trade/mock-pay.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            log("确认模拟支付失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "模拟支付确认失败"
            );
        }
    }
}
