package cn.campushub.model;

/**
 * 封装交易订单操作的处理结果与返回数据。
 */
public record TradeOrderResult(
        boolean success,
        String message,
        GoodsOrder order
) {
    /**
     * 创建表示操作成功的结果对象。
     *
     * @param message 消息数据
     * @param order 订单数据
     * @return 方法处理结果
     */
    public static TradeOrderResult success(String message, GoodsOrder order) {
        return new TradeOrderResult(true, message, order);
    }

    /**
     * 创建表示操作失败的结果对象。
     *
     * @param message 消息数据
     * @return 方法处理结果
     */
    public static TradeOrderResult failure(String message) {
        return new TradeOrderResult(false, message, null);
    }
}
