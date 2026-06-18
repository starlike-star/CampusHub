package cn.campushub.dao;

import cn.campushub.model.CheckinResult;
import cn.campushub.model.ExperienceInfo;
import cn.campushub.model.HomeSidebarVO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 定义首页数据访问能力及业务层依赖的数据契约。
 */
public interface HomeDao {
    /**
     * 查询`LatestNotices`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<HomeSidebarVO.NoticeItem> findLatestNotices() throws SQLException;

    /**
     * 查询`RecommendedActivities`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<HomeSidebarVO.ActivityItem> findRecommendedActivities() throws SQLException;

    /**
     * 查询`LatestLostFound`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<HomeSidebarVO.LostFoundItem> findLatestLostFound() throws SQLException;

    /**
     * 查询签到。
     *
     * @param userId 用户编号
     * @param date 参数 `date`
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<HomeSidebarVO.CheckinStatus> findCheckin(long userId, LocalDate date)
            throws SQLException;

    /**
     * 查询经验值信息。
     *
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<ExperienceInfo> findExperienceInfo(long userId) throws SQLException;

    /**
     * 检查`In`。
     *
     * @param userId 用户编号
     * @param date 参数 `date`
     * @param points 参数 `points`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    CheckinResult checkIn(long userId, LocalDate date, int points) throws SQLException;
}
