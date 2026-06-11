package cn.campushub.model;

import java.time.LocalDateTime;

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
