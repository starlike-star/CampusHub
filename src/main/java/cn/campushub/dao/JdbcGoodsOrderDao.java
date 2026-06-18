package cn.campushub.dao;

import cn.campushub.model.GoodsOrder;
import cn.campushub.model.TradeOrderResult;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 使用 JDBC 实现商品订单数据的查询与持久化操作。
 */
public class JdbcGoodsOrderDao implements GoodsOrderDao {
    private static final String ORDER_COLUMNS = """
            SELECT o.id, o.order_no, o.goods_id, o.buyer_id, o.seller_id,
                   o.amount, o.pay_method, o.status, o.pay_token, o.expire_at,
                   o.paid_at, o.cancelled_at, o.created_at, o.updated_at,
                   g.title AS goods_title
            FROM goods_orders o
            JOIN goods g ON g.id = o.goods_id
            """;

    /**
     * 创建`Pending`。
     *
     * @param goodsId 商品编号
     * @param buyerId `buyer`编号
     * @param orderNo 参数 `orderNo`
     * @param payToken 参数 `payToken`
     * @param expireAt 参数 `expireAt`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public TradeOrderResult createPending(
            long goodsId,
            long buyerId,
            String orderNo,
            String payToken,
            LocalDateTime expireAt
    ) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                TradeOrderResult validation = validateAndLockGoods(
                        connection,
                        goodsId,
                        buyerId
                );
                if (!validation.success()) {
                    connection.rollback();
                    return validation;
                }
                Optional<GoodsOrder> reusable = findReusablePending(
                        connection,
                        goodsId,
                        buyerId
                );
                if (reusable.isPresent()) {
                    connection.commit();
                    return TradeOrderResult.success("继续支付待付款订单", reusable.get());
                }

                GoodsOrder goods = validation.order();
                String sql = """
                        INSERT INTO goods_orders
                            (order_no, goods_id, buyer_id, seller_id, amount,
                             pay_method, status, pay_token, expire_at)
                        VALUES (?, ?, ?, ?, ?, 'mock_wechat',
                                'pending_payment', ?, ?)
                        """;
                try (PreparedStatement statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )) {
                    statement.setString(1, orderNo);
                    statement.setLong(2, goodsId);
                    statement.setLong(3, buyerId);
                    statement.setLong(4, goods.getSellerId());
                    statement.setBigDecimal(5, goods.getAmount());
                    statement.setString(6, payToken);
                    statement.setTimestamp(7, Timestamp.valueOf(expireAt));
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("创建订单后未获得主键");
                        }
                        goods.setId(keys.getLong(1));
                    }
                }
                goods.setOrderNo(orderNo);
                goods.setBuyerId(buyerId);
                goods.setGoodsId(goodsId);
                goods.setPayMethod("mock_wechat");
                goods.setStatus("pending_payment");
                goods.setPayToken(payToken);
                goods.setExpireAt(expireAt);
                connection.commit();
                return TradeOrderResult.success("模拟订单创建成功", goods);
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 根据`OrderNo`查询`JdbcGoodsOrder`。
     *
     * @param orderNo 参数 `orderNo`
     * @param buyerId `buyer`编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Optional<GoodsOrder> findByOrderNo(String orderNo, long buyerId)
            throws SQLException {
        String sql = ORDER_COLUMNS + """

                WHERE o.order_no = ? AND o.buyer_id = ?
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, orderNo);
            statement.setLong(2, buyerId);
            return queryOne(statement);
        }
    }

    /**
     * 根据令牌查询`JdbcGoodsOrder`。
     *
     * @param token 参数 `token`
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Optional<GoodsOrder> findByToken(String token) throws SQLException {
        String sql = ORDER_COLUMNS + """

                WHERE o.pay_token = ?
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);
            return queryOne(statement);
        }
    }

    /**
     * 根据输入计算并返回 `confirmPaid` 的处理结果。
     *
     * @param token 参数 `token`
     * @param now 参数 `now`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public TradeOrderResult confirmPaid(String token, LocalDateTime now)
            throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Optional<GoodsOrder> found = findByTokenForUpdate(connection, token);
                if (found.isEmpty()) {
                    connection.rollback();
                    return TradeOrderResult.failure("模拟支付链接无效");
                }
                GoodsOrder order = found.get();
                if ("paid".equals(order.getStatus())) {
                    connection.commit();
                    return TradeOrderResult.success("订单已支付", order);
                }
                if (!"pending_payment".equals(order.getStatus())) {
                    connection.rollback();
                    return TradeOrderResult.failure("订单当前状态不可支付");
                }
                if (order.getExpireAt() != null
                        && !order.getExpireAt().isAfter(now)) {
                    expire(connection, order.getId());
                    connection.commit();
                    order.setStatus("expired");
                    return TradeOrderResult.failure("订单已过期，请重新下单");
                }
                if (!lockGoodsOnSale(connection, order.getGoodsId())) {
                    connection.rollback();
                    return TradeOrderResult.failure("商品已售出或已不在售");
                }
                try (PreparedStatement statement = connection.prepareStatement("""
                        UPDATE goods_orders
                        SET status = 'paid', paid_at = ?
                        WHERE id = ? AND status = 'pending_payment'
                        """)) {
                    statement.setTimestamp(1, Timestamp.valueOf(now));
                    statement.setLong(2, order.getId());
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return TradeOrderResult.failure("订单状态已发生变化");
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement("""
                        UPDATE goods
                        SET status = 'sold'
                        WHERE id = ? AND status = 'on_sale'
                        """)) {
                    statement.setLong(1, order.getGoodsId());
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return TradeOrderResult.failure("商品状态已发生变化");
                    }
                }
                connection.commit();
                order.setStatus("paid");
                order.setPaidAt(now);
                return TradeOrderResult.success("模拟支付成功", order);
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * 根据输入计算并返回 `expireIfNecessary` 的处理结果。
     *
     * @param orderNo 参数 `orderNo`
     * @param buyerId `buyer`编号
     * @param now 参数 `now`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public boolean expireIfNecessary(
            String orderNo,
            long buyerId,
            LocalDateTime now
    ) throws SQLException {
        String sql = """
                UPDATE goods_orders
                SET status = 'expired'
                WHERE order_no = ? AND buyer_id = ?
                  AND status = 'pending_payment'
                  AND expire_at IS NOT NULL AND expire_at <= ?
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, orderNo);
            statement.setLong(2, buyerId);
            statement.setTimestamp(3, Timestamp.valueOf(now));
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * 校验`AndLockGoods`。
     *
     * @param connection 数据库连接
     * @param goodsId 商品编号
     * @param buyerId `buyer`编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private TradeOrderResult validateAndLockGoods(
            Connection connection,
            long goodsId,
            long buyerId
    ) throws SQLException {
        String sql = """
                SELECT id, user_id, title, price, trade_method, status
                FROM goods
                WHERE id = ?
                FOR UPDATE
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, goodsId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return TradeOrderResult.failure("商品不存在");
                }
                long sellerId = resultSet.getLong("user_id");
                if (sellerId == buyerId) {
                    return TradeOrderResult.failure("不能购买自己发布的商品");
                }
                String status = resultSet.getString("status");
                if ("sold".equals(status)) {
                    return TradeOrderResult.failure("商品已售出");
                }
                if ("off_shelf".equals(status)) {
                    return TradeOrderResult.failure("商品已下架");
                }
                if (!"on_sale".equals(status)) {
                    return TradeOrderResult.failure("商品当前不可购买");
                }
                String tradeMethod = resultSet.getString("trade_method");
                if (!"online".equals(tradeMethod) && !"both".equals(tradeMethod)) {
                    return TradeOrderResult.failure(
                            "该商品仅支持线下交易，请通过私信联系卖家"
                    );
                }
                if (hasPaidOrder(connection, goodsId)) {
                    return TradeOrderResult.failure("该商品已有已支付订单");
                }
                GoodsOrder order = new GoodsOrder();
                order.setGoodsId(goodsId);
                order.setSellerId(sellerId);
                order.setAmount(resultSet.getBigDecimal("price"));
                order.setGoodsTitle(resultSet.getString("title"));
                return TradeOrderResult.success("校验通过", order);
            }
        }
    }

    /**
     * 判断是否具有`PaidOrder`。
     *
     * @param connection 数据库连接
     * @param goodsId 商品编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean hasPaidOrder(Connection connection, long goodsId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT 1
                FROM goods_orders
                WHERE goods_id = ? AND status = 'paid'
                LIMIT 1
                """)) {
            statement.setLong(1, goodsId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 查询`ReusablePending`。
     *
     * @param connection 数据库连接
     * @param goodsId 商品编号
     * @param buyerId `buyer`编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private Optional<GoodsOrder> findReusablePending(
            Connection connection,
            long goodsId,
            long buyerId
    ) throws SQLException {
        String sql = ORDER_COLUMNS + """

                WHERE o.goods_id = ? AND o.buyer_id = ?
                  AND o.status = 'pending_payment'
                  AND (o.expire_at IS NULL OR o.expire_at > NOW())
                ORDER BY o.created_at DESC
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, goodsId);
            statement.setLong(2, buyerId);
            return queryOne(statement);
        }
    }

    /**
     * 根据`TokenForUpdate`查询`JdbcGoodsOrder`。
     *
     * @param connection 数据库连接
     * @param token 参数 `token`
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private Optional<GoodsOrder> findByTokenForUpdate(
            Connection connection,
            String token
    ) throws SQLException {
        String sql = ORDER_COLUMNS + """

                WHERE o.pay_token = ?
                LIMIT 1
                FOR UPDATE
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);
            return queryOne(statement);
        }
    }

    /**
     * 根据输入计算并返回 `lockGoodsOnSale` 的处理结果。
     *
     * @param connection 数据库连接
     * @param goodsId 商品编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    private boolean lockGoodsOnSale(Connection connection, long goodsId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT status
                FROM goods
                WHERE id = ?
                FOR UPDATE
                """)) {
            statement.setLong(1, goodsId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        && "on_sale".equals(resultSet.getString("status"));
            }
        }
    }

    /**
     * 处理 `expire` 对应的业务流程。
     *
     * @param connection 数据库连接
     * @param orderId 订单编号
     * @throws SQLException 数据库访问失败时抛出
     */
    private void expire(Connection connection, long orderId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE goods_orders
                SET status = 'expired'
                WHERE id = ? AND status = 'pending_payment'
                """)) {
            statement.setLong(1, orderId);
            statement.executeUpdate();
        }
    }

    /**
     * 查询`queryOne`并返回结果。
     *
     * @param statement 预编译 SQL 语句
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private Optional<GoodsOrder> queryOne(PreparedStatement statement)
            throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next()
                    ? Optional.of(mapOrder(resultSet))
                    : Optional.empty();
        }
    }

    /**
     * 将数据库结果映射为订单。
     *
     * @param resultSet 数据库查询结果集
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private GoodsOrder mapOrder(ResultSet resultSet) throws SQLException {
        GoodsOrder order = new GoodsOrder();
        order.setId(resultSet.getLong("id"));
        order.setOrderNo(resultSet.getString("order_no"));
        order.setGoodsId(resultSet.getLong("goods_id"));
        order.setBuyerId(resultSet.getLong("buyer_id"));
        order.setSellerId(resultSet.getLong("seller_id"));
        order.setAmount(resultSet.getBigDecimal("amount"));
        order.setPayMethod(resultSet.getString("pay_method"));
        order.setStatus(resultSet.getString("status"));
        order.setPayToken(resultSet.getString("pay_token"));
        order.setExpireAt(toLocalDateTime(resultSet.getTimestamp("expire_at")));
        order.setPaidAt(toLocalDateTime(resultSet.getTimestamp("paid_at")));
        order.setCancelledAt(toLocalDateTime(resultSet.getTimestamp("cancelled_at")));
        order.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        order.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        order.setGoodsTitle(resultSet.getString("goods_title"));
        return order;
    }

    /**
     * 转换为`LocalDateTime`。
     *
     * @param timestamp 数据库时间戳
     * @return 方法处理结果
     */
    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
