package cn.campushub.model;

public record ExperienceInfo(
        int experience,
        int level,
        int currentLevelBaseExp,
        int nextLevelRequiredExp,
        int remainingExp,
        int progressPercent
) {
}
