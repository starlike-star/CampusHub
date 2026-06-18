package cn.campushub.service;

import cn.campushub.dao.JdbcPostDao;
import cn.campushub.dao.PostDao;
import cn.campushub.model.Category;
import cn.campushub.model.Comment;
import cn.campushub.model.CommentCreateResult;
import cn.campushub.model.Post;
import cn.campushub.model.PostToggleResult;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 编排帖子业务规则、参数校验与数据访问操作。
 */
public class PostService {
    private final PostDao postDao;

    /**
     * 初始化帖子对象及其运行所需依赖。
     */
    public PostService() {
        this(new JdbcPostDao());
    }

    PostService(PostDao postDao) {
        this.postDao = postDao;
    }

    /**
     * 查询帖子列表。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Post> listPosts() throws SQLException {
        return listPosts(null);
    }

    /**
     * 查询帖子列表。
     *
     * @param currentUserId 当前用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Post> listPosts(Long currentUserId) throws SQLException {
        return postDao.findActivePosts(currentUserId);
    }

    /**
     * 查询`Categories`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Category> listCategories() throws SQLException {
        return postDao.findActivePostCategories();
    }

    /**
     * 查询`viewPost`并返回结果。
     *
     * @param postId 帖子编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public Optional<Post> viewPost(long postId) throws SQLException {
        if (postId <= 0 || !postDao.incrementViewCount(postId)) {
            return Optional.empty();
        }
        return postDao.findActivePostById(postId);
    }

    /**
     * 查询`Comments`。
     *
     * @param postId 帖子编号
     * @param currentUserId 当前用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Comment> listComments(long postId, Long currentUserId)
            throws SQLException {
        return postDao.findActiveComments(postId, currentUserId);
    }

    /**
     * 判断是否`Liked`。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    public boolean isLiked(long postId, long userId) throws SQLException {
        return postDao.hasPostLike(postId, userId);
    }

    /**
     * 判断是否`Favorited`。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    public boolean isFavorited(long postId, long userId) throws SQLException {
        return postDao.hasPostFavorite(postId, userId);
    }

    /**
     * 根据输入计算并返回 `publish` 的处理结果。
     *
     * @param userId 用户编号
     * @param title 标题
     * @param content 正文内容
     * @param categoryIdValue 参数 `categoryIdValue`
     * @param topic 参数 `topic`
     * @param images 参数 `images`
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<Long> publish(
            long userId,
            String title,
            String content,
            String categoryIdValue,
            String topic,
            String images
    ) throws SQLException {
        title = ValidationUtils.trimToNull(title);
        content = ValidationUtils.trimToNull(content);
        topic = ValidationUtils.trimToNull(topic);
        images = ValidationUtils.trimToNull(images);

        if (title == null || title.length() > 150) {
            return ServiceResult.failure("标题不能为空且不能超过 150 个字符");
        }
        if (content == null) {
            return ServiceResult.failure("帖子内容不能为空");
        }
        if (topic != null && topic.length() > 100) {
            return ServiceResult.failure("话题不能超过 100 个字符");
        }

        Long categoryId = parsePositiveLong(categoryIdValue);
        if (categoryId == null || !postDao.isActivePostCategory(categoryId)) {
            return ServiceResult.failure("请选择有效的帖子分类");
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setCategoryId(categoryId);
        post.setTitle(title);
        post.setContent(content);
        post.setImages(images);
        post.setTopic(topic);
        long postId = postDao.create(post);
        return ServiceResult.success("发布成功", postId);
    }

    /**
     * 根据输入计算并返回 `comment` 的处理结果。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @param content 正文内容
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<CommentCreateResult> comment(
            long postId,
            long userId,
            String content
    )
            throws SQLException {
        content = ValidationUtils.trimToNull(content);
        if (postId <= 0) {
            return ServiceResult.failure("帖子参数无效");
        }
        if (content == null) {
            return ServiceResult.failure("评论内容不能为空");
        }
        if (content.length() > 2000) {
            return ServiceResult.failure("评论内容不能超过 2000 个字符");
        }
        CommentCreateResult result = postDao.addComment(postId, userId, content);
        if (result == null) {
            return ServiceResult.failure("帖子不存在或不可评论");
        }
        return ServiceResult.success("评论成功", result);
    }

    /**
     * 切换点赞。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<PostToggleResult> toggleLike(long postId, long userId)
            throws SQLException {
        if (postId <= 0) {
            return ServiceResult.failure("帖子参数无效");
        }
        PostToggleResult result = postDao.togglePostLike(postId, userId);
        return ServiceResult.success(
                result.active() ? "点赞成功" : "已取消点赞",
                result
        );
    }

    /**
     * 切换收藏。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<PostToggleResult> toggleFavorite(long postId, long userId)
            throws SQLException {
        if (postId <= 0) {
            return ServiceResult.failure("帖子参数无效");
        }
        PostToggleResult result = postDao.togglePostFavorite(postId, userId);
        return ServiceResult.success(
                result.active() ? "收藏成功" : "已取消收藏",
                result
        );
    }

    /**
     * 切换评论点赞。
     *
     * @param commentId 评论编号
     * @param userId 用户编号
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<PostToggleResult> toggleCommentLike(
            long commentId,
            long userId
    ) throws SQLException {
        if (commentId <= 0) {
            return ServiceResult.failure("评论参数无效");
        }
        PostToggleResult result = postDao.toggleCommentLike(commentId, userId);
        return ServiceResult.success(
                result.active() ? "点赞成功" : "已取消点赞",
                result
        );
    }

    /**
     * 更新帖子。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @param title 标题
     * @param content 正文内容
     * @param topic 参数 `topic`
     * @param categoryIdValue 参数 `categoryIdValue`
     * @param images 参数 `images`
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<Post> update(
            long postId,
            long userId,
            String title,
            String content,
            String topic,
            String categoryIdValue,
            String images
    ) throws SQLException {
        title = ValidationUtils.trimToNull(title);
        content = ValidationUtils.trimToNull(content);
        topic = normalizeTopic(topic);
        images = ValidationUtils.trimToNull(images);
        ServiceResult<Void> validation =
                validatePostFields(title, content, topic, categoryIdValue);
        if (!validation.success()) {
            return ServiceResult.failure(validation.message());
        }

        long categoryId = Long.parseLong(categoryIdValue);
        Post post = new Post();
        post.setId(postId);
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setTopic(topic);
        post.setCategoryId(categoryId);
        post.setImages(images);
        Optional<Post> updated = postDao.updateOwnedPost(post);
        return updated
                .map(value -> ServiceResult.success("更新成功", value))
                .orElseGet(() -> ServiceResult.failure("帖子不存在或无权编辑"));
    }

    /**
     * 删除帖子。
     *
     * @param postId 帖子编号
     * @param userId 用户编号
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<Void> delete(long postId, long userId) throws SQLException {
        if (postId <= 0) {
            return ServiceResult.failure("帖子参数无效");
        }
        if (!postDao.deleteOwnedPost(postId, userId)) {
            return ServiceResult.failure("帖子不存在或无权删除");
        }
        return ServiceResult.success("删除成功", null);
    }

    /**
     * 校验`PostFields`。
     *
     * @param title 标题
     * @param content 正文内容
     * @param topic 参数 `topic`
     * @param categoryIdValue 参数 `categoryIdValue`
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    private ServiceResult<Void> validatePostFields(
            String title,
            String content,
            String topic,
            String categoryIdValue
    ) throws SQLException {
        if (title == null || title.length() > 150) {
            return ServiceResult.failure("标题不能为空且不能超过 150 个字符");
        }
        if (content == null) {
            return ServiceResult.failure("帖子内容不能为空");
        }
        if (topic != null && topic.length() > 100) {
            return ServiceResult.failure("话题不能超过 100 个字符");
        }
        Long categoryId = parsePositiveLong(categoryIdValue);
        if (categoryId == null || !postDao.isActivePostCategory(categoryId)) {
            return ServiceResult.failure("请选择有效的帖子分类");
        }
        return ServiceResult.success("验证通过", null);
    }

    /**
     * 规范化`Topic`。
     *
     * @param topic 参数 `topic`
     * @return 方法处理结果
     */
    private String normalizeTopic(String topic) {
        topic = ValidationUtils.trimToNull(topic);
        if (topic != null && topic.startsWith("#")) {
            topic = ValidationUtils.trimToNull(topic.substring(1));
        }
        return topic;
    }

    /**
     * 解析`PositiveLong`。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    private Long parsePositiveLong(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
