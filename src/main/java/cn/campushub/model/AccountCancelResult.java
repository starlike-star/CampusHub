package cn.campushub.model;

/**
 * 封装账号操作的处理结果与返回数据。
 */
public record AccountCancelResult(boolean success, String message) {
    /**
     * 创建表示操作成功的结果对象。
     *
     * @return `completed`
     */
    public static AccountCancelResult completed() {
        return new AccountCancelResult(true, "账号已注销");
    }

    /**
     * 创建表示操作失败的结果对象。
     *
     * @param message 消息数据
     * @return 方法处理结果
     */
    public static AccountCancelResult failure(String message) {
        return new AccountCancelResult(false, message);
    }
}
