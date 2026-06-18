package cn.campushub.service;

import cn.campushub.dao.ActivityRegistrationDao;
import cn.campushub.dao.JdbcActivityRegistrationDao;
import cn.campushub.model.ActivityRegistrationResult;
import cn.campushub.model.ActivityRegistrationVO;
import cn.campushub.model.ProfileActivityVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * 编排活动报名业务规则、参数校验与数据访问操作。
 */
public class ActivityRegistrationService {
    private final ActivityRegistrationDao registrationDao;

    /**
     * 初始化活动报名对象及其运行所需依赖。
     */
    public ActivityRegistrationService() {
        this(new JdbcActivityRegistrationDao());
    }

    ActivityRegistrationService(ActivityRegistrationDao registrationDao) {
        this.registrationDao = registrationDao;
    }

    /**
     * 查询当前用户已报名的活动编号集合。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public Set<Long> registeredActivityIds(long userId) throws SQLException {
        return registrationDao.findRegisteredActivityIds(userId);
    }

    /**
     * 判断是否已报名。
     *
     * @param activityId 活动编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    public boolean isRegistered(long activityId, long userId)
            throws SQLException {
        return activityId > 0 && registrationDao.isRegistered(activityId, userId);
    }

    /**
     * 提交活动报名。
     *
     * @param activityId 活动编号
     * @param userId 用户编号
     * @param nickname 用户昵称
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<ActivityRegistrationResult> register(
            long activityId,
            long userId,
            String nickname
    ) throws SQLException {
        if (activityId <= 0) {
            return ServiceResult.failure("活动参数无效");
        }
        try {
            return ServiceResult.success(
                    "报名成功",
                    registrationDao.register(activityId, userId, nickname)
            );
        } catch (IllegalStateException exception) {
            return ServiceResult.failure(exception.getMessage());
        }
    }

    /**
     * 取消活动报名。
     *
     * @param activityId 活动编号
     * @param userId 用户编号
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<ActivityRegistrationResult> cancel(
            long activityId,
            long userId
    ) throws SQLException {
        if (activityId <= 0) {
            return ServiceResult.failure("活动参数无效");
        }
        try {
            return ServiceResult.success(
                    "已取消报名",
                    registrationDao.cancel(activityId, userId)
            );
        } catch (IllegalStateException exception) {
            return ServiceResult.failure(exception.getMessage());
        }
    }

    /**
     * 查询报名记录并返回结果。
     *
     * @param activityId 活动编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<ActivityRegistrationVO> registrations(long activityId)
            throws SQLException {
        return registrationDao.findRegistrations(activityId);
    }

    /**
     * 查询活动列表并返回结果。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<ProfileActivityVO> activities(long userId) throws SQLException {
        return registrationDao.findByUser(userId);
    }
}
