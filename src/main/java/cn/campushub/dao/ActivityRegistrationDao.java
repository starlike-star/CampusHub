package cn.campushub.dao;

import cn.campushub.model.ActivityRegistrationResult;
import cn.campushub.model.ActivityRegistrationVO;
import cn.campushub.model.ProfileActivityVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface ActivityRegistrationDao {
    Set<Long> findRegisteredActivityIds(long userId) throws SQLException;

    boolean isRegistered(long activityId, long userId) throws SQLException;

    ActivityRegistrationResult register(
            long activityId,
            long userId,
            String userNickname
    ) throws SQLException;

    ActivityRegistrationResult cancel(long activityId, long userId)
            throws SQLException;

    List<ActivityRegistrationVO> findRegistrations(long activityId)
            throws SQLException;

    List<ProfileActivityVO> findByUser(long userId) throws SQLException;
}
