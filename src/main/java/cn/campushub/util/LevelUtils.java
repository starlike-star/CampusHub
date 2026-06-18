package cn.campushub.util;

import cn.campushub.model.ExperienceInfo;

/**
 * 根据经验值计算用户等级、进度和升级阈值。
 */
public final class LevelUtils {
    private static final int FIRST_LEVEL_REQUIRED_EXP = 100;

    /**
     * 初始化等级对象及其运行所需依赖。
     */
    private LevelUtils() {
    }

    /**
     * 计算等级。
     *
     * @param experience 参数 `experience`
     * @return 方法处理结果
     */
    public static int calculateLevel(int experience) {
        int normalizedExperience = Math.max(experience, 0);
        int level = 1;
        while (level < Integer.SIZE
                && normalizedExperience >= requiredExpAsLong(level)) {
            level++;
        }
        return level;
    }

    /**
     * 根据输入计算并返回 `nextLevelRequiredExp` 的处理结果。
     *
     * @param currentLevel 参数 `currentLevel`
     * @return 方法处理结果
     */
    public static int nextLevelRequiredExp(int currentLevel) {
        return clampToInt(requiredExpAsLong(Math.max(currentLevel, 1)));
    }

    /**
     * 获取当前`LevelBaseExp`。
     *
     * @param currentLevel 参数 `currentLevel`
     * @return 方法处理结果
     */
    public static int currentLevelBaseExp(int currentLevel) {
        return currentLevel <= 1
                ? 0
                : clampToInt(requiredExpAsLong(currentLevel - 1));
    }

    /**
     * 根据输入计算并返回 `progressToNextLevel` 的处理结果。
     *
     * @param experience 参数 `experience`
     * @return 方法处理结果
     */
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

    /**
     * 根据输入计算并返回 `experienceInfo` 的处理结果。
     *
     * @param experience 参数 `experience`
     * @return 方法处理结果
     */
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

    /**
     * 根据输入计算并返回 `requiredExpAsLong` 的处理结果。
     *
     * @param level 参数 `level`
     * @return 方法处理结果
     */
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

    /**
     * 根据输入计算并返回 `clampToInt` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private static int clampToInt(long value) {
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }
}
