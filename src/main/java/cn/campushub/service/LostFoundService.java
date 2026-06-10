package cn.campushub.service;

import cn.campushub.dao.JdbcLostFoundDao;
import cn.campushub.dao.LostFoundDao;
import cn.campushub.model.Category;
import cn.campushub.model.LostFound;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LostFoundService {
    private static final Set<String> TYPES = Set.of("lost", "found");
    private static final Set<String> STATUSES =
            Set.of("pending", "claiming", "completed", "closed");
    private static final Set<String> SORTS = Set.of("latest", "oldest");

    private final LostFoundDao lostFoundDao;

    public LostFoundService() {
        this(new JdbcLostFoundDao());
    }

    LostFoundService(LostFoundDao lostFoundDao) {
        this.lostFoundDao = lostFoundDao;
    }

    public List<LostFound> list(
            String type,
            String status,
            String keyword,
            String categoryIdValue,
            String sort
    ) throws SQLException {
        Long categoryId = normalizeCategoryId(categoryIdValue);
        if (categoryId != null && !lostFoundDao.isActiveCategory(categoryId)) {
            categoryId = null;
        }
        return lostFoundDao.findAll(
                normalizeType(type),
                normalizeStatusFilter(status),
                normalizeKeyword(keyword),
                categoryId,
                normalizeSort(sort)
        );
    }

    public List<LostFound> listOwn(long userId) throws SQLException {
        return lostFoundDao.findByUser(userId);
    }

    public List<Category> listCategories() throws SQLException {
        return lostFoundDao.findActiveCategories();
    }

    public Optional<LostFound> detail(long id) throws SQLException {
        return id > 0 ? lostFoundDao.findById(id) : Optional.empty();
    }

    public ServiceResult<Long> create(
            long userId,
            String type,
            String itemName,
            String title,
            String categoryId,
            String description,
            String place,
            String eventTime,
            String images,
            String contact
    ) throws SQLException {
        LostFound item = new LostFound();
        item.setUserId(userId);
        ServiceResult<Void> validation = populateAndValidate(
                item, type, itemName, title, categoryId, description,
                place, eventTime, images, contact
        );
        if (!validation.success()) {
            return ServiceResult.failure(validation.message());
        }
        return ServiceResult.success("发布成功", lostFoundDao.create(item));
    }

    public ServiceResult<Void> update(
            long id,
            long userId,
            boolean admin,
            String type,
            String itemName,
            String title,
            String categoryId,
            String description,
            String place,
            String eventTime,
            String images,
            String contact
    ) throws SQLException {
        if (id <= 0) {
            return ServiceResult.failure("失物招领参数无效");
        }
        LostFound item = new LostFound();
        item.setId(id);
        item.setUserId(userId);
        ServiceResult<Void> validation = populateAndValidate(
                item, type, itemName, title, categoryId, description,
                place, eventTime, images, contact
        );
        if (!validation.success()) {
            return validation;
        }
        if (!lostFoundDao.update(item, admin)) {
            return ServiceResult.failure("信息不存在或无权编辑");
        }
        return ServiceResult.success("信息已更新", null);
    }

    public ServiceResult<Void> updateStatus(
            long id,
            long userId,
            boolean admin,
            String status
    ) throws SQLException {
        if (id <= 0 || status == null || !STATUSES.contains(status)) {
            return ServiceResult.failure("状态参数无效");
        }
        if (!lostFoundDao.updateStatus(id, userId, admin, status)) {
            return ServiceResult.failure("信息不存在或无权修改");
        }
        return ServiceResult.success("状态已更新", null);
    }

    public String normalizeTypeValue(String value) {
        return value != null && TYPES.contains(value) ? value : "all";
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

    public Long normalizeCategoryId(String value) {
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String normalizeType(String value) {
        return value != null && TYPES.contains(value) ? value : null;
    }

    private String normalizeStatusFilter(String value) {
        return value != null && STATUSES.contains(value) ? value : null;
    }

    private ServiceResult<Void> populateAndValidate(
            LostFound item,
            String type,
            String itemName,
            String title,
            String categoryIdValue,
            String description,
            String place,
            String eventTimeValue,
            String images,
            String contact
    ) throws SQLException {
        itemName = ValidationUtils.trimToNull(itemName);
        title = ValidationUtils.trimToNull(title);
        description = ValidationUtils.trimToNull(description);
        place = ValidationUtils.trimToNull(place);
        images = ValidationUtils.trimToNull(images);
        contact = ValidationUtils.trimToNull(contact);
        Long categoryId = normalizeCategoryId(categoryIdValue);
        LocalDateTime eventTime = parseDateTime(eventTimeValue);

        if (type == null || !TYPES.contains(type)) {
            return ServiceResult.failure("请选择有效的信息类型");
        }
        if (itemName == null || itemName.length() > 100) {
            return ServiceResult.failure("物品名称不能为空且不能超过 100 个字符");
        }
        if (title == null || title.length() > 150) {
            return ServiceResult.failure("标题不能为空且不能超过 150 个字符");
        }
        if (description == null) {
            return ServiceResult.failure("详细描述不能为空");
        }
        if (categoryId == null || !lostFoundDao.isActiveCategory(categoryId)) {
            return ServiceResult.failure("请选择有效的物品分类");
        }
        if (place != null && place.length() > 150) {
            return ServiceResult.failure("地点不能超过 150 个字符");
        }
        if (ValidationUtils.trimToNull(eventTimeValue) != null
                && eventTime == null) {
            return ServiceResult.failure("事件时间格式无效");
        }
        if (contact == null || contact.length() > 100) {
            return ServiceResult.failure("联系方式不能为空且不能超过 100 个字符");
        }
        item.setType(type);
        item.setItemName(itemName);
        item.setTitle(title);
        item.setCategoryId(categoryId);
        item.setDescription(description);
        item.setPlace(place);
        item.setEventTime(eventTime);
        item.setImages(images);
        item.setContact(contact);
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
}
