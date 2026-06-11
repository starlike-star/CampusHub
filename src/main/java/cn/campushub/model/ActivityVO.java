package cn.campushub.model;

/**
 * 聚合活动页面展示所需的数据。
 */
public record ActivityVO(
        Activity activity,
        String creatorNickname,
        String creatorAvatar,
        String creatorCollege
) {
}
