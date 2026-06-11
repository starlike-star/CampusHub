package cn.campushub.dao;

import cn.campushub.model.CheckinResult;
import cn.campushub.model.ExperienceInfo;
import cn.campushub.model.HomeSidebarVO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HomeDao {
    List<HomeSidebarVO.NoticeItem> findLatestNotices() throws SQLException;

    List<HomeSidebarVO.ActivityItem> findRecommendedActivities() throws SQLException;

    List<HomeSidebarVO.LostFoundItem> findLatestLostFound() throws SQLException;

    Optional<HomeSidebarVO.CheckinStatus> findCheckin(long userId, LocalDate date)
            throws SQLException;

    Optional<ExperienceInfo> findExperienceInfo(long userId) throws SQLException;

    CheckinResult checkIn(long userId, LocalDate date, int points) throws SQLException;
}
