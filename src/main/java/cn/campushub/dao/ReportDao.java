package cn.campushub.dao;

import java.sql.SQLException;

/**
 * 定义举报数据访问能力及业务层依赖的数据契约。
 */
public interface ReportDao {
    Long findTargetOwnerId(String targetType, long targetId) throws SQLException;

    boolean existsPendingReport(long userId, String targetType, long targetId)
            throws SQLException;

    int createReport(long userId, String targetType, long targetId, String reason)
            throws SQLException;
}
