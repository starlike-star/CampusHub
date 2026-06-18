package cn.campushub.servlet;

import cn.campushub.model.GoodsOrder;
import cn.campushub.model.TradeOrderResult;
import cn.campushub.service.MockPayService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 接收MockPay的页面展示请求，调用业务层并生成 HTTP 响应。
 */
public class MockPayPageServlet extends HttpServlet {
    private final MockPayService mockPayService = new MockPayService();

    /**
     * 处理`MockPayPage`相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Optional<GoodsOrder> order = mockPayService.findByToken(
                    request.getParameter("token")
            );
            if (order.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute(
                        "payResult",
                        TradeOrderResult.failure("模拟支付链接无效或已失效")
                );
                forward(request, response);
                return;
            }
            request.setAttribute("order", order.get());
            forward(request, response);
        } catch (SQLException exception) {
            log("加载模拟支付确认页失败", exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute(
                    "payResult",
                    TradeOrderResult.failure("模拟支付页面暂时不可用，请稍后重试")
            );
            forward(request, response);
        }
    }

    /**
     * 处理 `forward` 对应的业务流程。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/trade/mock-pay.jsp")
                .forward(request, response);
    }
}
