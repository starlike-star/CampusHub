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

class LostFoundServiceTest {
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

    @Test
    void createRejectsInvalidTypeAndRequiredFields() throws SQLException {
        LostFoundService service = new LostFoundService(new FakeLostFoundDao());

        ServiceResult<Long> result = service.create(
                7L, "other", "", "", "3",
                "", "", "", "", ""
        );

        assertFalse(result.success());
    }

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

        @Override
        public List<LostFound> findByUser(long userId) {
            return List.of();
        }

        @Override
        public List<Category> findActiveCategories() {
            return List.of();
        }

        @Override
        public boolean isActiveCategory(long categoryId) {
            return categoryId == 3L;
        }

        @Override
        public long create(LostFound lostFound) {
            created = lostFound;
            return 99L;
        }

        @Override
        public Optional<LostFound> findById(long id) {
            return Optional.empty();
        }

        @Override
        public boolean update(LostFound lostFound, boolean admin) {
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
