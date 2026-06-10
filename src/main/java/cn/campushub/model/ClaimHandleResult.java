package cn.campushub.model;

public record ClaimHandleResult(
        long applicantId,
        String lostFoundTitle,
        boolean approved
) {
}
