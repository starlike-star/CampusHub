package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的公开用户主页领域数据，并提供对应属性访问。
 */
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
