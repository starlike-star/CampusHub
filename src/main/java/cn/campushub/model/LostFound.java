package cn.campushub.model;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 表示系统中的失物招领领域数据，并提供对应属性访问。
 */
public class LostFound {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String type;
    private String itemName;
    private String title;
    private String description;
    private String place;
    private LocalDateTime eventTime;
    private String images;
    private String contact;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String publisherNickname;
    private String publisherAvatar;
    private String publisherCollege;
    private String categoryName;

    /**
     * 获取编号。
     *
     * @return 编号
     */
    public Long getId() { return id; }
    /**
     * 设置编号。
     *
     * @param id 业务数据编号
     */
    public void setId(Long id) { this.id = id; }
    /**
     * 获取用户编号。
     *
     * @return 用户编号
     */
    public Long getUserId() { return userId; }
    /**
     * 设置用户编号。
     *
     * @param userId 用户编号
     */
    public void setUserId(Long userId) { this.userId = userId; }
    /**
     * 获取分类编号。
     *
     * @return 分类编号
     */
    public Long getCategoryId() { return categoryId; }
    /**
     * 设置分类编号。
     *
     * @param categoryId 分类编号
     */
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    /**
     * 获取`Type`。
     *
     * @return `Type`
     */
    public String getType() { return type; }
    /**
     * 设置`Type`。
     *
     * @param type 参数 `type`
     */
    public void setType(String type) { this.type = type; }
    /**
     * 获取`ItemName`。
     *
     * @return `ItemName`
     */
    public String getItemName() { return itemName; }
    /**
     * 设置`ItemName`。
     *
     * @param itemName 参数 `itemName`
     */
    public void setItemName(String itemName) { this.itemName = itemName; }
    /**
     * 获取标题。
     *
     * @return 标题
     */
    public String getTitle() { return title; }
    /**
     * 设置标题。
     *
     * @param title 标题
     */
    public void setTitle(String title) { this.title = title; }
    /**
     * 获取`Description`。
     *
     * @return `Description`
     */
    public String getDescription() { return description; }
    /**
     * 设置`Description`。
     *
     * @param description 描述内容
     */
    public void setDescription(String description) { this.description = description; }
    /**
     * 获取`Place`。
     *
     * @return `Place`
     */
    public String getPlace() { return place; }
    /**
     * 设置`Place`。
     *
     * @param place 参数 `place`
     */
    public void setPlace(String place) { this.place = place; }
    /**
     * 获取`EventTime`。
     *
     * @return `EventTime`
     */
    public LocalDateTime getEventTime() { return eventTime; }
    /**
     * 设置`EventTime`。
     *
     * @param eventTime 参数 `eventTime`
     */
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
    /**
     * 获取`Images`。
     *
     * @return `Images`
     */
    public String getImages() { return images; }
    /**
     * 设置`Images`。
     *
     * @param images 参数 `images`
     */
    public void setImages(String images) { this.images = images; }
    /**
     * 获取`Contact`。
     *
     * @return `Contact`
     */
    public String getContact() { return contact; }
    /**
     * 设置`Contact`。
     *
     * @param contact 参数 `contact`
     */
    public void setContact(String contact) { this.contact = contact; }
    /**
     * 获取状态。
     *
     * @return 状态
     */
    public String getStatus() { return status; }
    /**
     * 设置状态。
     *
     * @param status 业务状态
     */
    public void setStatus(String status) { this.status = status; }
    /**
     * 获取`CreatedAt`。
     *
     * @return `CreatedAt`
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 设置`CreatedAt`。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    /**
     * 获取`UpdatedAt`。
     *
     * @return `UpdatedAt`
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /**
     * 设置`UpdatedAt`。
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    /**
     * 获取`PublisherNickname`。
     *
     * @return `PublisherNickname`
     */
    public String getPublisherNickname() { return publisherNickname; }
    /**
     * 设置`PublisherNickname`。
     *
     * @param publisherNickname 参数 `publisherNickname`
     */
    public void setPublisherNickname(String publisherNickname) {
        this.publisherNickname = publisherNickname;
    }
    /**
     * 获取`PublisherAvatar`。
     *
     * @return `PublisherAvatar`
     */
    public String getPublisherAvatar() { return publisherAvatar; }
    /**
     * 设置`PublisherAvatar`。
     *
     * @param publisherAvatar 参数 `publisherAvatar`
     */
    public void setPublisherAvatar(String publisherAvatar) {
        this.publisherAvatar = publisherAvatar;
    }
    /**
     * 获取`PublisherCollege`。
     *
     * @return `PublisherCollege`
     */
    public String getPublisherCollege() { return publisherCollege; }
    /**
     * 设置`PublisherCollege`。
     *
     * @param publisherCollege 参数 `publisherCollege`
     */
    public void setPublisherCollege(String publisherCollege) {
        this.publisherCollege = publisherCollege;
    }
    /**
     * 获取`CategoryName`。
     *
     * @return `CategoryName`
     */
    public String getCategoryName() { return categoryName; }
    /**
     * 设置`CategoryName`。
     *
     * @param categoryName 参数 `categoryName`
     */
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    /**
     * 获取`FirstImage`。
     *
     * @return `FirstImage`
     */
    public String getFirstImage() {
        if (images == null || images.isBlank()) {
            return "images/default-lostfound.png";
        }
        return Arrays.stream(images.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .findFirst()
                .orElse("images/default-lostfound.png");
    }
}
