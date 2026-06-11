package cn.campushub.util;

import cn.campushub.model.ExperienceInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 LevelUtils相关逻辑的正常路径、边界条件和失败场景。
 */
class LevelUtilsTest {
    @Test
    void calculatesLevelsAtConfiguredThresholds() {
        assertEquals(1, LevelUtils.calculateLevel(0));
        assertEquals(1, LevelUtils.calculateLevel(99));
        assertEquals(2, LevelUtils.calculateLevel(100));
        assertEquals(3, LevelUtils.calculateLevel(200));
        assertEquals(4, LevelUtils.calculateLevel(400));
        assertEquals(5, LevelUtils.calculateLevel(800));
    }

    @Test
    void calculatesCurrentRangeAndProgress() {
        ExperienceInfo info = LevelUtils.experienceInfo(150);

        assertEquals(2, info.level());
        assertEquals(100, info.currentLevelBaseExp());
        assertEquals(200, info.nextLevelRequiredExp());
        assertEquals(50, info.remainingExp());
        assertEquals(50, info.progressPercent());
    }

    @Test
    void normalizesNegativeExperience() {
        ExperienceInfo info = LevelUtils.experienceInfo(-5);

        assertEquals(0, info.experience());
        assertEquals(1, info.level());
        assertEquals(100, info.remainingExp());
        assertEquals(0, info.progressPercent());
    }
}
