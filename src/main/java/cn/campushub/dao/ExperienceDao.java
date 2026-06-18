package cn.campushub.dao;

import cn.campushub.model.ExperienceInfo;
import cn.campushub.model.ExperienceLog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义经验值数据访问能力及业务层依赖的数据契约。
 */
public interface ExperienceDao {
    /**
     * 新增经验值。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @param value 待处理的值
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    ExperienceInfo addExperience(Connection connection, long userId, int value)
            throws SQLException;

    /**
     * 新增`ExperienceLog`。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @param changeValue 参数 `changeValue`
     * @param source 参数 `source`
     * @param description 描述内容
     * @throws SQLException 数据库访问失败时抛出
     */
    void insertExperienceLog(
            Connection connection,
            long userId,
            int changeValue,
            String source,
            String description
    ) throws SQLException;

    /**
     * 获取`RecentLogs`。
     *
     * @param userId 用户编号
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<ExperienceLog> getRecentLogs(long userId, int limit)
            throws SQLException;

    /**
     * 根据输入计算并返回 `reconcileCheckinExperience` 的处理结果。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    int reconcileCheckinExperience(long userId) throws SQLException;

    /**
     * 更新用户等级。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @param level 参数 `level`
     * @throws SQLException 数据库访问失败时抛出
     */
    void updateUserLevel(Connection connection, long userId, int level)
            throws SQLException;

    /**
     * 获取用户经验值信息。
     *
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<ExperienceInfo> getUserExperienceInfo(long userId)
            throws SQLException;

    /**
     * 获取用户经验值信息。
     *
     * @param connection 数据库连接
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<ExperienceInfo> getUserExperienceInfo(
            Connection connection,
            long userId
    ) throws SQLException;
}
