package cn.campushub.model;

public record CheckinResult(
        boolean success,
        boolean checkedIn,
        int points,
        int continuousDays,
        String message
) {
    public static CheckinResult success(int points, int continuousDays) {
        return new CheckinResult(true, true, points, continuousDays, "签到成功");
    }

    public static CheckinResult alreadyCheckedIn(int points, int continuousDays) {
        return new CheckinResult(false, true, points, continuousDays, "今日已签到");
    }
}
