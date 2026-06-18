package cn.campushub.service;

import cn.campushub.dao.ActivityDao;
import cn.campushub.dao.JdbcActivityDao;
import cn.campushub.model.Activity;
import cn.campushub.model.ActivityVO;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 编排活动业务规则、参数校验与数据访问操作。
 */
public class ActivityService {
    private static final Set<String> STATUSES =
            Set.of("signup", "closed", "ongoing", "finished");
    private static final Set<String> SORTS = Set.of("latest", "hot", "soon");

    private final ActivityDao activityDao;

    /**
     * 初始化活动对象及其运行所需依赖。
     */
    public ActivityService() {
        this(new JdbcActivityDao());
    }

    ActivityService(ActivityDao activityDao) {
        this.activityDao = activityDao;
    }

    /**
     * 查询活动。
     *
     * @param status 业务状态
     * @param keyword 搜索关键字
     * @param sort 排序方式
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<ActivityVO> list(String status, String keyword, String sort)
            throws SQLException {
        return activityDao.findAll(
                normalizeStatusFilter(status),
                normalizeKeyword(keyword),
                normalizeSort(sort)
        );
    }

    /**
     * 查询活动详情。
     *
     * @param id 业务数据编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public Optional<ActivityVO> detail(long id) throws SQLException {
        return id > 0 ? activityDao.findById(id) : Optional.empty();
    }

    /**
     * 创建活动。
     *
     * @param userId 用户编号
     * @param title 标题
     * @param content 正文内容
     * @param coverImage 封面图片地址
     * @param location 参数 `location`
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param deadline 截止时间
     * @param maxMembers 人数上限
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<Long> create(
            long userId,
            String title,
            String content,
            String coverImage,
            String location,
            String startTime,
            String endTime,
            String deadline,
            String maxMembers
    ) throws SQLException {
        Activity activity = new Activity();
        activity.setCreatedBy(userId);
        ServiceResult<Void> validation = populateAndValidate(
                activity,
                title,
                content,
                coverImage,
                location,
                startTime,
                endTime,
                deadline,
                maxMembers
        );
        if (!validation.success()) {
            return ServiceResult.failure(validation.message());
        }
        return ServiceResult.success("活动发布成功", activityDao.create(activity));
    }

    /**
     * 更新活动。
     *
     * @param id 业务数据编号
     * @param userId 用户编号
     * @param admin 是否具有管理员权限
     * @param title 标题
     * @param content 正文内容
     * @param coverImage 封面图片地址
     * @param location 参数 `location`
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param deadline 截止时间
     * @param maxMembers 人数上限
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<Activity> update(
            long id,
            long userId,
            boolean admin,
            String title,
            String content,
            String coverImage,
            String location,
            String startTime,
            String endTime,
            String deadline,
            String maxMembers
    ) throws SQLException {
        if (id <= 0) {
            return ServiceResult.failure("活动参数无效");
        }
        Activity activity = new Activity();
        activity.setId(id);
        ServiceResult<Void> validation = populateAndValidate(
                activity,
                title,
                content,
                coverImage,
                location,
                startTime,
                endTime,
                deadline,
                maxMembers
        );
        if (!validation.success()) {
            return ServiceResult.failure(validation.message());
        }
        Optional<ActivityVO> existing = activityDao.findById(id);
        if (existing.isEmpty()) {
            return ServiceResult.failure("活动不存在");
        }
        if (activity.getMaxMembers()
                < existing.get().activity().getCurrentMembers()) {
            return ServiceResult.failure("人数上限不能小于当前报名人数");
        }
        if (!activityDao.update(activity, userId, admin)) {
            return ServiceResult.failure("活动不存在或无权编辑");
        }
        return ServiceResult.success("活动已更新", activity);
    }

    /**
     * 更新状态。
     *
     * @param id 业务数据编号
     * @param userId 用户编号
     * @param admin 是否具有管理员权限
     * @param status 业务状态
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public ServiceResult<Void> updateStatus(
            long id,
            long userId,
            boolean admin,
            String status
    ) throws SQLException {
        if (id <= 0 || !STATUSES.contains(status)) {
            return ServiceResult.failure("活动状态参数无效");
        }
        if (!activityDao.updateStatus(id, userId, admin, status)) {
            return ServiceResult.failure("活动不存在或无权修改");
        }
        return ServiceResult.success("状态已更新", null);
    }

    /**
     * 规范化状态值。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    public String normalizeStatusValue(String value) {
        return value != null && STATUSES.contains(value) ? value : "all";
    }

    /**
     * 规范化排序方式。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    public String normalizeSort(String value) {
        return value != null && SORTS.contains(value) ? value : "latest";
    }

    /**
     * 规范化关键字。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    public String normalizeKeyword(String value) {
        value = ValidationUtils.trimToNull(value);
        return value != null && value.length() > 100
                ? value.substring(0, 100)
                : value;
    }

    /**
     * 规范化`StatusFilter`。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private String normalizeStatusFilter(String value) {
        return value != null && STATUSES.contains(value) ? value : null;
    }

    /**
     * 填充并校验活动数据。
     *
     * @param activity 活动数据
     * @param title 标题
     * @param content 正文内容
     * @param coverImage 封面图片地址
     * @param location 参数 `location`
     * @param startTimeValue 参数 `startTimeValue`
     * @param endTimeValue 参数 `endTimeValue`
     * @param deadlineValue 参数 `deadlineValue`
     * @param maxMembersValue 参数 `maxMembersValue`
     * @return 包含处理状态、提示信息和业务数据的结果
     */
    private ServiceResult<Void> populateAndValidate(
            Activity activity,
            String title,
            String content,
            String coverImage,
            String location,
            String startTimeValue,
            String endTimeValue,
            String deadlineValue,
            String maxMembersValue
    ) {
        title = ValidationUtils.trimToNull(title);
        content = ValidationUtils.trimToNull(content);
        coverImage = ValidationUtils.trimToNull(coverImage);
        location = ValidationUtils.trimToNull(location);
        LocalDateTime startTime = parseDateTime(startTimeValue);
        LocalDateTime endTime = parseDateTime(endTimeValue);
        LocalDateTime deadline = parseDateTime(deadlineValue);
        Integer maxMembers = parseNonNegativeInt(maxMembersValue);

        if (title == null || title.length() > 150) {
            return ServiceResult.failure("活动标题不能为空且不能超过 150 个字符");
        }
        if (content == null) {
            return ServiceResult.failure("活动内容不能为空");
        }
        if (location == null || location.length() > 150) {
            return ServiceResult.failure("活动地点不能为空且不能超过 150 个字符");
        }
        if (coverImage != null && coverImage.length() > 255) {
            return ServiceResult.failure("封面图片路径不能超过 255 个字符");
        }
        if (startTime == null || endTime == null || deadline == null) {
            return ServiceResult.failure("请填写有效的开始、结束和报名截止时间");
        }
        if (endTime.isBefore(startTime)) {
            return ServiceResult.failure("结束时间不能早于开始时间");
        }
        if (deadline.isAfter(startTime)) {
            return ServiceResult.failure("报名截止时间不能晚于开始时间");
        }
        if (maxMembers == null) {
            return ServiceResult.failure("人数上限必须是大于等于 0 的整数");
        }

        activity.setTitle(title);
        activity.setContent(content);
        activity.setCoverImage(coverImage);
        activity.setLocation(location);
        activity.setStartTime(startTime);
        activity.setEndTime(endTime);
        activity.setDeadline(deadline);
        activity.setMaxMembers(maxMembers);
        return ServiceResult.success("验证通过", null);
    }

    /**
     * 解析日期时间。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    private LocalDateTime parseDateTime(String value) {
        value = ValidationUtils.trimToNull(value);
        if (value == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * 解析非负数整数。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    private Integer parseNonNegativeInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed >= 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
