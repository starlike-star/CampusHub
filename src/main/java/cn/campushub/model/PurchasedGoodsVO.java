package cn.campushub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 聚合PurchasedGoods页面展示所需的数据。
 */
public record PurchasedGoodsVO(
        long goodsId,
        String orderNo,
        String title,
        BigDecimal amount,
        String images,
        String conditionLevel,
        String tradeMethod,
        String sellerNickname,
        String categoryName,
        LocalDateTime paidAt
) {
    public String firstImage() {
        List<String> imageList = images == null || images.isBlank()
                ? List.of()
                : Arrays.stream(images.split(","))
                        .map(String::trim)
                        .filter(value -> !value.isEmpty())
                        .toList();
        return imageList.stream()
                .findFirst()
                .orElse("images/default-goods.png");
    }

    public String tradeMethodText() {
        return switch (tradeMethod) {
            case "online" -> "线上交易";
            case "both" -> "线上/线下均可";
            default -> "线下交易";
        };
    }
}
