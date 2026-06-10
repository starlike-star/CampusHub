package cn.campushub.model;

public record ActivityRegistrationResult(
        boolean registered,
        int currentMembers
) {
}
