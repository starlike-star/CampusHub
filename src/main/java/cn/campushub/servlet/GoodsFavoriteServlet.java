package cn.campushub.servlet;

import cn.campushub.model.PostToggleResult;
import cn.campushub.model.SessionUser;
import cn.campushub.service.GoodsService;
import cn.campushub.service.MessageService;
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
 * 接收商品的收藏切换请求，调用业务层并生成 HTTP 响应。
 */
public class GoodsFavoriteServlet extends HttpServlet {
    private final GoodsService goodsService = new GoodsService();
    private final MessageService messageService = new MessageService();

    /**
     * 处理商品收藏相关的 HTTP POST 请求并生成响应。
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
            ServiceResult<PostToggleResult> result =
                    goodsService.toggleFavorite(goodsId, user.id());
            if (result.data().active()) {
                try {
                    messageService.notifyGoodsFavorite(
                            goodsId,
                            user.id(),
                            user.nickname()
                    );
                } catch (SQLException notificationError) {
                    log("创建商品收藏通知失败", notificationError);
                }
            }
            JsonUtils.write(
                    response,
                    HttpServletResponse.SC_OK,
                    Map.of(
                            "success", true,
                            "favorited", result.data().active(),
                            "favoriteCount", result.data().count()
                    )
            );
        } catch (SQLException exception) {
            log("切换商品收藏状态失败", exception);
            GoodsJsonSupport.writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "收藏操作失败"
            );
        }
    }
}
