package cn.campushub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 表示系统中的商品领域数据，并提供对应属性访问。
 */
public class Goods {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private String conditionLevel;
    private String images;
    private String tradePlace;
    private String tradeMethod;
    private String contact;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String sellerNickname;
    private String sellerAvatar;
    private String sellerCollege;
    private String categoryName;
    private int favoriteCount;
    private boolean favorited;

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
     * 获取分类编号。
     *
     * @return 分类编号
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * 设置分类编号。
     *
     * @param categoryId 分类编号
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
     * 获取`Price`。
     *
     * @return `Price`
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置`Price`。
     *
     * @param price 参数 `price`
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取`ConditionLevel`。
     *
     * @return `ConditionLevel`
     */
    public String getConditionLevel() {
        return conditionLevel;
    }

    /**
     * 设置`ConditionLevel`。
     *
     * @param conditionLevel 参数 `conditionLevel`
     */
    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    /**
     * 获取`Images`。
     *
     * @return `Images`
     */
    public String getImages() {
        return images;
    }

    /**
     * 设置`Images`。
     *
     * @param images 参数 `images`
     */
    public void setImages(String images) {
        this.images = images;
    }

    /**
     * 获取`TradePlace`。
     *
     * @return `TradePlace`
     */
    public String getTradePlace() {
        return tradePlace;
    }

    /**
     * 设置`TradePlace`。
     *
     * @param tradePlace 参数 `tradePlace`
     */
    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    /**
     * 获取`TradeMethod`。
     *
     * @return `TradeMethod`
     */
    public String getTradeMethod() {
        return tradeMethod;
    }

    /**
     * 设置`TradeMethod`。
     *
     * @param tradeMethod 参数 `tradeMethod`
     */
    public void setTradeMethod(String tradeMethod) {
        this.tradeMethod = tradeMethod;
    }

    /**
     * 获取`Contact`。
     *
     * @return `Contact`
     */
    public String getContact() {
        return contact;
    }

    /**
     * 设置`Contact`。
     *
     * @param contact 参数 `contact`
     */
    public void setContact(String contact) {
        this.contact = contact;
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
     * 获取`SellerNickname`。
     *
     * @return `SellerNickname`
     */
    public String getSellerNickname() {
        return sellerNickname;
    }

    /**
     * 设置`SellerNickname`。
     *
     * @param sellerNickname 参数 `sellerNickname`
     */
    public void setSellerNickname(String sellerNickname) {
        this.sellerNickname = sellerNickname;
    }

    /**
     * 获取`SellerAvatar`。
     *
     * @return `SellerAvatar`
     */
    public String getSellerAvatar() {
        return sellerAvatar;
    }

    /**
     * 设置`SellerAvatar`。
     *
     * @param sellerAvatar 参数 `sellerAvatar`
     */
    public void setSellerAvatar(String sellerAvatar) {
        this.sellerAvatar = sellerAvatar;
    }

    /**
     * 获取`SellerCollege`。
     *
     * @return `SellerCollege`
     */
    public String getSellerCollege() {
        return sellerCollege;
    }

    /**
     * 设置`SellerCollege`。
     *
     * @param sellerCollege 参数 `sellerCollege`
     */
    public void setSellerCollege(String sellerCollege) {
        this.sellerCollege = sellerCollege;
    }

    /**
     * 获取`CategoryName`。
     *
     * @return `CategoryName`
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 设置`CategoryName`。
     *
     * @param categoryName 参数 `categoryName`
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 获取收藏数量。
     *
     * @return 收藏数量
     */
    public int getFavoriteCount() {
        return favoriteCount;
    }

    /**
     * 设置收藏数量。
     *
     * @param favoriteCount 参数 `favoriteCount`
     */
    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    /**
     * 判断是否`Favorited`。
     *
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public boolean isFavorited() {
        return favorited;
    }

    /**
     * 设置`Favorited`。
     *
     * @param favorited 参数 `favorited`
     */
    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    /**
     * 获取`FirstImage`。
     *
     * @return `FirstImage`
     */
    public String getFirstImage() {
        return getImageList().stream()
                .findFirst()
                .orElse("images/default-goods.png");
    }

    /**
     * 获取图片列表。
     *
     * @return 符合条件的数据列表
     */
    public List<String> getImageList() {
        if (images == null || images.isBlank()) {
            return List.of();
        }
        return Arrays.stream(images.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    /**
     * 获取`SellerInitial`。
     *
     * @return `SellerInitial`
     */
    public String getSellerInitial() {
        return sellerNickname == null || sellerNickname.isBlank()
                ? "U"
                : sellerNickname.substring(0, 1);
    }

    /**
     * 获取`TradeMethodText`。
     *
     * @return `TradeMethodText`
     */
    public String getTradeMethodText() {
        return switch (tradeMethod) {
            case "online" -> "支持线上付款";
            case "both" -> "线上/线下均可";
            default -> "线下交易";
        };
    }

    /**
     * 获取`WantMessage`。
     *
     * @return `WantMessage`
     */
    public String getWantMessage() {
        return switch (tradeMethod) {
            case "online" ->
                    "该商品支持线上付款，但线上付款流程暂未开放，请先联系卖家确认交易。";
            case "both" ->
                    "该商品支持线上付款或线下交易，具体方式请联系卖家确认。";
            default -> "请联系卖家约定线下交易地点。";
        };
    }
}
