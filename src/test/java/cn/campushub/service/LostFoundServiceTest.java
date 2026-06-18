package cn.campushub.service;

import cn.campushub.dao.LostFoundDao;
import cn.campushub.model.Category;
import cn.campushub.model.LostFound;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 失物招领相关逻辑的正常路径、边界条件和失败场景。
 */
class LostFoundServiceTest {
    /**
     * 验证 `createUsesSessionUserAndValidatedCategory` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void createUsesSessionUserAndValidatedCategory() throws SQLException {
        FakeLostFoundDao dao = new FakeLostFoundDao();
        LostFoundService service = new LostFoundService(dao);

        ServiceResult<Long> result = service.create(
                7L, "lost", "校园卡", "寻找校园卡", "3",
                "蓝色卡套", "图书馆", "2026-06-10T09:30",
                "", "13800000000"
        );

        assertTrue(result.success());
        assertEquals(99L, result.data());
        assertEquals(7L, dao.created.getUserId());
        assertEquals("校园卡", dao.created.getItemName());
        assertEquals(3L, dao.created.getCategoryId());
    }

    /**
     * 验证 `createRejectsInvalidTypeAndRequiredFields` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void createRejectsInvalidTypeAndRequiredFields() throws SQLException {
        LostFoundService service = new LostFoundService(new FakeLostFoundDao());

        ServiceResult<Long> result = service.create(
                7L, "other", "", "", "3",
                "", "", "", "", ""
        );

        assertFalse(result.success());
    }

    /**
     * 验证 `listNormalizesUnknownFilters` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void listNormalizesUnknownFilters() throws SQLException {
        FakeLostFoundDao dao = new FakeLostFoundDao();
        LostFoundService service = new LostFoundService(dao);

        service.list("unknown", "unknown", "  校园卡  ", "bad", "bad");

        assertEquals(null, dao.type);
        assertEquals(null, dao.status);
        assertEquals("校园卡", dao.keyword);
        assertEquals("latest", dao.sort);
    }

    /**
     * 验证 `missingOptionalFiltersUseDefaults` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void missingOptionalFiltersUseDefaults() throws SQLException {
        FakeLostFoundDao dao = new FakeLostFoundDao();
        LostFoundService service = new LostFoundService(dao);

        service.list(null, null, null, null, null);

        assertEquals(null, dao.type);
        assertEquals(null, dao.status);
        assertEquals("latest", dao.sort);
        assertEquals("all", service.normalizeTypeValue(null));
        assertEquals("all", service.normalizeStatusValue(null));
    }

    private static class FakeLostFoundDao implements LostFoundDao {
        private LostFound created;
        private String type;
        private String status;
        private String keyword;
        private String sort;

        /**
         * 查询全部`FakeLostFound`。
         *
         * @param type 参数 `type`
         * @param status 业务状态
         * @param keyword 搜索关键字
         * @param categoryId 分类编号
         * @param sort 排序方式
         * @return 符合条件的数据列表
         */
        @Override
        public List<LostFound> findAll(
                String type,
                String status,
                String keyword,
                Long categoryId,
                String sort
        ) {
            this.type = type;
            this.status = status;
            this.keyword = keyword;
            this.sort = sort;
            return List.of();
        }

        /**
         * 根据用户查询`FakeLostFound`。
         *
         * @param userId 用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<LostFound> findByUser(long userId) {
            return List.of();
        }

        /**
         * 查询`ActiveCategories`。
         *
         * @return 符合条件的数据列表
         */
        @Override
        public List<Category> findActiveCategories() {
            return List.of();
        }

        /**
         * 判断是否`ActiveCategory`。
         *
         * @param categoryId 分类编号
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean isActiveCategory(long categoryId) {
            return categoryId == 3L;
        }

        /**
         * 创建`FakeLostFound`。
         *
         * @param lostFound 参数 `lostFound`
         * @return 新建数据的编号
         */
        @Override
        public long create(LostFound lostFound) {
            created = lostFound;
            return 99L;
        }

        /**
         * 根据编号查询`FakeLostFound`。
         *
         * @param id 业务数据编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<LostFound> findById(long id) {
            return Optional.empty();
        }

        /**
         * 更新`FakeLostFound`。
         *
         * @param lostFound 参数 `lostFound`
         * @param admin 是否具有管理员权限
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean update(LostFound lostFound, boolean admin) {
            return true;
        }

        /**
         * 更新状态。
         *
         * @param id 业务数据编号
         * @param userId 用户编号
         * @param admin 是否具有管理员权限
         * @param status 业务状态
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean updateStatus(
                long id,
                long userId,
                boolean admin,
                String status
        ) {
            return true;
        }
    }
}
