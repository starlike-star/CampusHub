package cn.campushub.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
