package cn.campushub.dao;

import cn.campushub.model.ActivityRegistrationResult;
import cn.campushub.model.ActivityRegistrationVO;
import cn.campushub.model.ProfileActivityVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * 定义活动报名数据访问能力及业务层依赖的数据契约。
 */
public interface ActivityRegistrationDao {
    /**
     * 查询`RegisteredActivityIds`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    Set<Long> findRegisteredActivityIds(long userId) throws SQLException;

    /**
     * 判断是否已报名。
     *
     * @param activityId 活动编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean isRegistered(long activityId, long userId) throws SQLException;

    /**
     * 提交活动报名。
     *
     * @param activityId 活动编号
     * @param userId 用户编号
     * @param userNickname 参数 `userNickname`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    ActivityRegistrationResult register(
            long activityId,
            long userId,
            String userNickname
    ) throws SQLException;

    /**
     * 取消活动报名。
     *
     * @param activityId 活动编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    ActivityRegistrationResult cancel(long activityId, long userId)
            throws SQLException;

    /**
     * 查询报名记录。
     *
     * @param activityId 活动编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<ActivityRegistrationVO> findRegistrations(long activityId)
            throws SQLException;

    /**
     * 根据用户查询活动报名。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<ProfileActivityVO> findByUser(long userId) throws SQLException;
}
