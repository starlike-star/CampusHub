package cn.campushub.dao;

import cn.campushub.model.FavoriteItemVO;
import cn.campushub.model.Post;
import cn.campushub.model.ProfileOverviewVO;
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

    UserCheckinStatsVO findCheckins(long userId) throws SQLException;

    Optional<User> updateProfile(User user) throws SQLException;
}
