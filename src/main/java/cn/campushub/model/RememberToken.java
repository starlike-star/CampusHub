package cn.campushub.model;

import java.time.LocalDateTime;

public record RememberToken(
        long id,
        long userId,
        String selector,
        String tokenHash,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime lastUsedAt,
        String userAgent,
        String ipAddress
) {
}
