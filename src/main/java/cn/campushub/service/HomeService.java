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

/**
 * 编排首页业务规则、参数校验与数据访问操作。
 */
public class HomeService {
    private static final int DAILY_POINTS = 5;

    private final HomeDao homeDao;
    private final Clock clock;

    /**
     * 初始化首页对象及其运行所需依赖。
     */
    public HomeService() {
        this(new JdbcHomeDao(), Clock.systemDefaultZone());
    }

    HomeService(HomeDao homeDao, Clock clock) {
        this.homeDao = homeDao;
        this.clock = clock;
    }

    /**
     * 加载`Sidebar`。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 检查`In`。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public CheckinResult checkIn(long userId) throws SQLException {
        return homeDao.checkIn(userId, LocalDate.now(clock), DAILY_POINTS);
    }
}
