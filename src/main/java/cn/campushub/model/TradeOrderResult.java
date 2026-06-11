package cn.campushub.model;

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
