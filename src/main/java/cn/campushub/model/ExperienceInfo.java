package cn.campushub.model;

/**
 * 承载经验值相关的只读信息。
 */
public record ExperienceInfo(
        int experience,
        int level,
        int currentLevelBaseExp,
        int nextLevelRequiredExp,
        int remainingExp,
        int progressPercent
) {
}
