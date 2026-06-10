package cn.campushub.model;

import java.time.LocalDateTime;

public record ActivityRegistration(
        long id,
        long activityId,
        long userId,
        String status,
        LocalDateTime createdAt
) {
}
