package cn.campushub.service;

import cn.campushub.dao.ActivityDao;
import cn.campushub.model.Activity;
import cn.campushub.model.ActivityVO;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 活动相关逻辑的正常路径、边界条件和失败场景。
 */
class ActivityServiceTest {
    /**
     * 验证 `listNormalizesFilters` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void listNormalizesFilters() throws SQLException {
        FakeActivityDao dao = new FakeActivityDao();
        ActivityService service = new ActivityService(dao);

        service.list("unknown", "  摄影  ", "unknown");

        assertEquals(null, dao.status);
        assertEquals("摄影", dao.keyword);
        assertEquals("latest", dao.sort);
    }

    /**
     * 验证 `createValidatesTimeOrderAndPersistsCreator` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void createValidatesTimeOrderAndPersistsCreator() throws SQLException {
        FakeActivityDao dao = new FakeActivityDao();
        ActivityService service = new ActivityService(dao);

        assertFalse(service.create(
                9L,
                "摄影活动",
                "校园摄影交流",
                null,
                "图书馆",
                "2026-06-12T10:00",
                "2026-06-12T09:00",
                "2026-06-11T18:00",
                "20"
        ).success());

        ServiceResult<Long> result = service.create(
                9L,
                "摄影活动",
                "校园摄影交流",
                null,
                "图书馆",
                "2026-06-12T10:00",
                "2026-06-12T12:00",
                "2026-06-11T18:00",
                "20"
        );

        assertTrue(result.success());
        assertEquals(9L, dao.created.getCreatedBy());
        assertEquals(20, dao.created.getMaxMembers());
    }

    /**
     * 验证 `updateRejectsLimitBelowCurrentMembers` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void updateRejectsLimitBelowCurrentMembers() throws SQLException {
        FakeActivityDao dao = new FakeActivityDao();
        Activity existing = new Activity();
        existing.setId(3L);
        existing.setCurrentMembers(8);
        dao.existing = new ActivityVO(existing, "发布者", null, null);
        ActivityService service = new ActivityService(dao);

        ServiceResult<Activity> result = service.update(
                3L,
                2L,
                false,
                "活动",
                "内容",
                null,
                "操场",
                "2026-06-12T10:00",
                "2026-06-12T12:00",
                "2026-06-11T18:00",
                "5"
        );

        assertFalse(result.success());
        assertEquals("人数上限不能小于当前报名人数", result.message());
        assertFalse(dao.updated);
    }

    private static final class FakeActivityDao implements ActivityDao {
        private String status;
        private String keyword;
        private String sort;
        private Activity created;
        private ActivityVO existing;
        private boolean updated;

        /**
         * 查询全部模拟活动。
         *
         * @param status 业务状态
         * @param keyword 搜索关键字
         * @param sort 排序方式
         * @return 符合条件的数据列表
         */
        @Override
        public List<ActivityVO> findAll(
                String status,
                String keyword,
                String sort
        ) {
            this.status = status;
            this.keyword = keyword;
            this.sort = sort;
            return List.of();
        }

        /**
         * 根据编号查询模拟活动。
         *
         * @param id 业务数据编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<ActivityVO> findById(long id) {
            return Optional.ofNullable(existing);
        }

        /**
         * 创建模拟活动。
         *
         * @param activity 活动数据
         * @return 新建数据的编号
         */
        @Override
        public long create(Activity activity) {
            created = activity;
            return 11L;
        }

        /**
         * 更新模拟活动。
         *
         * @param activity 活动数据
         * @param userId 用户编号
         * @param admin 是否具有管理员权限
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean update(Activity activity, long userId, boolean admin) {
            updated = true;
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
