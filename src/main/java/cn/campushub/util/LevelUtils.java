package cn.campushub.util;

import cn.campushub.model.ExperienceInfo;

public final class LevelUtils {
    private static final int FIRST_LEVEL_REQUIRED_EXP = 100;

    private LevelUtils() {
    }

    public static int calculateLevel(int experience) {
        int normalizedExperience = Math.max(experience, 0);
        int level = 1;
        while (level < Integer.SIZE
                && normalizedExperience >= requiredExpAsLong(level)) {
            level++;
        }
        return level;
    }

    public static int nextLevelRequiredExp(int currentLevel) {
        return clampToInt(requiredExpAsLong(Math.max(currentLevel, 1)));
    }

    public static int currentLevelBaseExp(int currentLevel) {
        return currentLevel <= 1
                ? 0
                : clampToInt(requiredExpAsLong(currentLevel - 1));
    }

    public static int progressToNextLevel(int experience) {
        int normalizedExperience = Math.max(experience, 0);
        int level = calculateLevel(normalizedExperience);
        int base = currentLevelBaseExp(level);
        int next = nextLevelRequiredExp(level);
        if (next <= base) {
            return 100;
        }
        long progress = (long) (normalizedExperience - base) * 100
                / (next - base);
        return (int) Math.max(0, Math.min(progress, 100));
    }

    public static ExperienceInfo experienceInfo(int experience) {
        int normalizedExperience = Math.max(experience, 0);
        int level = calculateLevel(normalizedExperience);
        int base = currentLevelBaseExp(level);
        int next = nextLevelRequiredExp(level);
        return new ExperienceInfo(
                normalizedExperience,
                level,
                base,
                next,
                Math.max(next - normalizedExperience, 0),
                progressToNextLevel(normalizedExperience)
        );
    }

    private static long requiredExpAsLong(int level) {
        int exponent = Math.max(level - 1, 0);
        if (exponent >= Long.SIZE - 1) {
            return Long.MAX_VALUE;
        }
        long multiplier = 1L << exponent;
        if (multiplier > Long.MAX_VALUE / FIRST_LEVEL_REQUIRED_EXP) {
            return Long.MAX_VALUE;
        }
        return FIRST_LEVEL_REQUIRED_EXP * multiplier;
    }

    private static int clampToInt(long value) {
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }
}
