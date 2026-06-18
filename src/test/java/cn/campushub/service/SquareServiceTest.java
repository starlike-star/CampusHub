package cn.campushub.service;

import cn.campushub.dao.SquareDao;
import cn.campushub.model.Notice;
import cn.campushub.model.Post;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 验证 校园广场相关逻辑的正常路径、边界条件和失败场景。
 */
class SquareServiceTest {
    /**
     * 验证 `invalidTabFallsBackToLatest` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
    @Test
    void invalidTabFallsBackToLatest() throws Exception {
        FakeSquareDao dao = new FakeSquareDao();
        SquareService service = new SquareService(dao);

        service.listPosts("unknown", 7L, null);

        assertEquals("latest", dao.tab);
    }

    /**
     * 验证 `noticeTabDoesNotQueryPosts` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
    @Test
    void noticeTabDoesNotQueryPosts() throws Exception {
        FakeSquareDao dao = new FakeSquareDao();
        SquareService service = new SquareService(dao);

        assertEquals(List.of(), service.listPosts("notice", 7L, null));
        assertNull(dao.tab);
    }

    /**
     * 验证 `tradeTabQueriesSecondHandTradePosts` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
    @Test
    void tradeTabQueriesSecondHandTradePosts() throws Exception {
        FakeSquareDao dao = new FakeSquareDao();
        SquareService service = new SquareService(dao);

        service.listPosts("trade", 7L, null);

        assertEquals("trade", dao.tab);
    }

    /**
     * 验证 `keywordIsTrimmedAndLimited` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
    @Test
    void keywordIsTrimmedAndLimited() throws Exception {
        FakeSquareDao dao = new FakeSquareDao();
        SquareService service = new SquareService(dao);
        String keyword = " x".repeat(80);

        service.listNotices(keyword);

        assertEquals(100, dao.keyword.length());
        assertEquals('x', dao.keyword.charAt(0));
    }

    private static class FakeSquareDao implements SquareDao {
        private String tab;
        private String keyword;

        /**
         * 查询帖子列表。
         *
         * @param tab 参数 `tab`
         * @param currentUserId 当前用户编号
         * @param keyword 搜索关键字
         * @return 符合条件的数据列表
         */
        @Override
        public List<Post> findPosts(
                String tab,
                Long currentUserId,
                String keyword
        ) {
            this.tab = tab;
            this.keyword = keyword;
            return List.of();
        }

        /**
         * 查询公告列表。
         *
         * @param keyword 搜索关键字
         * @return 符合条件的数据列表
         */
        @Override
        public List<Notice> findNotices(String keyword) {
            this.keyword = keyword;
            return List.of();
        }
    }
}
