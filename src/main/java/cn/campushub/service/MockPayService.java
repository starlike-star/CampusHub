package cn.campushub.service;

import cn.campushub.model.GoodsOrder;
import cn.campushub.model.TradeOrderResult;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MockPayService {
    private static final Logger LOGGER =
            Logger.getLogger(MockPayService.class.getName());
    private final TradeOrderService tradeOrderService;
    private final MessageService messageService;

    public MockPayService() {
        this(new TradeOrderService(), new MessageService());
    }

    MockPayService(
            TradeOrderService tradeOrderService,
            MessageService messageService
    ) {
        this.tradeOrderService = tradeOrderService;
        this.messageService = messageService;
    }

    public Optional<GoodsOrder> findByToken(String token) throws SQLException {
        return tradeOrderService.findByToken(token);
    }

    public TradeOrderResult confirm(String token) throws SQLException {
        TradeOrderResult result = tradeOrderService.confirm(token);
        if (result.success() && "模拟支付成功".equals(result.message())) {
            GoodsOrder order = result.order();
            try {
                messageService.notifyTradePaid(
                        order.getBuyerId(),
                        order.getSellerId(),
                        order.getGoodsTitle()
                );
            } catch (SQLException exception) {
                LOGGER.log(Level.WARNING, "模拟支付成功，但交易通知创建失败", exception);
            }
        }
        return result;
    }
}
