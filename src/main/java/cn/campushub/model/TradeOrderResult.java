package cn.campushub.model;

/**
 * 封装交易订单操作的处理结果与返回数据。
 */
public record TradeOrderResult(
        boolean success,
        String message,
        GoodsOrder order
) {
    public static TradeOrderResult success(String message, GoodsOrder order) {
        return new TradeOrderResult(true, message, order);
    }

    public static TradeOrderResult failure(String message) {
        return new TradeOrderResult(false, message, null);
    }
}
