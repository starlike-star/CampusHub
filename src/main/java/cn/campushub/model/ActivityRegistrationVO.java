package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 聚合活动报名页面展示所需的数据。
 */
public record ActivityRegistrationVO(
        long id,
        long activityId,
        long userId,
        String status,
        LocalDateTime createdAt,
        String nickname,
        String avatar,
        String college,
        String major,
        String grade,
        String email,
        String phone
) {
}
