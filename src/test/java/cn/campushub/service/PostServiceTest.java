package cn.campushub.service;

import cn.campushub.dao.PostDao;
import cn.campushub.model.Category;
import cn.campushub.model.Comment;
import cn.campushub.model.CommentCreateResult;
import cn.campushub.model.Post;
import cn.campushub.model.PostToggleResult;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 帖子相关逻辑的正常路径、边界条件和失败场景。
 */
class PostServiceTest {
    /**
     * 验证 `publishUsesSessionUserAndStoresImages` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void publishUsesSessionUserAndStoresImages() throws SQLException {
        FakePostDao dao = new FakePostDao();
        PostService service = new PostService(dao);

        ServiceResult<Long> result =
                service.publish(
                        7L,
                        " 标题 ",
                        " 正文 ",
                        "3",
                        " 校园生活 ",
                        " /uploads/post/example.jpg "
                );

        assertTrue(result.success());
        assertEquals(99L, result.data());
        assertEquals(7L, dao.createdPost.getUserId());
        assertEquals(3L, dao.createdPost.getCategoryId());
        assertEquals("标题", dao.createdPost.getTitle());
        assertEquals("正文", dao.createdPost.getContent());
        assertEquals("校园生活", dao.createdPost.getTopic());
        assertEquals("/uploads/post/example.jpg", dao.createdPost.getImages());
    }

    /**
     * 验证 `publishRejectsInactiveCategory` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void publishRejectsInactiveCategory() throws SQLException {
        FakePostDao dao = new FakePostDao();
        dao.activeCategory = false;
        PostService service = new PostService(dao);

        ServiceResult<Long> result =
                service.publish(7L, "标题", "正文", "3", null, null);

        assertFalse(result.success());
        assertNull(dao.createdPost);
    }

    /**
     * 验证 `commentValidatesContentBeforeWriting` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void commentValidatesContentBeforeWriting() throws SQLException {
        FakePostDao dao = new FakePostDao();
        PostService service = new PostService(dao);

        ServiceResult<CommentCreateResult> emptyResult =
                service.comment(5L, 7L, "   ");
        ServiceResult<CommentCreateResult> successResult =
                service.comment(5L, 7L, " 评论 ");

        assertFalse(emptyResult.success());
        assertTrue(successResult.success());
        assertEquals("评论", dao.commentContent);
    }

    /**
     * 验证 `toggleLikeReturnsCurrentState` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void toggleLikeReturnsCurrentState() throws SQLException {
        FakePostDao dao = new FakePostDao();
        PostService service = new PostService(dao);

        assertTrue(service.toggleLike(5L, 7L).data().active());
        dao.nextLikedState = false;
        assertFalse(service.toggleLike(5L, 7L).data().active());
    }

    /**
     * 验证 `browsingFeedDoesNotIncreaseViewsButOpeningDetailDoes` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
    @Test
    void browsingFeedDoesNotIncreaseViewsButOpeningDetailDoes() throws SQLException {
        FakePostDao dao = new FakePostDao();
        PostService service = new PostService(dao);

        service.listPosts(7L);
        assertEquals(0, dao.viewIncrementCalls);

        service.viewPost(5L);
        assertEquals(1, dao.viewIncrementCalls);
    }

    private static class FakePostDao implements PostDao {
        private boolean activeCategory = true;
        private boolean nextLikedState = true;
        private Post createdPost;
        private String commentContent;
        private int viewIncrementCalls;

        /**
         * 查询`ActivePosts`。
         *
         * @param currentUserId 当前用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<Post> findActivePosts(Long currentUserId) {
            return List.of();
        }

        /**
         * 查询`ActivePostById`。
         *
         * @param postId 帖子编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<Post> findActivePostById(long postId) {
            Post post = new Post();
            post.setId(postId);
            return Optional.of(post);
        }

        /**
         * 查询`ActivePostCategories`。
         *
         * @return 符合条件的数据列表
         */
        @Override
        public List<Category> findActivePostCategories() {
            return List.of();
        }

        /**
         * 判断是否`ActivePostCategory`。
         *
         * @param categoryId 分类编号
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean isActivePostCategory(long categoryId) {
            return activeCategory;
        }

        /**
         * 创建模拟帖子。
         *
         * @param post 帖子数据
         * @return 新建数据的编号
         */
        @Override
        public long create(Post post) {
            createdPost = post;
            return 99L;
        }

        /**
         * 增加`ViewCount`。
         *
         * @param postId 帖子编号
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean incrementViewCount(long postId) {
            viewIncrementCalls++;
            return true;
        }

        /**
         * 查询`ActiveComments`。
         *
         * @param postId 帖子编号
         * @param currentUserId 当前用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<Comment> findActiveComments(
                long postId,
                Long currentUserId
        ) {
            return List.of();
        }

        /**
         * 新增评论。
         *
         * @param postId 帖子编号
         * @param userId 用户编号
         * @param content 正文内容
         * @return 方法处理结果
         */
        @Override
        public CommentCreateResult addComment(long postId, long userId, String content) {
            commentContent = content;
            Comment comment = new Comment();
            comment.setId(1L);
            comment.setContent(content);
            return new CommentCreateResult(comment, 1);
        }

        /**
         * 切换帖子点赞。
         *
         * @param postId 帖子编号
         * @param userId 用户编号
         * @return 方法处理结果
         */
        @Override
        public PostToggleResult togglePostLike(long postId, long userId) {
            return new PostToggleResult(nextLikedState, nextLikedState ? 1 : 0);
        }

        /**
         * 判断是否具有帖子点赞。
         *
         * @param postId 帖子编号
         * @param userId 用户编号
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean hasPostLike(long postId, long userId) {
            return false;
        }

        /**
         * 切换帖子收藏。
         *
         * @param postId 帖子编号
         * @param userId 用户编号
         * @return 方法处理结果
         */
        @Override
        public PostToggleResult togglePostFavorite(long postId, long userId) {
            return new PostToggleResult(true, 1);
        }

        /**
         * 判断是否具有帖子收藏。
         *
         * @param postId 帖子编号
         * @param userId 用户编号
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean hasPostFavorite(long postId, long userId) {
            return false;
        }

        /**
         * 切换评论点赞。
         *
         * @param commentId 评论编号
         * @param userId 用户编号
         * @return 方法处理结果
         */
        @Override
        public PostToggleResult toggleCommentLike(long commentId, long userId) {
            return new PostToggleResult(nextLikedState, nextLikedState ? 1 : 0);
        }

        /**
         * 更新`OwnedPost`。
         *
         * @param post 帖子数据
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<Post> updateOwnedPost(Post post) {
            return Optional.of(post);
        }

        /**
         * 删除`OwnedPost`。
         *
         * @param postId 帖子编号
         * @param userId 用户编号
         * @return 满足条件或操作成功时返回 true，否则返回 false
         */
        @Override
        public boolean deleteOwnedPost(long postId, long userId) {
            return true;
        }
    }
}
