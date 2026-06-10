package cn.campushub.dao;

import java.sql.SQLException;

public interface ReportDao {
    Long findTargetOwnerId(String targetType, long targetId) throws SQLException;

    boolean existsPendingReport(long userId, String targetType, long targetId)
            throws SQLException;

    int createReport(long userId, String targetType, long targetId, String reason)
            throws SQLException;
}
