package cn.campushub.service;

import cn.campushub.dao.JdbcProfileDao;
import cn.campushub.dao.ProfileDao;
import cn.campushub.model.FavoriteItemVO;
import cn.campushub.model.Post;
import cn.campushub.model.ProfileOverviewVO;
import cn.campushub.model.User;
import cn.campushub.model.UserCheckinStatsVO;
import cn.campushub.model.UserCommentVO;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProfileService {
    private static final Set<String> TABS = Set.of(
            "overview", "posts", "comments", "favorites", "goods",
            "lostfound", "checkins"
    );

    private final ProfileDao profileDao;

    public ProfileService() {
        this(new JdbcProfileDao());
    }

    ProfileService(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public String normalizeTab(String tab) {
        return tab != null && TABS.contains(tab) ? tab : "overview";
    }

    public Optional<ProfileOverviewVO> overview(long userId) throws SQLException {
        return profileDao.findOverview(userId);
    }

    public List<Post> posts(long userId) throws SQLException {
        return profileDao.findPosts(userId);
    }

    public List<UserCommentVO> comments(long userId) throws SQLException {
        return profileDao.findComments(userId);
    }

    public List<FavoriteItemVO> favorites(long userId) throws SQLException {
        return profileDao.findFavorites(userId);
    }

    public UserCheckinStatsVO checkins(long userId) throws SQLException {
        return profileDao.findCheckins(userId);
    }

    public ServiceResult<User> update(
            long userId,
            String nickname,
            String avatar,
            String studentNo,
            String college,
            String major,
            String grade,
            String email,
            String phone
    ) throws SQLException {
        nickname = ValidationUtils.trimToNull(nickname);
        avatar = normalizeOptional(avatar);
        studentNo = normalizeOptional(studentNo);
        college = normalizeOptional(college);
        major = normalizeOptional(major);
        grade = normalizeOptional(grade);
        email = normalizeOptional(email);
        phone = normalizeOptional(phone);

        if (nickname == null || nickname.length() > 50) {
            return ServiceResult.failure("昵称不能为空且不能超过 50 个字符");
        }
        if (email != null && !ValidationUtils.isValidEmail(email)) {
            return ServiceResult.failure("请输入有效的邮箱地址");
        }
        if (phone != null && phone.length() > 30) {
            return ServiceResult.failure("手机号不能超过 30 个字符");
        }
        if (!withinLimit(avatar, 255)
                || !withinLimit(studentNo, 50)
                || !withinLimit(college, 100)
                || !withinLimit(major, 100)
                || !withinLimit(grade, 50)) {
            return ServiceResult.failure("资料内容超过字段长度限制");
        }

        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        user.setAvatar(avatar == null ? "images/default-avatar.png" : avatar);
        user.setStudentNo(studentNo);
        user.setCollege(college);
        user.setMajor(major);
        user.setGrade(grade);
        user.setEmail(email);
        user.setPhone(phone);
        return profileDao.updateProfile(user)
                .map(value -> ServiceResult.success("资料更新成功", value))
                .orElseGet(() -> ServiceResult.failure("用户不存在或账号不可用"));
    }

    private String normalizeOptional(String value) {
        return ValidationUtils.trimToNull(value);
    }

    private boolean withinLimit(String value, int limit) {
        return value == null || value.length() <= limit;
    }
}
