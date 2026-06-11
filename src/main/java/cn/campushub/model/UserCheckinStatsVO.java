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
    public record Record(
            LocalDate checkinDate,
            int points,
            int continuousDays,
            LocalDateTime createdAt
    ) {
    }
}
