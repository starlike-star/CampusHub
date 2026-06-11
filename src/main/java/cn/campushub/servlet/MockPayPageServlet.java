package cn.campushub.servlet;

import cn.campushub.model.GoodsOrder;
import cn.campushub.service.MockPayService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class MockPayPageServlet extends HttpServlet {
    private final MockPayService mockPayService = new MockPayService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Optional<GoodsOrder> order = mockPayService.findByToken(
                    request.getParameter("token")
            );
            if (order.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "模拟支付链接无效"
                );
                return;
            }
            request.setAttribute("order", order.get());
            request.getRequestDispatcher("/WEB-INF/views/trade/mock-pay.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            log("加载模拟支付确认页失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "模拟支付页面加载失败"
            );
        }
    }
}
