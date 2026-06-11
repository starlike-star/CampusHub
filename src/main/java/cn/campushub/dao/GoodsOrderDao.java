package cn.campushub.dao;

import cn.campushub.model.GoodsOrder;
import cn.campushub.model.TradeOrderResult;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public interface GoodsOrderDao {
    TradeOrderResult createPending(
            long goodsId,
            long buyerId,
            String orderNo,
            String payToken,
            LocalDateTime expireAt
    ) throws SQLException;

    Optional<GoodsOrder> findByOrderNo(String orderNo, long buyerId)
            throws SQLException;

    Optional<GoodsOrder> findByToken(String token) throws SQLException;

    TradeOrderResult confirmPaid(String token, LocalDateTime now)
            throws SQLException;

    boolean expireIfNecessary(String orderNo, long buyerId, LocalDateTime now)
            throws SQLException;
}
