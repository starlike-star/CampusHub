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
    /**
     * 根据输入计算并返回 `CheckinStatus` 的处理结果。
     *
     * @param authenticated 参数 `authenticated`
     * @param checkedIn 参数 `checkedIn`
     * @param points 参数 `points`
     * @param continuousDays 参数 `continuousDays`
     * @return 方法处理结果
     */
    public record CheckinStatus(
            boolean authenticated,
            boolean checkedIn,
            int points,
            int continuousDays
    ) {
        /**
         * 获取`guest`。
         *
         * @return `guest`
         */
        public static CheckinStatus guest() {
            return new CheckinStatus(false, false, 5, 0);
        }

        /**
         * 获取`pending`。
         *
         * @return `pending`
         */
        public static CheckinStatus pending() {
            return new CheckinStatus(true, false, 5, 0);
        }
    }

    /**
     * 根据输入计算并返回 `NoticeItem` 的处理结果。
     *
     * @param id 业务数据编号
     * @param title 标题
     * @param type 参数 `type`
     * @param createdAt 创建时间
     * @return 方法处理结果
     */
    public record NoticeItem(
            long id,
            String title,
            String type,
            LocalDateTime createdAt
    ) {
    }

    /**
     * 根据输入计算并返回 `ActivityItem` 的处理结果。
     *
     * @param id 业务数据编号
     * @param title 标题
     * @param coverImage 封面图片地址
     * @param location 参数 `location`
     * @param startTime 开始时间
     * @param currentMembers 当前人数
     * @param maxMembers 人数上限
     * @return 方法处理结果
     */
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

    /**
     * 根据输入计算并返回 `LostFoundItem` 的处理结果。
     *
     * @param id 业务数据编号
     * @param title 标题
     * @param type 参数 `type`
     * @param place 参数 `place`
     * @param createdAt 创建时间
     * @return 方法处理结果
     */
    public record LostFoundItem(
            long id,
            String title,
            String type,
            String place,
            LocalDateTime createdAt
    ) {
    }
}
