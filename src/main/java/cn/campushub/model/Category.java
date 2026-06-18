package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的Category领域数据，并提供对应属性访问。
 */
public class Category {
    private Long id;
    private String name;
    private String type;
    private String description;
    private Integer sortOrder;
    private Integer status;
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
     * 获取`Name`。
     *
     * @return `Name`
     */
    public String getName() {
        return name;
    }

    /**
     * 设置`Name`。
     *
     * @param name 参数 `name`
     */
    public void setName(String name) {
        this.name = name;
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
     * 获取`Description`。
     *
     * @return `Description`
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置`Description`。
     *
     * @param description 描述内容
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取排序方式订单。
     *
     * @return 排序方式订单
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * 设置排序方式订单。
     *
     * @param sortOrder 参数 `sortOrder`
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * 获取状态。
     *
     * @return 状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态。
     *
     * @param status 业务状态
     */
    public void setStatus(Integer status) {
        this.status = status;
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
