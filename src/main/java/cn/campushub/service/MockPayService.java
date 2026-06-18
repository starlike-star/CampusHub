package cn.campushub.service;

import cn.campushub.model.GoodsOrder;
import cn.campushub.model.TradeOrderResult;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 编排MockPay业务规则、参数校验与数据访问操作。
 */
public class MockPayService {
    private static final Logger LOGGER =
            Logger.getLogger(MockPayService.class.getName());
    private final TradeOrderService tradeOrderService;
    private final MessageService messageService;

    /**
     * 初始化`MockPay`对象及其运行所需依赖。
     */
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

    /**
     * 根据令牌查询`MockPay`。
     *
     * @param token 参数 `token`
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public Optional<GoodsOrder> findByToken(String token) throws SQLException {
        return tradeOrderService.findByToken(token);
    }

    /**
     * 根据输入计算并返回 `confirm` 的处理结果。
     *
     * @param token 参数 `token`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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
