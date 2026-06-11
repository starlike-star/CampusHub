package cn.campushub.dao;

import cn.campushub.model.ReportNotificationTarget;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 定义后台管理数据访问能力及业务层依赖的数据契约。
 */
public interface AdminDao {
    Map<String, Long> dashboard() throws SQLException;

    List<Map<String, Object>> findUsers(String keyword, String role, Integer status)
            throws SQLException;

    List<Map<String, Object>> findPosts(String keyword, Integer status, Long categoryId)
            throws SQLException;

    List<Map<String, Object>> findGoods(String keyword, String status, String tradeMethod)
            throws SQLException;

    List<Map<String, Object>> findLostFound(String keyword, String type, String status)
            throws SQLException;

    List<Map<String, Object>> findActivities(String keyword, String status)
            throws SQLException;

    List<Map<String, Object>> findNotices(String type) throws SQLException;

    List<Map<String, Object>> findReports(String status, String targetType)
            throws SQLException;

    List<Map<String, Object>> findCategories(String type) throws SQLException;

    boolean updateUserStatus(long userId, int status) throws SQLException;

    boolean resetUserPassword(long userId, String passwordHash) throws SQLException;

    boolean updatePostStatus(long id, int status) throws SQLException;

    boolean updateGoodsStatus(long id, String status) throws SQLException;

    boolean updateLostFoundStatus(long id, String status) throws SQLException;

    boolean updateActivityStatus(long id, String status) throws SQLException;

    boolean createNotice(
            String title,
            String content,
            String type,
            long createdBy
    ) throws SQLException;

    boolean updateNotice(long id, String title, String content, String type)
            throws SQLException;

    boolean updateNoticeStatus(long id, int status) throws SQLException;

    boolean updateNoticeTop(long id, int isTop) throws SQLException;

    boolean handleReport(long reportId, long adminId) throws SQLException;

    boolean rejectReport(long reportId, long adminId) throws SQLException;

    Optional<ReportNotificationTarget> findReportNotificationTarget(long reportId)
            throws SQLException;
}
