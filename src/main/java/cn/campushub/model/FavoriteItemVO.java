package cn.campushub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 聚合FavoriteItem页面展示所需的数据。
 */
public record FavoriteItemVO(
        long favoriteId,
        String targetType,
        long targetId,
        String title,
        String summary,
        String categoryName,
        String authorNickname,
        String topic,
        int likeCount,
        int commentCount,
        int viewCount,
        BigDecimal price,
        String conditionLevel,
        String images,
        String tradePlace,
        String tradeMethod,
        String status,
        LocalDateTime favoriteTime
) {
    public boolean isPost() {
        return "post".equals(targetType);
    }

    public String firstImage() {
        if (images == null || images.isBlank()) {
            return "images/default-goods.png";
        }
        return Arrays.stream(images.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .findFirst()
                .orElse("images/default-goods.png");
    }
}
