package cn.campushub.model;

/**
 * 聚合个人主页页面展示所需的数据。
 */
public record ProfileOverviewVO(User user, Stats stats) {
    /**
     * 根据输入计算并返回 `Stats` 的处理结果。
     *
     * @param postCount 参数 `postCount`
     * @param commentCount 参数 `commentCount`
     * @param favoriteCount 参数 `favoriteCount`
     * @param goodsCount 参数 `goodsCount`
     * @param checkinDays 参数 `checkinDays`
     * @param continuousDays 参数 `continuousDays`
     * @return 方法处理结果
     */
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
