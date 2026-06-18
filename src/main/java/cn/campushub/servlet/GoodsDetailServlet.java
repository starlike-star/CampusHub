package cn.campushub.servlet;

import cn.campushub.model.Goods;
import cn.campushub.model.SessionUser;
import cn.campushub.service.GoodsService;
import cn.campushub.util.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 接收商品的详情查询请求，调用业务层并生成 HTTP 响应。
 */
public class GoodsDetailServlet extends HttpServlet {
    private final GoodsService goodsService = new GoodsService();

    /**
     * 处理商品详情相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 处理请求失败时抛出
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long goodsId = parseId(request.getParameter("id"));
        if (goodsId == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "商品参数无效"
            );
            return;
        }
        SessionUser user = SessionUtils.currentUser(request);
        try {
            Optional<Goods> goods = goodsService.detail(
                    goodsId,
                    user == null ? null : user.id()
            );
            if (goods.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "商品不存在或已下架"
                );
                return;
            }
            request.setAttribute("goods", goods.get());
            request.setAttribute("goodsCategories", goodsService.listCategories());
            request.setAttribute(
                    "goodsOwner",
                    user != null && (user.id() == goods.get().getUserId()
                            || GoodsJsonSupport.isAdmin(user))
            );
            request.getRequestDispatcher("/WEB-INF/views/goodsDetail.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            log("加载商品详情失败", exception);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "商品详情加载失败"
            );
        }
    }

    /**
     * 解析编号。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    private Long parseId(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
