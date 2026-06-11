package cn.campushub.model;

/**
 * 封装活动报名操作的处理结果与返回数据。
 */
public record ActivityRegistrationResult(
        boolean registered,
        int currentMembers
) {
}
