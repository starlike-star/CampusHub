package cn.campushub.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 验证 TradeUrlUtils相关逻辑的正常路径、边界条件和失败场景。
 */
class TradeUrlUtilsTest {
    /**
     * 验证 `appendsContextPathWhenConfiguredUrlOnlyContainsHostAndPort` 场景下的业务行为与预期结果一致。
     */
    @Test
    void appendsContextPathWhenConfiguredUrlOnlyContainsHostAndPort() {
        assertEquals(
                "http://192.168.1.20:8080/CampusHub",
                TradeUrlUtils.normalizeConfiguredBaseUrl(
                        "http://192.168.1.20:8080/",
                        "/CampusHub"
                )
        );
    }

    /**
     * 验证 `keepsConfiguredApplicationPathWithoutDuplicatingContextPath` 场景下的业务行为与预期结果一致。
     */
    @Test
    void keepsConfiguredApplicationPathWithoutDuplicatingContextPath() {
        assertEquals(
                "https://campus.example.edu/apps/CampusHub",
                TradeUrlUtils.normalizeConfiguredBaseUrl(
                        "https://campus.example.edu/apps/CampusHub/",
                        "/CampusHub"
                )
        );
    }

    /**
     * 验证 `rejectsRelativeOrQueryBasedConfiguration` 场景下的业务行为与预期结果一致。
     */
    @Test
    void rejectsRelativeOrQueryBasedConfiguration() {
        assertNull(TradeUrlUtils.normalizeConfiguredBaseUrl(
                "localhost:8080",
                "/CampusHub"
        ));
        assertNull(TradeUrlUtils.normalizeConfiguredBaseUrl(
                "https://campus.example.edu?target=CampusHub",
                "/CampusHub"
        ));
    }
}
