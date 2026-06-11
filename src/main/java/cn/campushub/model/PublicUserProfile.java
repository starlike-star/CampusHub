package cn.campushub.model;

import java.time.LocalDateTime;

public record PublicUserProfile(
        long id,
        String nickname,
        String avatar,
        String college,
        String major,
        String grade,
        LocalDateTime createdAt
) {
}
