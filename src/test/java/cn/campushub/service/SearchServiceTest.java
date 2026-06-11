package cn.campushub.service;

import cn.campushub.dao.SearchDao;
import cn.campushub.model.SearchPageVO;
import cn.campushub.model.SearchResultVO;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class SearchServiceTest {
    @Test
    void emptyKeywordDoesNotQueryDatabase() throws Exception {
        FakeSearchDao dao = new FakeSearchDao();
        SearchPageVO page = new SearchService(dao).search("   ", "post");

        assertEquals("请输入关键词进行搜索", page.validationMessage());
        assertFalse(dao.counted);
    }

    @Test
    void invalidTypeFallsBackToAllAndUsesFiveItemLimits() throws Exception {
        FakeSearchDao dao = new FakeSearchDao();
        SearchPageVO page = new SearchService(dao).search(" 校园卡 ", "bad");

        assertEquals("all", page.type());
        assertEquals("校园卡", page.keyword());
        assertEquals(List.of(5, 5, 5, 5, 5), dao.limits);
        assertNull(page.validationMessage());
    }

    @Test
    void singleTypeOnlyLoadsSelectedResultsWithTwentyItemLimit()
            throws Exception {
        FakeSearchDao dao = new FakeSearchDao();
        SearchPageVO page = new SearchService(dao).search("活动", "activity");

        assertEquals(List.of("activity"), dao.queriedTypes);
        assertEquals(List.of(20), dao.limits);
        assertEquals(1, page.totalCount());
    }

    @Test
    void overlongKeywordDoesNotQueryDatabase() throws Exception {
        FakeSearchDao dao = new FakeSearchDao();
        SearchPageVO page = new SearchService(dao).search("x".repeat(51), "all");

        assertEquals("搜索关键词不能超过 50 个字符", page.validationMessage());
        assertFalse(dao.counted);
    }

    private static class FakeSearchDao implements SearchDao {
        private boolean counted;
        private final List<String> queriedTypes = new java.util.ArrayList<>();
        private final List<Integer> limits = new java.util.ArrayList<>();

        @Override
        public Map<String, Integer> countMatches(String keyword) {
            counted = true;
            Map<String, Integer> counts = new LinkedHashMap<>();
            counts.put("post", 0);
            counts.put("goods", 0);
            counts.put("lost_found", 0);
            counts.put("activity", 1);
            counts.put("notice", 0);
            return counts;
        }

        @Override
        public List<SearchResultVO> searchPosts(String keyword, int limit) {
            return searched("post", limit);
        }

        @Override
        public List<SearchResultVO> searchGoods(String keyword, int limit) {
            return searched("goods", limit);
        }

        @Override
        public List<SearchResultVO> searchLostFound(String keyword, int limit) {
            return searched("lost_found", limit);
        }

        @Override
        public List<SearchResultVO> searchActivities(String keyword, int limit) {
            return searched("activity", limit);
        }

        @Override
        public List<SearchResultVO> searchNotices(String keyword, int limit) {
            return searched("notice", limit);
        }

        private List<SearchResultVO> searched(String type, int limit) {
            queriedTypes.add(type);
            limits.add(limit);
            return List.of();
        }
    }
}
