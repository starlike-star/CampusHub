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
    ExperienceInfo addExperience(Connection connection, long userId, int value)
            throws SQLException;

    void insertExperienceLog(
            Connection connection,
            long userId,
            int changeValue,
            String source,
            String description
    ) throws SQLException;

    List<ExperienceLog> getRecentLogs(long userId, int limit)
            throws SQLException;

    int reconcileCheckinExperience(long userId) throws SQLException;

    void updateUserLevel(Connection connection, long userId, int level)
            throws SQLException;

    Optional<ExperienceInfo> getUserExperienceInfo(long userId)
            throws SQLException;

    Optional<ExperienceInfo> getUserExperienceInfo(
            Connection connection,
            long userId
    ) throws SQLException;
}
