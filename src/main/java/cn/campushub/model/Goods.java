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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getConditionLevel() {
        return conditionLevel;
    }

    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public String getTradeMethod() {
        return tradeMethod;
    }

    public void setTradeMethod(String tradeMethod) {
        this.tradeMethod = tradeMethod;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSellerNickname() {
        return sellerNickname;
    }

    public void setSellerNickname(String sellerNickname) {
        this.sellerNickname = sellerNickname;
    }

    public String getSellerAvatar() {
        return sellerAvatar;
    }

    public void setSellerAvatar(String sellerAvatar) {
        this.sellerAvatar = sellerAvatar;
    }

    public String getSellerCollege() {
        return sellerCollege;
    }

    public void setSellerCollege(String sellerCollege) {
        this.sellerCollege = sellerCollege;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public String getFirstImage() {
        return getImageList().stream()
                .findFirst()
                .orElse("images/default-goods.png");
    }

    public List<String> getImageList() {
        if (images == null || images.isBlank()) {
            return List.of();
        }
        return Arrays.stream(images.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    public String getSellerInitial() {
        return sellerNickname == null || sellerNickname.isBlank()
                ? "U"
                : sellerNickname.substring(0, 1);
    }

    public String getTradeMethodText() {
        return switch (tradeMethod) {
            case "online" -> "支持线上付款";
            case "both" -> "线上/线下均可";
            default -> "线下交易";
        };
    }

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
