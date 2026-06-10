package cn.campushub.service;

import cn.campushub.dao.ActivityRegistrationDao;
import cn.campushub.dao.JdbcActivityRegistrationDao;
import cn.campushub.model.ActivityRegistrationResult;
import cn.campushub.model.ActivityRegistrationVO;
import cn.campushub.model.ProfileActivityVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class ActivityRegistrationService {
    private final ActivityRegistrationDao registrationDao;

    public ActivityRegistrationService() {
        this(new JdbcActivityRegistrationDao());
    }

    ActivityRegistrationService(ActivityRegistrationDao registrationDao) {
        this.registrationDao = registrationDao;
    }

    public Set<Long> registeredActivityIds(long userId) throws SQLException {
        return registrationDao.findRegisteredActivityIds(userId);
    }

    public boolean isRegistered(long activityId, long userId)
            throws SQLException {
        return activityId > 0 && registrationDao.isRegistered(activityId, userId);
    }

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

    public List<ActivityRegistrationVO> registrations(long activityId)
            throws SQLException {
        return registrationDao.findRegistrations(activityId);
    }

    public List<ProfileActivityVO> activities(long userId) throws SQLException {
        return registrationDao.findByUser(userId);
    }
}
