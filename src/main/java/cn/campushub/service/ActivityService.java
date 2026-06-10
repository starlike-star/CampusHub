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

public class ActivityService {
    private static final Set<String> STATUSES =
            Set.of("signup", "closed", "ongoing", "finished");
    private static final Set<String> SORTS = Set.of("latest", "hot", "soon");

    private final ActivityDao activityDao;

    public ActivityService() {
        this(new JdbcActivityDao());
    }

    ActivityService(ActivityDao activityDao) {
        this.activityDao = activityDao;
    }

    public List<ActivityVO> list(String status, String keyword, String sort)
            throws SQLException {
        return activityDao.findAll(
                normalizeStatusFilter(status),
                normalizeKeyword(keyword),
                normalizeSort(sort)
        );
    }

    public Optional<ActivityVO> detail(long id) throws SQLException {
        return id > 0 ? activityDao.findById(id) : Optional.empty();
    }

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

    public String normalizeStatusValue(String value) {
        return value != null && STATUSES.contains(value) ? value : "all";
    }

    public String normalizeSort(String value) {
        return value != null && SORTS.contains(value) ? value : "latest";
    }

    public String normalizeKeyword(String value) {
        value = ValidationUtils.trimToNull(value);
        return value != null && value.length() > 100
                ? value.substring(0, 100)
                : value;
    }

    private String normalizeStatusFilter(String value) {
        return value != null && STATUSES.contains(value) ? value : null;
    }

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

    private Integer parseNonNegativeInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed >= 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
