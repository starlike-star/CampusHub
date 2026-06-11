package cn.campushub.model;

/**
 * 描述举报消息或跳转的目标信息。
 */
public record ReportNotificationTarget(long reporterId, Long ownerId) {
}
