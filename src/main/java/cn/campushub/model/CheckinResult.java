package cn.campushub.model;

/**
 * 封装Checkin操作的处理结果与返回数据。
 */
public record CheckinResult(
        boolean success,
        boolean checkedIn,
        int points,
        int continuousDays,
        ExperienceInfo experience,
        String message
) {
    /**
     * 创建表示操作成功的结果对象。
     *
     * @param points 参数 `points`
     * @param continuousDays 参数 `continuousDays`
     * @param experience 参数 `experience`
     * @return 方法处理结果
     */
    public static CheckinResult success(
            int points,
            int continuousDays,
            ExperienceInfo experience
    ) {
        return new CheckinResult(
                true,
                true,
                points,
                continuousDays,
                experience,
                "签到成功"
        );
    }

    /**
     * 根据输入计算并返回 `alreadyCheckedIn` 的处理结果。
     *
     * @param points 参数 `points`
     * @param continuousDays 参数 `continuousDays`
     * @param experience 参数 `experience`
     * @return 方法处理结果
     */
    public static CheckinResult alreadyCheckedIn(
            int points,
            int continuousDays,
            ExperienceInfo experience
    ) {
        return new CheckinResult(
                false,
                true,
                points,
                continuousDays,
                experience,
                "今日已签到"
        );
    }
}
