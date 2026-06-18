package cn.campushub.dao;

import cn.campushub.model.Category;
import cn.campushub.model.Goods;
import cn.campushub.model.PostToggleResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义商品数据访问能力及业务层依赖的数据契约。
 */
public interface GoodsDao {
    /**
     * 查询商品。
     *
     * @param currentUserId 当前用户编号
     * @param keyword 搜索关键字
     * @param categoryId 分类编号
     * @param status 业务状态
     * @param tradeMethod 参数 `tradeMethod`
     * @param sort 排序方式
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Goods> findGoods(
            Long currentUserId,
            String keyword,
            Long categoryId,
            String status,
            String tradeMethod,
            String sort
    ) throws SQLException;

    /**
     * 查询`OwnGoods`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Goods> findOwnGoods(long userId) throws SQLException;

    /**
     * 查询收藏商品。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Goods> findFavoriteGoods(long userId) throws SQLException;

    /**
     * 查询`ActiveGoodsCategories`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Category> findActiveGoodsCategories() throws SQLException;

    /**
     * 判断是否`ActiveGoodsCategory`。
     *
     * @param categoryId 分类编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean isActiveGoodsCategory(long categoryId) throws SQLException;

    /**
     * 创建商品。
     *
     * @param goods 商品数据
     * @return 新建数据的编号
     * @throws SQLException 数据库访问失败时抛出
     */
    long create(Goods goods) throws SQLException;

    /**
     * 查询`VisibleById`。
     *
     * @param goodsId 商品编号
     * @param currentUserId 当前用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<Goods> findVisibleById(long goodsId, Long currentUserId)
            throws SQLException;

    /**
     * 切换收藏。
     *
     * @param goodsId 商品编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    PostToggleResult toggleFavorite(long goodsId, long userId)
            throws SQLException;

    /**
     * 更新商品。
     *
     * @param goods 商品数据
     * @param admin 是否具有管理员权限
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<Goods> update(Goods goods, boolean admin) throws SQLException;

    /**
     * 更新状态。
     *
     * @param goodsId 商品编号
     * @param userId 用户编号
     * @param admin 是否具有管理员权限
     * @param status 业务状态
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean updateStatus(
            long goodsId,
            long userId,
            boolean admin,
            String status
    ) throws SQLException;
}
