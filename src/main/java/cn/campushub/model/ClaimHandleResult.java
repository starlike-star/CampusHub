package cn.campushub.model;

/**
 * 封装ClaimHandle操作的处理结果与返回数据。
 */
public record ClaimHandleResult(
        long applicantId,
        String lostFoundTitle,
        boolean approved
) {
}
