package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的记住登录令牌领域数据，并提供对应属性访问。
 */
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
