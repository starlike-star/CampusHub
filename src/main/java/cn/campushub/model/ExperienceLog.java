package cn.campushub.model;

import java.time.LocalDateTime;

public record ExperienceLog(
        long id,
        long userId,
        int changeValue,
        String source,
        String description,
        LocalDateTime createdAt
) {
}
