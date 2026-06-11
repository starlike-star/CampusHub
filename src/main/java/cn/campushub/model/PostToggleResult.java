package cn.campushub.model;

/**
 * 封装帖子操作的处理结果与返回数据。
 */
public record PostToggleResult(boolean active, int count) {
}
