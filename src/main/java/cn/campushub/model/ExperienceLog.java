package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 记录经验值变更明细及审计信息。
 */
public record ExperienceLog(
        long id,
        long userId,
        int changeValue,
        String source,
        String description,
        LocalDateTime createdAt
) {
}
