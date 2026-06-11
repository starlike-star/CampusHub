package cn.campushub.service;

import cn.campushub.dao.GoodsDao;
import cn.campushub.model.Category;
import cn.campushub.model.Goods;
import cn.campushub.model.PostToggleResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 商品相关逻辑的正常路径、边界条件和失败场景。
 */
class GoodsServiceTest {
    @Test
    void createUsesSessionUserAndOnSaleData() throws Exception {
        FakeGoodsDao dao = new FakeGoodsDao();
        GoodsService service = new GoodsService(dao);

        ServiceResult<Long> result = service.create(
                7L,
                " 教材 ",
                " 九成新 ",
                "12.5",
                "3",
                "九成新",
                "",
                "图书馆",
                "offline",
                "123"
        );

        assertTrue(result.success());
        assertEquals(7L, dao.created.getUserId());
        assertEquals(new BigDecimal("12.50"), dao.created.getPrice());
        assertEquals("教材", dao.created.getTitle());
    }

    @Test
    void createRejectsInvalidCategory() throws Exception {
        FakeGoodsDao dao = new FakeGoodsDao();
        dao.activeCategory = false;
        GoodsService service = new GoodsService(dao);

        ServiceResult<Long> result = service.create(
                7L, "教材", "描述", "12", "3",
                null, null, null, "offline", null
        );

        assertFalse(result.success());
    }

    @Test
    void listNormalizesFilters() throws Exception {
        FakeGoodsDao dao = new FakeGoodsDao();
        GoodsService service = new GoodsService(dao);

        service.list(
                7L, " 教材 ", "bad", "off_shelf", "invalid", "bad"
        );

        assertEquals("教材", dao.keyword);
        assertEquals("latest", dao.sort);
        assertEquals(null, dao.status);
        assertEquals(null, dao.tradeMethod);
        assertEquals(null, dao.categoryId);
    }

    @Test
    void listIgnoresInactiveCategory() throws Exception {
        FakeGoodsDao dao = new FakeGoodsDao();
        dao.activeCategory = false;
        GoodsService service = new GoodsService(dao);

        service.list(7L, null, "88", "on_sale", "online", "latest");

        assertEquals(null, dao.categoryId);
        assertEquals("on_sale", dao.status);
        assertEquals("online", dao.tradeMethod);
    }

    private static class FakeGoodsDao implements GoodsDao {
        private boolean activeCategory = true;
        private Goods created;
        private String keyword;
        private Long categoryId;
        private String status;
        private String tradeMethod;
        private String sort;

        @Override
        public List<Goods> findGoods(
                Long currentUserId,
                String keyword,
                Long categoryId,
                String status,
                String tradeMethod,
                String sort
        ) {
            this.keyword = keyword;
            this.categoryId = categoryId;
            this.status = status;
            this.tradeMethod = tradeMethod;
            this.sort = sort;
            return List.of();
        }

        @Override
        public List<Goods> findOwnGoods(long userId) {
            return List.of();
        }

        @Override
        public List<Goods> findFavoriteGoods(long userId) {
            return List.of();
        }

        @Override
        public List<Category> findActiveGoodsCategories() {
            return List.of();
        }

        @Override
        public boolean isActiveGoodsCategory(long categoryId) {
            return activeCategory;
        }

        @Override
        public long create(Goods goods) {
            created = goods;
            return 99L;
        }

        @Override
        public Optional<Goods> findVisibleById(long goodsId, Long currentUserId) {
            return Optional.empty();
        }

        @Override
        public PostToggleResult toggleFavorite(long goodsId, long userId) {
            return new PostToggleResult(true, 1);
        }

        @Override
        public Optional<Goods> update(Goods goods, boolean admin) {
            return Optional.of(goods);
        }

        @Override
        public boolean updateStatus(
                long goodsId,
                long userId,
                boolean admin,
                String status
        ) {
            return true;
        }
    }
}
