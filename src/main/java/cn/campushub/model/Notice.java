package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的公告领域数据，并提供对应属性访问。
 */
public class Notice {
    private Long id;
    private String title;
    private String content;
    private String type;
    private boolean top;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String publisherName;

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
     * 获取`Type`。
     *
     * @return `Type`
     */
    public String getType() {
        return type;
    }

    /**
     * 设置`Type`。
     *
     * @param type 参数 `type`
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 判断是否`Top`。
     *
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public boolean isTop() {
        return top;
    }

    /**
     * 设置`Top`。
     *
     * @param top 参数 `top`
     */
    public void setTop(boolean top) {
        this.top = top;
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

    /**
     * 获取`PublisherName`。
     *
     * @return `PublisherName`
     */
    public String getPublisherName() {
        return publisherName;
    }

    /**
     * 设置`PublisherName`。
     *
     * @param publisherName 参数 `publisherName`
     */
    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    /**
     * 获取`Summary`。
     *
     * @return `Summary`
     */
    public String getSummary() {
        if (content == null || content.isBlank()) {
            return "";
        }
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 180
                ? normalized
                : normalized.substring(0, 180) + "...";
    }
}
