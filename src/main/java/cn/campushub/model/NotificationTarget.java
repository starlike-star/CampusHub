package cn.campushub.model;

/**
 * 描述Notification消息或跳转的目标信息。
 */
public record NotificationTarget(long ownerId, String title) {
}
