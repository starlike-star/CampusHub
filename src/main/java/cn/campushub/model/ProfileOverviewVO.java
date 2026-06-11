package cn.campushub.model;

/**
 * 聚合个人主页页面展示所需的数据。
 */
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
