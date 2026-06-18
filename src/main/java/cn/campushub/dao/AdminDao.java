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
    /**
     * 查询后台概览并返回结果。
     *
     * @return 按键组织的结果数据
     * @throws SQLException 数据库访问失败时抛出
     */
    Map<String, Long> dashboard() throws SQLException;

    /**
     * 查询用户列表。
     *
     * @param keyword 搜索关键字
     * @param role 参数 `role`
     * @param status 业务状态
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Map<String, Object>> findUsers(String keyword, String role, Integer status)
            throws SQLException;

    /**
     * 查询帖子列表。
     *
     * @param keyword 搜索关键字
     * @param status 业务状态
     * @param categoryId 分类编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Map<String, Object>> findPosts(String keyword, Integer status, Long categoryId)
            throws SQLException;

    /**
     * 查询商品。
     *
     * @param keyword 搜索关键字
     * @param status 业务状态
     * @param tradeMethod 参数 `tradeMethod`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Map<String, Object>> findGoods(String keyword, String status, String tradeMethod)
            throws SQLException;

    /**
     * 查询`LostFound`。
     *
     * @param keyword 搜索关键字
     * @param type 参数 `type`
     * @param status 业务状态
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Map<String, Object>> findLostFound(String keyword, String type, String status)
            throws SQLException;

    /**
     * 查询活动列表。
     *
     * @param keyword 搜索关键字
     * @param status 业务状态
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Map<String, Object>> findActivities(String keyword, String status)
            throws SQLException;

    /**
     * 查询公告列表。
     *
     * @param type 参数 `type`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Map<String, Object>> findNotices(String type) throws SQLException;

    /**
     * 查询举报记录。
     *
     * @param status 业务状态
     * @param targetType 参数 `targetType`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Map<String, Object>> findReports(String status, String targetType)
            throws SQLException;

    /**
     * 查询`Categories`。
     *
     * @param type 参数 `type`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Map<String, Object>> findCategories(String type) throws SQLException;

    /**
     * 更新用户状态。
     *
     * @param userId 用户编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updateUserStatus(long userId, int status) throws SQLException;

    /**
     * 重置用户密码。
     *
     * @param userId 用户编号
     * @param passwordHash 参数 `passwordHash`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean resetUserPassword(long userId, String passwordHash) throws SQLException;

    /**
     * 更新帖子状态。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updatePostStatus(long id, int status) throws SQLException;

    /**
     * 更新商品状态。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updateGoodsStatus(long id, String status) throws SQLException;

    /**
     * 更新`LostFoundStatus`。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updateLostFoundStatus(long id, String status) throws SQLException;

    /**
     * 更新活动状态。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updateActivityStatus(long id, String status) throws SQLException;

    /**
     * 创建公告。
     *
     * @param title 标题
     * @param content 正文内容
     * @param type 参数 `type`
     * @param createdBy 参数 `createdBy`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean createNotice(
            String title,
            String content,
            String type,
            long createdBy
    ) throws SQLException;

    /**
     * 更新公告。
     *
     * @param id 业务数据编号
     * @param title 标题
     * @param content 正文内容
     * @param type 参数 `type`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updateNotice(long id, String title, String content, String type)
            throws SQLException;

    /**
     * 更新公告状态。
     *
     * @param id 业务数据编号
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updateNoticeStatus(long id, int status) throws SQLException;

    /**
     * 更新`NoticeTop`。
     *
     * @param id 业务数据编号
     * @param isTop 是否`Top`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updateNoticeTop(long id, int isTop) throws SQLException;

    /**
     * 处理举报。
     *
     * @param reportId 举报编号
     * @param adminId 管理员编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean handleReport(long reportId, long adminId) throws SQLException;

    /**
     * 驳回举报。
     *
     * @param reportId 举报编号
     * @param adminId 管理员编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean rejectReport(long reportId, long adminId) throws SQLException;

    /**
     * 查询`ReportNotificationTarget`。
     *
     * @param reportId 举报编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<ReportNotificationTarget> findReportNotificationTarget(long reportId)
            throws SQLException;
}
