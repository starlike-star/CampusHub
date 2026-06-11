package cn.campushub.model;

import java.time.LocalDateTime;

public record PrivateMessage(
        long id,
        long conversationId,
        long senderId,
        long receiverId,
        String content,
        boolean read,
        LocalDateTime createdAt
) {
}
