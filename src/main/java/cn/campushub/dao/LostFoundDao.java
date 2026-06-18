package cn.campushub.dao;

import cn.campushub.model.Category;
import cn.campushub.model.LostFound;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义失物招领数据访问能力及业务层依赖的数据契约。
 */
public interface LostFoundDao {
    /**
     * 查询全部`LostFound`。
     *
     * @param type 参数 `type`
     * @param status 业务状态
     * @param keyword 搜索关键字
     * @param categoryId 分类编号
     * @param sort 排序方式
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<LostFound> findAll(
            String type,
            String status,
            String keyword,
            Long categoryId,
            String sort
    ) throws SQLException;

    /**
     * 根据用户查询`LostFound`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<LostFound> findByUser(long userId) throws SQLException;

    /**
     * 查询`ActiveCategories`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Category> findActiveCategories() throws SQLException;

    /**
     * 判断是否`ActiveCategory`。
     *
     * @param categoryId 分类编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean isActiveCategory(long categoryId) throws SQLException;

    /**
     * 创建`LostFound`。
     *
     * @param lostFound 参数 `lostFound`
     * @return 新建数据的编号
     * @throws SQLException 数据库访问失败时抛出
     */
    long create(LostFound lostFound) throws SQLException;

    /**
     * 根据编号查询`LostFound`。
     *
     * @param id 业务数据编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<LostFound> findById(long id) throws SQLException;

    /**
     * 更新`LostFound`。
     *
     * @param lostFound 参数 `lostFound`
     * @param admin 是否具有管理员权限
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean update(LostFound lostFound, boolean admin) throws SQLException;

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
