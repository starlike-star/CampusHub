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

public interface ProfileDao {
    Optional<ProfileOverviewVO> findOverview(long userId) throws SQLException;

    List<Post> findPosts(long userId) throws SQLException;

    List<UserCommentVO> findComments(long userId) throws SQLException;

    List<FavoriteItemVO> findFavorites(long userId) throws SQLException;

    default List<PurchasedGoodsVO> findPurchasedGoods(long userId)
            throws SQLException {
        return List.of();
    }

    UserCheckinStatsVO findCheckins(long userId) throws SQLException;

    default List<ProfileActivityVO> findActivities(long userId)
            throws SQLException {
        return List.of();
    }

    Optional<User> updateProfile(User user) throws SQLException;
}
