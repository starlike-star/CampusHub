package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的站内通知领域数据，并提供对应属性访问。
 */
public class Message {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

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
     * 获取用户编号。
     *
     * @return 用户编号
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户编号。
     *
     * @param userId 用户编号
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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
     * 判断是否已读状态。
     *
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public boolean isRead() {
        return read;
    }

    /**
     * 设置已读状态。
     *
     * @param read 参数 `read`
     */
    public void setRead(boolean read) {
        this.read = read;
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
}
