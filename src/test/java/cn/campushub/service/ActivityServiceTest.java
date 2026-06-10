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

class ActivityServiceTest {
    @Test
    void listNormalizesFilters() throws SQLException {
        FakeActivityDao dao = new FakeActivityDao();
        ActivityService service = new ActivityService(dao);

        service.list("unknown", "  摄影  ", "unknown");

        assertEquals(null, dao.status);
        assertEquals("摄影", dao.keyword);
        assertEquals("latest", dao.sort);
    }

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

        @Override
        public Optional<ActivityVO> findById(long id) {
            return Optional.ofNullable(existing);
        }

        @Override
        public long create(Activity activity) {
            created = activity;
            return 11L;
        }

        @Override
        public boolean update(Activity activity, long userId, boolean admin) {
            updated = true;
            return true;
        }

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
