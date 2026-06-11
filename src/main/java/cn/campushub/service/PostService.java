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

    public PostService() {
        this(new JdbcPostDao());
    }

    PostService(PostDao postDao) {
        this.postDao = postDao;
    }

    public List<Post> listPosts() throws SQLException {
        return listPosts(null);
    }

    public List<Post> listPosts(Long currentUserId) throws SQLException {
        return postDao.findActivePosts(currentUserId);
    }

    public List<Category> listCategories() throws SQLException {
        return postDao.findActivePostCategories();
    }

    public Optional<Post> viewPost(long postId) throws SQLException {
        if (postId <= 0 || !postDao.incrementViewCount(postId)) {
            return Optional.empty();
        }
        return postDao.findActivePostById(postId);
    }

    public List<Comment> listComments(long postId, Long currentUserId)
            throws SQLException {
        return postDao.findActiveComments(postId, currentUserId);
    }

    public boolean isLiked(long postId, long userId) throws SQLException {
        return postDao.hasPostLike(postId, userId);
    }

    public boolean isFavorited(long postId, long userId) throws SQLException {
        return postDao.hasPostFavorite(postId, userId);
    }

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

    public ServiceResult<Void> delete(long postId, long userId) throws SQLException {
        if (postId <= 0) {
            return ServiceResult.failure("帖子参数无效");
        }
        if (!postDao.deleteOwnedPost(postId, userId)) {
            return ServiceResult.failure("帖子不存在或无权删除");
        }
        return ServiceResult.success("删除成功", null);
    }

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

    private String normalizeTopic(String topic) {
        topic = ValidationUtils.trimToNull(topic);
        if (topic != null && topic.startsWith("#")) {
            topic = ValidationUtils.trimToNull(topic.substring(1));
        }
        return topic;
    }

    private Long parsePositiveLong(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
