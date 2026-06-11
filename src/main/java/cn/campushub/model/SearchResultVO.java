package cn.campushub.model;

import java.time.LocalDateTime;

public record SearchResultVO(
        long id,
        String type,
        String title,
        String summary,
        String image,
        String authorName,
        String statusText,
        String extraInfo,
        String targetUrl,
        LocalDateTime createdAt
) {
    public String typeLabel() {
        return switch (type) {
            case "post" -> "帖子";
            case "goods" -> "二手商品";
            case "lost_found" -> "失物招领";
            case "activity" -> "校园活动";
            case "notice" -> "校园公告";
            default -> "校园内容";
        };
    }
}
