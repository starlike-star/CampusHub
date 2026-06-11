package cn.campushub.model;

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
