package cn.campushub.model;

public record AccountCancelResult(boolean success, String message) {
    public static AccountCancelResult completed() {
        return new AccountCancelResult(true, "账号已注销");
    }

    public static AccountCancelResult failure(String message) {
        return new AccountCancelResult(false, message);
    }
}
