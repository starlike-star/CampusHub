<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.Category" %>
<%@ page import="cn.campushub.model.Goods" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String myGoodsContextPath = request.getContextPath();
    boolean loginRequired =
            Boolean.TRUE.equals(request.getAttribute("loginRequired"));
    List<Goods> goodsList =
            (List<Goods>) request.getAttribute("goodsList");
    List<Category> goodsCategories =
            (List<Category>) request.getAttribute("goodsCategories");
    DateTimeFormatter myGoodsDateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DecimalFormat myGoodsPriceFormatter = new DecimalFormat("0.00");
%>
<section class="market-header card">
    <div>
        <span class="market-eyebrow">MY GOODS</span>
        <h1>我的商品</h1>
        <p>管理已发布商品、交易方式和在售状态</p>
    </div>
    <% if (!loginRequired) { %>
    <button type="button"
            class="primary-btn market-publish-btn"
            data-open-goods-modal
            data-authenticated="true">+ 发布商品</button>
    <% } %>
</section>

<% if (loginRequired) { %>
<section class="empty-state card">
    <h2>登录后查看我的商品</h2>
    <p>登录后可以管理自己发布的商品。</p>
    <a class="primary-btn"
       href="<%= myGoodsContextPath %>/login">去登录</a>
</section>
<% } else if (goodsList == null || goodsList.isEmpty()) { %>
<section class="empty-state card">
    <h2>还没有发布商品</h2>
    <p>发布第一件校园闲置物品吧。</p>
    <button type="button"
            class="primary-btn"
            data-open-goods-modal
            data-authenticated="true">发布商品</button>
</section>
<% } else { %>
<div class="goods-grid">
    <% for (Goods goods : goodsList) {
        String statusText = "在售";
        if ("reserved".equals(goods.getStatus())) {
            statusText = "已预订";
        } else if ("sold".equals(goods.getStatus())) {
            statusText = "已售出";
        } else if ("off_shelf".equals(goods.getStatus())) {
            statusText = "已下架";
        }
    %>
    <article class="goods-card card"
             data-goods-record
             data-goods-id="<%= goods.getId() %>"
             data-title="<%= HtmlUtils.escape(goods.getTitle()) %>"
             data-category-id="<%= goods.getCategoryId() == null
                    ? ""
                    : goods.getCategoryId() %>"
             data-price="<%= goods.getPrice() %>"
             data-condition-level="<%=
                    HtmlUtils.escape(goods.getConditionLevel())
             %>"
             data-images="<%= HtmlUtils.escape(goods.getImages()) %>"
             data-trade-place="<%=
                    HtmlUtils.escape(goods.getTradePlace())
             %>"
             data-trade-method="<%=
                    HtmlUtils.escape(goods.getTradeMethod())
             %>"
             data-contact="<%= HtmlUtils.escape(goods.getContact()) %>">
        <textarea class="goods-raw-description" hidden><%=
                HtmlUtils.escape(goods.getDescription())
        %></textarea>
        <div class="goods-image">
            <img src="<%= myGoodsContextPath %><%=
                    goods.getFirstImage().startsWith("/") ? "" : "/"
            %><%= HtmlUtils.escape(goods.getFirstImage()) %>"
                 onerror="this.onerror=null;this.src='<%= myGoodsContextPath %>/images/default-goods.png';"
                 alt="<%= HtmlUtils.escape(goods.getTitle()) %>">
            <span class="goods-status <%= goods.getStatus() %>"><%=
                    statusText
            %></span>
            <span class="goods-category"><%=
                    HtmlUtils.escape(goods.getCategoryName())
            %></span>
        </div>
        <div class="goods-card-body">
            <h2 class="goods-title"><%= HtmlUtils.escape(goods.getTitle()) %></h2>
            <strong class="goods-price">¥<%=
                    myGoodsPriceFormatter.format(goods.getPrice())
            %></strong>
            <span class="trade-method-badge <%= goods.getTradeMethod() %>"><%=
                    HtmlUtils.escape(goods.getTradeMethodText())
            %></span>
            <p class="goods-manage-time"><%= goods.getCreatedAt() == null
                    ? ""
                    : goods.getCreatedAt().format(myGoodsDateFormatter) %></p>
            <div class="goods-actions">
                <button type="button" data-goods-action="edit">编辑</button>
                <label class="goods-status-control">
                    <select data-goods-action="status"
                            aria-label="修改商品状态">
                        <option value="on_sale"
                                <%= "on_sale".equals(goods.getStatus())
                                        ? "selected"
                                        : "" %>>在售</option>
                        <option value="reserved"
                                <%= "reserved".equals(goods.getStatus())
                                        ? "selected"
                                        : "" %>>已预订</option>
                        <option value="sold"
                                <%= "sold".equals(goods.getStatus())
                                        ? "selected"
                                        : "" %>>已售出</option>
                        <option value="off_shelf"
                                <%= "off_shelf".equals(goods.getStatus())
                                        ? "selected"
                                        : "" %>>已下架</option>
                    </select>
                </label>
                <% if (!"off_shelf".equals(goods.getStatus())) { %>
                <button type="button"
                        class="goods-delete-btn"
                        data-goods-action="delete">下架</button>
                <a class="goods-detail-btn"
                   href="<%= myGoodsContextPath %>/goods/detail?id=<%=
                            goods.getId()
                   %>">查看详情</a>
                <% } %>
            </div>
        </div>
    </article>
    <% } %>
</div>
<% } %>

<div class="goods-modal" data-goods-modal hidden>
    <div class="goods-modal-backdrop" data-close-goods-modal></div>
    <section class="goods-modal-dialog"
             role="dialog"
             aria-modal="true"
             aria-labelledby="goodsModalTitle">
        <div class="goods-modal-heading">
            <div>
                <span>MY GOODS</span>
                <h2 id="goodsModalTitle">发布商品</h2>
            </div>
            <button type="button"
                    class="modal-close-btn"
                    data-close-goods-modal
                    aria-label="关闭">×</button>
        </div>
        <form data-goods-form>
            <input type="hidden" name="goodsId">
            <label>商品标题
                <input type="text" name="title" maxlength="150" required>
            </label>
            <div class="goods-form-row">
                <label>分类
                    <select name="categoryId" required>
                        <option value="">请选择分类</option>
                        <% if (goodsCategories != null) {
                            for (Category category : goodsCategories) { %>
                        <option value="<%= category.getId() %>"><%=
                                HtmlUtils.escape(category.getName())
                        %></option>
                        <%  }
                           } %>
                    </select>
                </label>
                <label>价格
                    <input type="number"
                           name="price"
                           min="0"
                           max="99999999.99"
                           step="0.01"
                           required>
                </label>
            </div>
            <div class="goods-form-row">
                <label>新旧程度
                    <input type="text"
                           name="conditionLevel"
                           maxlength="50"
                           required>
                </label>
                <label>交易地点
                    <input type="text"
                           name="tradePlace"
                           maxlength="150">
                </label>
            </div>
            <div class="goods-form-row">
                <label>交易方式
                    <select name="tradeMethod" required>
                        <option value="offline">线下交易</option>
                        <option value="online">线上付款</option>
                        <option value="both">线上/线下均可</option>
                    </select>
                </label>
                <label>联系方式
                    <input type="text"
                           name="contact"
                           maxlength="100"
                           required>
                </label>
            </div>
            <label>商品描述
                <textarea name="description" rows="6" required></textarea>
            </label>
            <div class="image-upload" data-image-upload="goods">
                <span>商品封面</span>
                <input type="hidden"
                       name="images"
                       data-image-upload-value>
                <div class="image-upload-controls">
                    <input class="image-upload-file"
                           type="file"
                           accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp"
                           data-image-upload-input>
                    <button class="image-upload-button"
                            type="button"
                            data-image-upload-button>选择图片</button>
                    <small class="image-upload-status"
                           data-image-upload-status>支持重新选择，最大 5MB</small>
                </div>
                <div class="image-upload-preview"
                     data-image-upload-preview
                     hidden></div>
            </div>
            <p class="goods-trade-hint" data-trade-place-hint>
                线下交易建议填写明确的交易地点。
            </p>
            <p class="goods-form-error" data-goods-form-error hidden></p>
            <div class="goods-modal-actions">
                <button type="button" data-close-goods-modal>取消</button>
                <button type="submit" class="primary-btn">确认发布</button>
            </div>
        </form>
    </section>
</div>
