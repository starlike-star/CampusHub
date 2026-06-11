package cn.campushub.servlet;

import cn.campushub.model.GoodsOrder;
import cn.campushub.model.SessionUser;
import cn.campushub.service.TradeOrderService;
import cn.campushub.util.SessionUtils;
import cn.campushub.util.TradeUrlUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class TradeQrCodeServlet extends HttpServlet {
    private static final int SIZE = 320;
    private final TradeOrderService tradeOrderService = new TradeOrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        try {
            Optional<GoodsOrder> order = tradeOrderService.findForBuyer(
                    request.getParameter("orderNo"),
                    user.id()
            );
            if (order.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String payUrl = TradeUrlUtils.publicBaseUrl(request)
                    + "/trade/mock-pay?token=" + order.get().getPayToken();
            BitMatrix matrix = new MultiFormatWriter().encode(
                    payUrl,
                    BarcodeFormat.QR_CODE,
                    SIZE,
                    SIZE,
                    Map.of(EncodeHintType.CHARACTER_SET, "UTF-8")
            );
            BufferedImage image = new BufferedImage(
                    SIZE,
                    SIZE,
                    BufferedImage.TYPE_INT_RGB
            );
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? 0x111827 : 0xFFFFFF);
                }
            }
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-store");
            ImageIO.write(image, "PNG", response.getOutputStream());
        } catch (SQLException exception) {
            log("生成模拟支付二维码失败", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception exception) {
            log("二维码编码失败", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
