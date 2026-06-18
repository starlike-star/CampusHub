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
    /**
     * 验证 `invalidTabFallsBackToOverview` 场景下的业务行为与预期结果一致。
     */
    @Test
    void invalidTabFallsBackToOverview() {
        ProfileService service = new ProfileService(new FakeProfileDao());

        assertEquals("overview", service.normalizeTab(null));
        assertEquals("overview", service.normalizeTab("unknown"));
        assertEquals("favorites", service.normalizeTab("favorites"));
    }

    /**
     * 验证 `updateNormalizesOptionalFieldsAndUsesCurrentUserId` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 验证 `updateRejectsInvalidEmailAndLongPhone` 场景下的业务行为与预期结果一致。
     *
     * @throws SQLException 数据库访问失败时抛出
     */
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

        /**
         * 查询`Overview`。
         *
         * @param userId 用户编号
         * @return 查询到的数据；不存在时返回空结果
         */
        @Override
        public Optional<ProfileOverviewVO> findOverview(long userId) {
            return Optional.empty();
        }

        /**
         * 查询帖子列表。
         *
         * @param userId 用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<Post> findPosts(long userId) {
            return List.of();
        }

        /**
         * 查询`Comments`。
         *
         * @param userId 用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<UserCommentVO> findComments(long userId) {
            return List.of();
        }

        /**
         * 查询`Favorites`。
         *
         * @param userId 用户编号
         * @return 符合条件的数据列表
         */
        @Override
        public List<FavoriteItemVO> findFavorites(long userId) {
            return List.of();
        }

        /**
         * 查询`Checkins`。
         *
         * @param userId 用户编号
         * @return 方法处理结果
         */
        @Override
        public UserCheckinStatsVO findCheckins(long userId) {
            return new UserCheckinStatsVO(0, 0, 0, List.of());
        }

        /**
         * 更新个人资料。
         *
         * @param user 用户数据
         * @return 查询到的数据；不存在时返回空结果
         */
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
