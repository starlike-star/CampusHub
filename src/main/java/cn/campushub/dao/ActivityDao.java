package cn.campushub.dao;

import cn.campushub.model.Activity;
import cn.campushub.model.ActivityVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义活动数据访问能力及业务层依赖的数据契约。
 */
public interface ActivityDao {
    /**
     * 查询全部活动。
     *
     * @param status 业务状态
     * @param keyword 搜索关键字
     * @param sort 排序方式
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<ActivityVO> findAll(String status, String keyword, String sort)
            throws SQLException;

    /**
     * 根据编号查询活动。
     *
     * @param id 业务数据编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<ActivityVO> findById(long id) throws SQLException;

    /**
     * 创建活动。
     *
     * @param activity 活动数据
     * @return 新建数据的编号
     * @throws SQLException 数据库访问失败时抛出
     */
    long create(Activity activity) throws SQLException;

    /**
     * 更新活动。
     *
     * @param activity 活动数据
     * @param userId 用户编号
     * @param admin 是否具有管理员权限
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean update(Activity activity, long userId, boolean admin)
            throws SQLException;

    /**
     * 更新状态。
     *
     * @param id 业务数据编号
     * @param userId 用户编号
     * @param admin 是否具有管理员权限
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updateStatus(
            long id,
            long userId,
            boolean admin,
            String status
    ) throws SQLException;
}
