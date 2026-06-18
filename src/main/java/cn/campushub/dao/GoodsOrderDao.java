package cn.campushub.dao;

import cn.campushub.model.GoodsOrder;
import cn.campushub.model.TradeOrderResult;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 定义商品订单数据访问能力及业务层依赖的数据契约。
 */
public interface GoodsOrderDao {
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
    TradeOrderResult createPending(
            long goodsId,
            long buyerId,
            String orderNo,
            String payToken,
            LocalDateTime expireAt
    ) throws SQLException;

    /**
     * 根据`OrderNo`查询商品订单。
     *
     * @param orderNo 参数 `orderNo`
     * @param buyerId `buyer`编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<GoodsOrder> findByOrderNo(String orderNo, long buyerId)
            throws SQLException;

    /**
     * 根据令牌查询商品订单。
     *
     * @param token 参数 `token`
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<GoodsOrder> findByToken(String token) throws SQLException;

    /**
     * 根据输入计算并返回 `confirmPaid` 的处理结果。
     *
     * @param token 参数 `token`
     * @param now 参数 `now`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    TradeOrderResult confirmPaid(String token, LocalDateTime now)
            throws SQLException;

    /**
     * 根据输入计算并返回 `expireIfNecessary` 的处理结果。
     *
     * @param orderNo 参数 `orderNo`
     * @param buyerId `buyer`编号
     * @param now 参数 `now`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean expireIfNecessary(String orderNo, long buyerId, LocalDateTime now)
            throws SQLException;
}
