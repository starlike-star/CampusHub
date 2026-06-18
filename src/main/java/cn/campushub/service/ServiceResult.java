package cn.campushub.service;

/**
 * 统一封装业务操作的成功状态、提示消息和可选返回数据。
 */
public record ServiceResult<T>(boolean success, String message, T data) {
    /**
     * 创建表示操作成功的结果对象。
     *
     * @param message 消息数据
     * @param data 参数 `data`
     * @return 包含处理状态、提示信息和业务数据的结果
     */
    public static <T> ServiceResult<T> success(String message, T data) {
        return new ServiceResult<>(true, message, data);
    }

    /**
     * 创建表示操作失败的结果对象。
     *
     * @param message 消息数据
     * @return 包含处理状态、提示信息和业务数据的结果
     */
    public static <T> ServiceResult<T> failure(String message) {
        return new ServiceResult<>(false, message, null);
    }
}
