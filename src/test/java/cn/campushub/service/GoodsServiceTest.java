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
    /**
     * 验证 `createUsesSessionUserAndOnSaleData` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
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

    /**
     * 验证 `createRejectsInvalidCategory` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
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

    /**
     * 验证 `listNormalizesFilters` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
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

    /**
     * 验证 `listIgnoresInactiveCategory` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
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

        /**
         * 查询商品。
         *
         * @param currentUserId 当前用户编号
         * @param keyword 搜索关键字
         * @param categoryId 分类编号
         * @param status 业务状态
         * @param tradeMethod 参数 `tradeMethod`
         * @param sort 排序方式
         * @return 符合条件的数据列表
         */
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

        /**
         * 查询`OwnGoods`。
         *
         * @param userId 用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<Goods> findOwnGoods(long userId) {
            return List.of();
        }

        /**
         * 查询收藏商品。
         *
         * @param userId 用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<Goods> findFavoriteGoods(long userId) {
            return List.of();
        }

        /**
         * 查询`ActiveGoodsCategories`。
         *
         * @return 符合条件的数据列表
         */
        @Override
        public List<Category> findActiveGoodsCategories() {
            return List.of();
        }

        /**
         * 判断是否`ActiveGoodsCategory`。
         *
         * @param categoryId 分类编号
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean isActiveGoodsCategory(long categoryId) {
            return activeCategory;
        }

        /**
         * 创建模拟商品。
         *
         * @param goods 商品数据
         * @return 新建数据的编号
         */
        @Override
        public long create(Goods goods) {
            created = goods;
            return 99L;
        }

        /**
         * 查询`VisibleById`。
         *
         * @param goodsId 商品编号
         * @param currentUserId 当前用户编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<Goods> findVisibleById(long goodsId, Long currentUserId) {
            return Optional.empty();
        }

        /**
         * 切换收藏。
         *
         * @param goodsId 商品编号
         * @param userId 用户编号
         * @return 方法处理结果
         */
        @Override
        public PostToggleResult toggleFavorite(long goodsId, long userId) {
            return new PostToggleResult(true, 1);
        }

        /**
         * 更新模拟商品。
         *
         * @param goods 商品数据
         * @param admin 是否具有管理员权限
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<Goods> update(Goods goods, boolean admin) {
            return Optional.of(goods);
        }

        /**
         * 更新状态。
         *
         * @param goodsId 商品编号
         * @param userId 用户编号
         * @param admin 是否具有管理员权限
         * @param status 业务状态
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
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
