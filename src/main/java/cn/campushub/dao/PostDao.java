package cn.campushub.dao;

import cn.campushub.model.Category;
import cn.campushub.model.Comment;
import cn.campushub.model.CommentCreateResult;
import cn.campushub.model.Post;
import cn.campushub.model.PostToggleResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义帖子数据访问能力及业务层依赖的数据契约。
 */
public interface PostDao {
    /**
     * 查询`ActivePosts`。
     *
     * @param currentUserId 当前用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Post> findActivePosts(Long currentUserId) throws SQLException;

    /**
     * 查询`ActivePostById`。
     *
     * @param postId 帖子编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<Post> findActivePostById(long postId) throws SQLException;

    /**
     * 查询`ActivePostCategories`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Category> findActivePostCategories() throws SQLException;

    /**
     * 判断是否`ActivePostCategory`。
     *
     * @param categoryId 分类编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean isActivePostCategory(long categoryId) throws SQLException;

    /**
     * 创建帖子。
     *
     * @param post 帖子数据
     * @return 新建数据的编号
     * @throws SQLException 数据库访问失败时抛出
     */
    long create(Post post) throws SQLException;

    /**
     * 增加`ViewCount`。
     *
     * @param postId 帖子编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean incrementViewCount(long postId) throws SQLException;

    /**
     * 查询`ActiveComments`。
     *
     * @param postId 帖子编号
     * @param currentUserId 当前用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Comment> findActiveComments(long postId, Long currentUserId)
            throws SQLException;

    /**
     * 新增评论。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @param content 正文内容
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    CommentCreateResult addComment(long postId, long userId, String content)
            throws SQLException;

    /**
     * 切换帖子点赞。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    PostToggleResult togglePostLike(long postId, long userId) throws SQLException;

    /**
     * 判断是否具有帖子点赞。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean hasPostLike(long postId, long userId) throws SQLException;

    /**
     * 切换帖子收藏。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    PostToggleResult togglePostFavorite(long postId, long userId) throws SQLException;

    /**
     * 判断是否具有帖子收藏。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean hasPostFavorite(long postId, long userId) throws SQLException;

    /**
     * 切换评论点赞。
     *
     * @param commentId 评论编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    PostToggleResult toggleCommentLike(long commentId, long userId)
            throws SQLException;

    /**
     * 更新`OwnedPost`。
     *
     * @param post 帖子数据
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<Post> updateOwnedPost(Post post) throws SQLException;

    /**
     * 删除`OwnedPost`。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean deleteOwnedPost(long postId, long userId) throws SQLException;
}
