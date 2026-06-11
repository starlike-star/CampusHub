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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getPublisherNickname() { return publisherNickname; }
    public void setPublisherNickname(String publisherNickname) {
        this.publisherNickname = publisherNickname;
    }
    public String getPublisherAvatar() { return publisherAvatar; }
    public void setPublisherAvatar(String publisherAvatar) {
        this.publisherAvatar = publisherAvatar;
    }
    public String getPublisherCollege() { return publisherCollege; }
    public void setPublisherCollege(String publisherCollege) {
        this.publisherCollege = publisherCollege;
    }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

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
