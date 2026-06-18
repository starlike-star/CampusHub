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

    /**
     * 验证 `guestSidebarDoesNotQueryCheckin` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void guestSidebarDoesNotQueryCheckin() throws SQLException {
        FakeHomeDao dao = new FakeHomeDao();
        HomeService service = new HomeService(dao, CLOCK);

        HomeSidebarVO sidebar = service.loadSidebar(null);

        assertFalse(sidebar.checkin().authenticated());
        assertFalse(dao.checkinQueried);
    }

    /**
     * 验证 `signedInSidebarQueriesToday` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 验证 `checkInUsesTodayAndFivePoints` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
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

        /**
         * 查询`LatestNotices`。
         *
         * @return 符合条件的数据列表
         */
        @Override
        public List<HomeSidebarVO.NoticeItem> findLatestNotices() {
            return List.of();
        }

        /**
         * 查询`RecommendedActivities`。
         *
         * @return 符合条件的数据列表
         */
        @Override
        public List<HomeSidebarVO.ActivityItem> findRecommendedActivities() {
            return List.of();
        }

        /**
         * 查询`LatestLostFound`。
         *
         * @return 符合条件的数据列表
         */
        @Override
        public List<HomeSidebarVO.LostFoundItem> findLatestLostFound() {
            return List.of();
        }

        /**
         * 查询签到。
         *
         * @param userId 用户编号
         * @param date 参数 `date`
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<HomeSidebarVO.CheckinStatus> findCheckin(
                long userId,
                LocalDate date
        ) {
            checkinQueried = true;
            requestedDate = date;
            return checkinStatus;
        }

        /**
         * 查询经验值信息。
         *
         * @param userId 用户编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<ExperienceInfo> findExperienceInfo(long userId) {
            return Optional.of(LevelUtils.experienceInfo(0));
        }

        /**
         * 检查`In`。
         *
         * @param userId 用户编号
         * @param date 参数 `date`
         * @param points 参数 `points`
         * @return 方法处理结果
         */
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
