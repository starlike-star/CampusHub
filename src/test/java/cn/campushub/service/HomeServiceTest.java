package cn.campushub.service;

import cn.campushub.dao.HomeDao;
import cn.campushub.model.CheckinResult;
import cn.campushub.model.ExperienceInfo;
import cn.campushub.model.HomeSidebarVO;
import cn.campushub.util.LevelUtils;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 首页相关逻辑的正常路径、边界条件和失败场景。
 */
class HomeServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-06-09T08:00:00Z"),
            ZoneOffset.UTC
    );

    @Test
    void guestSidebarDoesNotQueryCheckin() throws SQLException {
        FakeHomeDao dao = new FakeHomeDao();
        HomeService service = new HomeService(dao, CLOCK);

        HomeSidebarVO sidebar = service.loadSidebar(null);

        assertFalse(sidebar.checkin().authenticated());
        assertFalse(dao.checkinQueried);
    }

    @Test
    void signedInSidebarQueriesToday() throws SQLException {
        FakeHomeDao dao = new FakeHomeDao();
        dao.checkinStatus = Optional.of(
                new HomeSidebarVO.CheckinStatus(true, true, 5, 4)
        );
        HomeService service = new HomeService(dao, CLOCK);

        HomeSidebarVO sidebar = service.loadSidebar(7L);

        assertTrue(sidebar.checkin().checkedIn());
        assertEquals(LocalDate.of(2026, 6, 9), dao.requestedDate);
        assertEquals(4, sidebar.checkin().continuousDays());
    }

    @Test
    void checkInUsesTodayAndFivePoints() throws SQLException {
        FakeHomeDao dao = new FakeHomeDao();
        HomeService service = new HomeService(dao, CLOCK);

        CheckinResult result = service.checkIn(7L);

        assertTrue(result.success());
        assertEquals(5, dao.requestedPoints);
        assertEquals(LocalDate.of(2026, 6, 9), dao.requestedDate);
    }

    private static class FakeHomeDao implements HomeDao {
        private Optional<HomeSidebarVO.CheckinStatus> checkinStatus = Optional.empty();
        private boolean checkinQueried;
        private LocalDate requestedDate;
        private int requestedPoints;

        @Override
        public List<HomeSidebarVO.NoticeItem> findLatestNotices() {
            return List.of();
        }

        @Override
        public List<HomeSidebarVO.ActivityItem> findRecommendedActivities() {
            return List.of();
        }

        @Override
        public List<HomeSidebarVO.LostFoundItem> findLatestLostFound() {
            return List.of();
        }

        @Override
        public Optional<HomeSidebarVO.CheckinStatus> findCheckin(
                long userId,
                LocalDate date
        ) {
            checkinQueried = true;
            requestedDate = date;
            return checkinStatus;
        }

        @Override
        public Optional<ExperienceInfo> findExperienceInfo(long userId) {
            return Optional.of(LevelUtils.experienceInfo(0));
        }

        @Override
        public CheckinResult checkIn(long userId, LocalDate date, int points) {
            requestedDate = date;
            requestedPoints = points;
            return CheckinResult.success(
                    points,
                    1,
                    LevelUtils.experienceInfo(points)
            );
        }
    }
}
