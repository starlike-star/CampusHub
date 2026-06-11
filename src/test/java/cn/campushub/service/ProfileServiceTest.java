package cn.campushub.service;

import cn.campushub.dao.ProfileDao;
import cn.campushub.model.FavoriteItemVO;
import cn.campushub.model.Post;
import cn.campushub.model.ProfileOverviewVO;
import cn.campushub.model.User;
import cn.campushub.model.UserCheckinStatsVO;
import cn.campushub.model.UserCommentVO;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 个人主页相关逻辑的正常路径、边界条件和失败场景。
 */
class ProfileServiceTest {
    @Test
    void invalidTabFallsBackToOverview() {
        ProfileService service = new ProfileService(new FakeProfileDao());

        assertEquals("overview", service.normalizeTab(null));
        assertEquals("overview", service.normalizeTab("unknown"));
        assertEquals("favorites", service.normalizeTab("favorites"));
    }

    @Test
    void updateNormalizesOptionalFieldsAndUsesCurrentUserId() throws SQLException {
        FakeProfileDao dao = new FakeProfileDao();
        ProfileService service = new ProfileService(dao);

        ServiceResult<User> result = service.update(
                9L,
                "  Campus User  ",
                " ",
                " 20260001 ",
                " 计算机学院 ",
                " 软件工程 ",
                " 2026 ",
                " campus@example.com ",
                " 13800138000 "
        );

        assertTrue(result.success());
        assertEquals(9L, dao.updated.getId());
        assertEquals("Campus User", dao.updated.getNickname());
        assertEquals("images/default-user.png", dao.updated.getAvatar());
        assertEquals("20260001", dao.updated.getStudentNo());
        assertEquals("campus@example.com", dao.updated.getEmail());
    }

    @Test
    void updateRejectsInvalidEmailAndLongPhone() throws SQLException {
        ProfileService service = new ProfileService(new FakeProfileDao());

        assertFalse(service.update(
                1L, "用户", null, null, null, null, null,
                "invalid", null
        ).success());
        assertFalse(service.update(
                1L, "用户", null, null, null, null, null,
                null, "1".repeat(31)
        ).success());
    }

    private static final class FakeProfileDao implements ProfileDao {
        private User updated;

        @Override
        public Optional<ProfileOverviewVO> findOverview(long userId) {
            return Optional.empty();
        }

        @Override
        public List<Post> findPosts(long userId) {
            return List.of();
        }

        @Override
        public List<UserCommentVO> findComments(long userId) {
            return List.of();
        }

        @Override
        public List<FavoriteItemVO> findFavorites(long userId) {
            return List.of();
        }

        @Override
        public UserCheckinStatsVO findCheckins(long userId) {
            return new UserCheckinStatsVO(0, 0, 0, List.of());
        }

        @Override
        public Optional<User> updateProfile(User user) {
            updated = user;
            user.setUsername("campus");
            user.setRole("student");
            user.setStatus(1);
            return Optional.of(user);
        }
    }
}
