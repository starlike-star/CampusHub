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
    @Test
    void invalidTabFallsBackToLatest() throws Exception {
        FakeSquareDao dao = new FakeSquareDao();
        SquareService service = new SquareService(dao);

        service.listPosts("unknown", 7L, null);

        assertEquals("latest", dao.tab);
    }

    @Test
    void noticeTabDoesNotQueryPosts() throws Exception {
        FakeSquareDao dao = new FakeSquareDao();
        SquareService service = new SquareService(dao);

        assertEquals(List.of(), service.listPosts("notice", 7L, null));
        assertNull(dao.tab);
    }

    @Test
    void tradeTabQueriesSecondHandTradePosts() throws Exception {
        FakeSquareDao dao = new FakeSquareDao();
        SquareService service = new SquareService(dao);

        service.listPosts("trade", 7L, null);

        assertEquals("trade", dao.tab);
    }

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

        @Override
        public List<Notice> findNotices(String keyword) {
            this.keyword = keyword;
            return List.of();
        }
    }
}
