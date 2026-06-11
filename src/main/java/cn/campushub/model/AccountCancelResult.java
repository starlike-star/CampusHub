package cn.campushub.model;

/**
 * 封装账号操作的处理结果与返回数据。
 */
public record AccountCancelResult(boolean success, String message) {
    public static AccountCancelResult completed() {
        return new AccountCancelResult(true, "账号已注销");
    }

    public static AccountCancelResult failure(String message) {
        return new AccountCancelResult(false, message);
    }
}
