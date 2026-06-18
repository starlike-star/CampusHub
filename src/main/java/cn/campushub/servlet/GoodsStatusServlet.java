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
 * 接收商品的状态变更请求，调用业务层并生成 HTTP 响应。
 */
public class GoodsStatusServlet extends HttpServlet {
    private final GoodsService goodsService = new GoodsService();

    /**
     * 处理商品状态相关的 HTTP POST 请求并生成响应。
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
            ServiceResult<Void> result = goodsService.updateStatus(
                    goodsId,
                    user.id(),
                    GoodsJsonSupport.isAdmin(user),
                    request.getParameter("status")
            );
            if (!result.success()) {
                GoodsJsonSupport.writeError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        result.message()
                );
                return;
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of("success", true, "message", result.message())
            );
        } catch (SQLException exception) {
            log("修改商品状态失败", exception);
            GoodsJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "商品状态修改失败"
            );
        }
    }
}
