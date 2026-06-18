package cn.campushub.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 聚合用户页面展示所需的数据。
 */
public record UserCheckinStatsVO(
        int totalDays,
        int totalPoints,
        int continuousDays,
        List<Record> records
) {
    /**
     * 根据输入计算并返回 `Record` 的处理结果。
     *
     * @param checkinDate 参数 `checkinDate`
     * @param points 参数 `points`
     * @param continuousDays 参数 `continuousDays`
     * @param createdAt 创建时间
     * @return 方法处理结果
     */
    public record Record(
            LocalDate checkinDate,
            int points,
            int continuousDays,
            LocalDateTime createdAt
    ) {
    }
}
