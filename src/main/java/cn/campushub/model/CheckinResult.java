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
