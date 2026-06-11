package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的活动报名领域数据，并提供对应属性访问。
 */
public record ActivityRegistration(
        long id,
        long activityId,
        long userId,
        String status,
        LocalDateTime createdAt
) {
}
