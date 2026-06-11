package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的私信会话领域数据，并提供对应属性访问。
 */
public record PrivateConversation(
        long id,
        long userAId,
        long userBId,
        long otherUserId,
        String otherNickname,
        String otherAvatar,
        String lastMessage,
        LocalDateTime lastMessageAt,
        LocalDateTime createdAt,
        int unreadCount
) {
}
