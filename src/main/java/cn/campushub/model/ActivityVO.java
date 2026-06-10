package cn.campushub.model;

public record ActivityVO(
        Activity activity,
        String creatorNickname,
        String creatorAvatar,
        String creatorCollege
) {
}
