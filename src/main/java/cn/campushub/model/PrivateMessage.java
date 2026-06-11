package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的私信消息领域数据，并提供对应属性访问。
 */
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
