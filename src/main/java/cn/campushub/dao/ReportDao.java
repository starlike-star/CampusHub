package cn.campushub.dao;

import java.sql.SQLException;

/**
 * 定义举报数据访问能力及业务层依赖的数据契约。
 */
public interface ReportDao {
    /**
     * 查询`TargetOwnerId`。
     *
     * @param targetType 参数 `targetType`
     * @param targetId `target`编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Long findTargetOwnerId(String targetType, long targetId) throws SQLException;

    /**
     * 根据输入计算并返回 `existsPendingReport` 的处理结果。
     *
     * @param userId 用户编号
     * @param targetType 参数 `targetType`
     * @param targetId `target`编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean existsPendingReport(long userId, String targetType, long targetId)
            throws SQLException;

    /**
     * 创建举报。
     *
     * @param userId 用户编号
     * @param targetType 参数 `targetType`
     * @param targetId `target`编号
     * @param reason 参数 `reason`
     * @return 新建数据的编号
     * @throws SQLException 数据库访问失败时抛出
     */
    int createReport(long userId, String targetType, long targetId, String reason)
            throws SQLException;
}
