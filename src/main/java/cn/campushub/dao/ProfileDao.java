package cn.campushub.dao;

import cn.campushub.model.FavoriteItemVO;
import cn.campushub.model.Post;
import cn.campushub.model.ProfileActivityVO;
import cn.campushub.model.ProfileOverviewVO;
import cn.campushub.model.PurchasedGoodsVO;
import cn.campushub.model.User;
import cn.campushub.model.UserCheckinStatsVO;
import cn.campushub.model.UserCommentVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义个人主页数据访问能力及业务层依赖的数据契约。
 */
public interface ProfileDao {
    /**
     * 查询`Overview`。
     *
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<ProfileOverviewVO> findOverview(long userId) throws SQLException;

    /**
     * 查询帖子列表。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Post> findPosts(long userId) throws SQLException;

    /**
     * 查询`Comments`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<UserCommentVO> findComments(long userId) throws SQLException;

    /**
     * 查询`Favorites`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<FavoriteItemVO> findFavorites(long userId) throws SQLException;

    /**
     * 查询`PurchasedGoods`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    default List<PurchasedGoodsVO> findPurchasedGoods(long userId)
            throws SQLException {
        return List.of();
    }

    /**
     * 查询`Checkins`。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    UserCheckinStatsVO findCheckins(long userId) throws SQLException;

    /**
     * 查询活动列表。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    default List<ProfileActivityVO> findActivities(long userId)
            throws SQLException {
        return List.of();
    }

    /**
     * 更新个人资料。
     *
     * @param user 用户数据
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<User> updateProfile(User user) throws SQLException;
}
