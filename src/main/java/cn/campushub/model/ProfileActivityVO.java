package cn.campushub.model;

import java.time.LocalDateTime;

public record ProfileActivityVO(
        long id,
        String title,
        String coverImage,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime deadline,
        String status,
        int currentMembers,
        int maxMembers,
        String registrationStatus,
        LocalDateTime registeredAt
) {
}
