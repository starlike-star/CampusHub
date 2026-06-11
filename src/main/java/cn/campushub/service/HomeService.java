package cn.campushub.service;

import cn.campushub.dao.HomeDao;
import cn.campushub.dao.JdbcHomeDao;
import cn.campushub.model.CheckinResult;
import cn.campushub.model.ExperienceInfo;
import cn.campushub.model.HomeSidebarVO;
import cn.campushub.util.LevelUtils;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;

public class HomeService {
    private static final int DAILY_POINTS = 5;

    private final HomeDao homeDao;
    private final Clock clock;

    public HomeService() {
        this(new JdbcHomeDao(), Clock.systemDefaultZone());
    }

    HomeService(HomeDao homeDao, Clock clock) {
        this.homeDao = homeDao;
        this.clock = clock;
    }

    public HomeSidebarVO loadSidebar(Long userId) throws SQLException {
        HomeSidebarVO.CheckinStatus checkin = userId == null
                ? HomeSidebarVO.CheckinStatus.guest()
                : homeDao.findCheckin(userId, LocalDate.now(clock))
                        .orElse(HomeSidebarVO.CheckinStatus.pending());
        ExperienceInfo experience = userId == null
                ? LevelUtils.experienceInfo(0)
                : homeDao.findExperienceInfo(userId)
                        .orElseGet(() -> LevelUtils.experienceInfo(0));
        return new HomeSidebarVO(
                checkin,
                experience,
                homeDao.findLatestNotices(),
                homeDao.findRecommendedActivities(),
                homeDao.findLatestLostFound()
        );
    }

    public CheckinResult checkIn(long userId) throws SQLException {
        return homeDao.checkIn(userId, LocalDate.now(clock), DAILY_POINTS);
    }
}
