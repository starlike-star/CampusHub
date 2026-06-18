package cn.campushub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 表示系统中的商品订单领域数据，并提供对应属性访问。
 */
public class GoodsOrder {
    private Long id;
    private String orderNo;
    private Long goodsId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal amount;
    private String payMethod;
    private String status;
    private String payToken;
    private LocalDateTime expireAt;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String goodsTitle;

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
     * 获取`OrderNo`。
     *
     * @return `OrderNo`
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置`OrderNo`。
     *
     * @param orderNo 参数 `orderNo`
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取商品编号。
     *
     * @return 商品编号
     */
    public Long getGoodsId() {
        return goodsId;
    }

    /**
     * 设置商品编号。
     *
     * @param goodsId 商品编号
     */
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * 获取`BuyerId`。
     *
     * @return `BuyerId`
     */
    public Long getBuyerId() {
        return buyerId;
    }

    /**
     * 设置`BuyerId`。
     *
     * @param buyerId `buyer`编号
     */
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    /**
     * 获取`SellerId`。
     *
     * @return `SellerId`
     */
    public Long getSellerId() {
        return sellerId;
    }

    /**
     * 设置`SellerId`。
     *
     * @param sellerId `seller`编号
     */
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * 获取`Amount`。
     *
     * @return `Amount`
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置`Amount`。
     *
     * @param amount 参数 `amount`
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取`PayMethod`。
     *
     * @return `PayMethod`
     */
    public String getPayMethod() {
        return payMethod;
    }

    /**
     * 设置`PayMethod`。
     *
     * @param payMethod 参数 `payMethod`
     */
    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
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
     * 获取`PayToken`。
     *
     * @return `PayToken`
     */
    public String getPayToken() {
        return payToken;
    }

    /**
     * 设置`PayToken`。
     *
     * @param payToken 参数 `payToken`
     */
    public void setPayToken(String payToken) {
        this.payToken = payToken;
    }

    /**
     * 获取`ExpireAt`。
     *
     * @return `ExpireAt`
     */
    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    /**
     * 设置`ExpireAt`。
     *
     * @param expireAt 参数 `expireAt`
     */
    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    /**
     * 获取`PaidAt`。
     *
     * @return `PaidAt`
     */
    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    /**
     * 设置`PaidAt`。
     *
     * @param paidAt 参数 `paidAt`
     */
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    /**
     * 获取`CancelledAt`。
     *
     * @return `CancelledAt`
     */
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    /**
     * 设置`CancelledAt`。
     *
     * @param cancelledAt 参数 `cancelledAt`
     */
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
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
     * 获取商品标题。
     *
     * @return 商品标题
     */
    public String getGoodsTitle() {
        return goodsTitle;
    }

    /**
     * 设置商品标题。
     *
     * @param goodsTitle 参数 `goodsTitle`
     */
    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }
}
