package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的活动领域数据，并提供对应属性访问。
 */
public class Activity {
    private Long id;
    private String title;
    private String content;
    private String coverImage;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime deadline;
    private Integer maxMembers;
    private Integer currentMembers;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 获取编号。
     *
     * @return 编号
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置编号。
     *
     * @param id 业务数据编号
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取标题。
     *
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题。
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取`Content`。
     *
     * @return `Content`
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置`Content`。
     *
     * @param content 正文内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取封面图片。
     *
     * @return 封面图片
     */
    public String getCoverImage() {
        return coverImage;
    }

    /**
     * 设置封面图片。
     *
     * @param coverImage 封面图片地址
     */
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    /**
     * 获取地点。
     *
     * @return 地点
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置地点。
     *
     * @param location 参数 `location`
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 获取开始时间。
     *
     * @return 开始时间
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * 设置开始时间。
     *
     * @param startTime 开始时间
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取结束时间。
     *
     * @return 结束时间
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间。
     *
     * @param endTime 结束时间
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取截止时间。
     *
     * @return 截止时间
     */
    public LocalDateTime getDeadline() {
        return deadline;
    }

    /**
     * 设置截止时间。
     *
     * @param deadline 截止时间
     */
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    /**
     * 获取最大值成员。
     *
     * @return 最大值成员
     */
    public Integer getMaxMembers() {
        return maxMembers;
    }

    /**
     * 设置最大值成员。
     *
     * @param maxMembers 人数上限
     */
    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    /**
     * 获取当前成员。
     *
     * @return 当前成员
     */
    public Integer getCurrentMembers() {
        return currentMembers;
    }

    /**
     * 设置当前成员。
     *
     * @param currentMembers 当前人数
     */
    public void setCurrentMembers(Integer currentMembers) {
        this.currentMembers = currentMembers;
    }

    /**
     * 获取状态。
     *
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态。
     *
     * @param status 业务状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取`CreatedBy`。
     *
     * @return `CreatedBy`
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * 设置`CreatedBy`。
     *
     * @param createdBy 参数 `createdBy`
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取`CreatedAt`。
     *
     * @return `CreatedAt`
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置`CreatedAt`。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取`UpdatedAt`。
     *
     * @return `UpdatedAt`
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置`UpdatedAt`。
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
