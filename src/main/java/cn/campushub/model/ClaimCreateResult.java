package cn.campushub.model;

/**
 * 封装ClaimCreate操作的处理结果与返回数据。
 */
public record ClaimCreateResult(
        boolean created,
        long ownerId,
        String lostFoundTitle
) {
}
