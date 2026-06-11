package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.service.GoodsService;
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
 * 接收商品的创建请求，调用业务层并生成 HTTP 响应。
 */
public class GoodsCreateServlet extends HttpServlet {
    private final GoodsService goodsService = new GoodsService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            GoodsJsonSupport.writeNeedLogin(response);
            return;
        }
        try {
            ServiceResult<Long> result = goodsService.create(
                    user.id(),
                    request.getParameter("title"),
                    request.getParameter("description"),
                    request.getParameter("price"),
                    request.getParameter("categoryId"),
                    request.getParameter("conditionLevel"),
                    request.getParameter("images"),
                    request.getParameter("tradePlace"),
                    request.getParameter("tradeMethod"),
                    request.getParameter("contact")
            );
            if (!result.success()) {
                GoodsJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        result.message()
                );
                return;
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "message", result.message(),
                            "goodsId", result.data()
                    )
            );
        } catch (SQLException exception) {
            log("发布商品失败", exception);
            GoodsJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "商品发布失败"
            );
        }
    }
}
