package cn.campushub.service;

import cn.campushub.dao.GoodsOrderDao;
import cn.campushub.dao.JdbcGoodsOrderDao;
import cn.campushub.model.GoodsOrder;
import cn.campushub.model.TradeOrderResult;
import cn.campushub.util.ValidationUtils;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 编排交易订单业务规则、参数校验与数据访问操作。
 */
public class TradeOrderService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final AtomicInteger SEQUENCE = new AtomicInteger();
    private static final DateTimeFormatter ORDER_TIME =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int PAYMENT_MINUTES = 10;

    private final GoodsOrderDao goodsOrderDao;

    public TradeOrderService() {
        this(new JdbcGoodsOrderDao());
    }

    TradeOrderService(GoodsOrderDao goodsOrderDao) {
        this.goodsOrderDao = goodsOrderDao;
    }

    public TradeOrderResult create(long goodsId, long buyerId)
            throws SQLException {
        if (goodsId <= 0 || buyerId <= 0) {
            return TradeOrderResult.failure("商品参数无效");
        }
        LocalDateTime now = LocalDateTime.now();
        return goodsOrderDao.createPending(
                goodsId,
                buyerId,
                createOrderNo(now),
                createToken(),
                now.plusMinutes(PAYMENT_MINUTES)
        );
    }

    public Optional<GoodsOrder> findForBuyer(String orderNo, long buyerId)
            throws SQLException {
        orderNo = ValidationUtils.trimToNull(orderNo);
        if (orderNo == null || orderNo.length() > 64 || buyerId <= 0) {
            return Optional.empty();
        }
        goodsOrderDao.expireIfNecessary(orderNo, buyerId, LocalDateTime.now());
        return goodsOrderDao.findByOrderNo(orderNo, buyerId);
    }

    public Optional<GoodsOrder> findByToken(String token) throws SQLException {
        token = normalizeToken(token);
        return token == null ? Optional.empty() : goodsOrderDao.findByToken(token);
    }

    public TradeOrderResult confirm(String token) throws SQLException {
        token = normalizeToken(token);
        if (token == null) {
            return TradeOrderResult.failure("模拟支付链接无效");
        }
        return goodsOrderDao.confirmPaid(token, LocalDateTime.now());
    }

    private String normalizeToken(String token) {
        token = ValidationUtils.trimToNull(token);
        return token != null && token.length() <= 128 ? token : null;
    }

    private String createOrderNo(LocalDateTime now) {
        int sequence = Math.floorMod(SEQUENCE.incrementAndGet(), 10000);
        return "CH" + now.format(ORDER_TIME) + String.format("%04d", sequence);
    }

    private String createToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
