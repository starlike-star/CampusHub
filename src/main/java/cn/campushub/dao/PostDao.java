package cn.campushub.dao;

import cn.campushub.model.Category;
import cn.campushub.model.Comment;
import cn.campushub.model.CommentCreateResult;
import cn.campushub.model.Post;
import cn.campushub.model.PostToggleResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PostDao {
    List<Post> findActivePosts(Long currentUserId) throws SQLException;

    Optional<Post> findActivePostById(long postId) throws SQLException;

    List<Category> findActivePostCategories() throws SQLException;

    boolean isActivePostCategory(long categoryId) throws SQLException;

    long create(Post post) throws SQLException;

    boolean incrementViewCount(long postId) throws SQLException;

    List<Comment> findActiveComments(long postId, Long currentUserId)
            throws SQLException;

    CommentCreateResult addComment(long postId, long userId, String content)
            throws SQLException;

    PostToggleResult togglePostLike(long postId, long userId) throws SQLException;

    boolean hasPostLike(long postId, long userId) throws SQLException;

    PostToggleResult togglePostFavorite(long postId, long userId) throws SQLException;

    boolean hasPostFavorite(long postId, long userId) throws SQLException;

    PostToggleResult toggleCommentLike(long commentId, long userId)
            throws SQLException;

    Optional<Post> updateOwnedPost(Post post) throws SQLException;

    boolean deleteOwnedPost(long postId, long userId) throws SQLException;
}
