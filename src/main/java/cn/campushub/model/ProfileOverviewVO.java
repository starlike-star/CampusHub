package cn.campushub.model;

public record ProfileOverviewVO(User user, Stats stats) {
    public record Stats(
            int postCount,
            int commentCount,
            int favoriteCount,
            int goodsCount,
            int checkinDays,
            int continuousDays
    ) {
    }
}
