package cn.campushub.service;

/**
 * 统一封装业务操作的成功状态、提示消息和可选返回数据。
 */
public record ServiceResult<T>(boolean success, String message, T data) {
    public static <T> ServiceResult<T> success(String message, T data) {
        return new ServiceResult<>(true, message, data);
    }

    public static <T> ServiceResult<T> failure(String message) {
        return new ServiceResult<>(false, message, null);
    }
}
