package cn.campushub.model;

public record ClaimCreateResult(
        boolean created,
        long ownerId,
        String lostFoundTitle
) {
}
