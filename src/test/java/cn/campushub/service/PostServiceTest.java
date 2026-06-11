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

    @Test
    void toggleLikeReturnsCurrentState() throws SQLException {
        FakePostDao dao = new FakePostDao();
        PostService service = new PostService(dao);

        assertTrue(service.toggleLike(5L, 7L).data().active());
        dao.nextLikedState = false;
        assertFalse(service.toggleLike(5L, 7L).data().active());
    }

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

        @Override
        public List<Post> findActivePosts(Long currentUserId) {
            return List.of();
        }

        @Override
        public Optional<Post> findActivePostById(long postId) {
            Post post = new Post();
            post.setId(postId);
            return Optional.of(post);
        }

        @Override
        public List<Category> findActivePostCategories() {
            return List.of();
        }

        @Override
        public boolean isActivePostCategory(long categoryId) {
            return activeCategory;
        }

        @Override
        public long create(Post post) {
            createdPost = post;
            return 99L;
        }

        @Override
        public boolean incrementViewCount(long postId) {
            viewIncrementCalls++;
            return true;
        }

        @Override
        public List<Comment> findActiveComments(
                long postId,
                Long currentUserId
        ) {
            return List.of();
        }

        @Override
        public CommentCreateResult addComment(long postId, long userId, String content) {
            commentContent = content;
            Comment comment = new Comment();
            comment.setId(1L);
            comment.setContent(content);
            return new CommentCreateResult(comment, 1);
        }

        @Override
        public PostToggleResult togglePostLike(long postId, long userId) {
            return new PostToggleResult(nextLikedState, nextLikedState ? 1 : 0);
        }

        @Override
        public boolean hasPostLike(long postId, long userId) {
            return false;
        }

        @Override
        public PostToggleResult togglePostFavorite(long postId, long userId) {
            return new PostToggleResult(true, 1);
        }

        @Override
        public boolean hasPostFavorite(long postId, long userId) {
            return false;
        }

        @Override
        public PostToggleResult toggleCommentLike(long commentId, long userId) {
            return new PostToggleResult(nextLikedState, nextLikedState ? 1 : 0);
        }

        @Override
        public Optional<Post> updateOwnedPost(Post post) {
            return Optional.of(post);
        }

        @Override
        public boolean deleteOwnedPost(long postId, long userId) {
            return true;
        }
    }
}
