package cn.campushub.service;

import cn.campushub.dao.ExperienceDao;
import cn.campushub.dao.JdbcExperienceDao;
import cn.campushub.dao.JdbcProfileDao;
import cn.campushub.dao.ProfileDao;
import cn.campushub.model.FavoriteItemVO;
import cn.campushub.model.Post;
import cn.campushub.model.ProfileOverviewVO;
import cn.campushub.model.ProfileActivityVO;
import cn.campushub.model.PurchasedGoodsVO;
import cn.campushub.model.User;
import cn.campushub.model.UserCheckinStatsVO;
import cn.campushub.model.UserCommentVO;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 编排个人主页业务规则、参数校验与数据访问操作。
 */
public class ProfileService {
    private static final Set<String> TABS = Set.of(
            "overview", "posts", "comments", "favorites", "goods",
            "purchasedGoods", "lostfound", "activities", "checkins"
    );

    private final ProfileDao profileDao;
    private final ExperienceDao experienceDao;

    /**
     * 初始化个人资料对象及其运行所需依赖。
     */
    public ProfileService() {
        this(new JdbcProfileDao(), new JdbcExperienceDao());
    }

    ProfileService(ProfileDao profileDao) {
        this(profileDao, null);
    }

    ProfileService(ProfileDao profileDao, ExperienceDao experienceDao) {
        this.profileDao = profileDao;
        this.experienceDao = experienceDao;
    }

    /**
     * 规范化`Tab`。
     *
     * @param tab 参数 `tab`
     * @return 方法处理结果
     */
    public String normalizeTab(String tab) {
        return tab != null && TABS.contains(tab) ? tab : "overview";
    }

    /**
     * 查询`overview`并返回结果。
     *
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public Optional<ProfileOverviewVO> overview(long userId) throws SQLException {
        if (experienceDao != null) {
            experienceDao.reconcileCheckinExperience(userId);
        }
        return profileDao.findOverview(userId);
    }

    /**
     * 查询帖子列表并返回结果。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Post> posts(long userId) throws SQLException {
        return profileDao.findPosts(userId);
    }

    /**
     * 查询`comments`并返回结果。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<UserCommentVO> comments(long userId) throws SQLException {
        return profileDao.findComments(userId);
    }

    /**
     * 查询`favorites`并返回结果。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<FavoriteItemVO> favorites(long userId) throws SQLException {
        return profileDao.findFavorites(userId);
    }

    /**
     * 查询`purchasedGoods`并返回结果。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<PurchasedGoodsVO> purchasedGoods(long userId)
            throws SQLException {
        return profileDao.findPurchasedGoods(userId);
    }

    /**
     * 检查`ins`。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public UserCheckinStatsVO checkins(long userId) throws SQLException {
        return profileDao.findCheckins(userId);
    }

    /**
     * 查询活动列表并返回结果。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<ProfileActivityVO> activities(long userId) throws SQLException {
        return profileDao.findActivities(userId);
    }

    /**
     * 更新个人资料。
     *
     * @param userId 用户编号
     * @param nickname 用户昵称
     * @param avatar 参数 `avatar`
     * @param studentNo 参数 `studentNo`
     * @param college 参数 `college`
     * @param major 参数 `major`
     * @param grade 参数 `grade`
     * @param email 电子邮箱
     * @param phone 参数 `phone`
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
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
        user.setAvatar(avatar == null ? "images/default-user.png" : avatar);
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

    /**
     * 规范化`Optional`。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private String normalizeOptional(String value) {
        return ValidationUtils.trimToNull(value);
    }

    /**
     * 根据输入计算并返回 `withinLimit` 的处理结果。
     *
     * @param value 待处理的值
     * @param limit 查询数量上限
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    private boolean withinLimit(String value, int limit) {
        return value == null || value.length() <= limit;
    }
}
