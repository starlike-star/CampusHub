package cn.campushub.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聚合首页页面展示所需的数据。
 */
public record HomeSidebarVO(
        CheckinStatus checkin,
        ExperienceInfo experience,
        List<NoticeItem> notices,
        List<ActivityItem> activities,
        List<LostFoundItem> lostFoundItems
) {
    public record CheckinStatus(
            boolean authenticated,
            boolean checkedIn,
            int points,
            int continuousDays
    ) {
        public static CheckinStatus guest() {
            return new CheckinStatus(false, false, 5, 0);
        }

        public static CheckinStatus pending() {
            return new CheckinStatus(true, false, 5, 0);
        }
    }

    public record NoticeItem(
            long id,
            String title,
            String type,
            LocalDateTime createdAt
    ) {
    }

    public record ActivityItem(
            long id,
            String title,
            String coverImage,
            String location,
            LocalDateTime startTime,
            int currentMembers,
            int maxMembers
    ) {
    }

    public record LostFoundItem(
            long id,
            String title,
            String type,
            String place,
            LocalDateTime createdAt
    ) {
    }
}
