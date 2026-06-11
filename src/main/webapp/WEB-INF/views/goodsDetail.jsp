<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.Goods" %>
<%@ page import="cn.campushub.model.Category" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String contextPath = request.getContextPath();
    Goods goods = (Goods) request.getAttribute("goods");
    List<Category> goodsCategories =
            (List<Category>) request.getAttribute("goodsCategories");
    boolean goodsOwner =
            Boolean.TRUE.equals(request.getAttribute("goodsOwner"));
    SessionUser goodsLoginUser =
            (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
    DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DecimalFormat priceFormatter = new DecimalFormat("0.00");
    String statusText = "在售";
    if ("reserved".equals(goods.getStatus())) {
        statusText = "已预订";
    } else if ("sold".equals(goods.getStatus())) {
        statusText = "已售出";
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="<%= contextPath %>">
    <title><%= HtmlUtils.escape(goods.getTitle()) %> - CampusHub 二手市场</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/report.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/image-upload.css">
</head>
<body data-report-authenticated="<%= goodsLoginUser != null %>">
<header class="simple-topbar">
    <a class="brand" href="<%= contextPath %>/home#market">
        <span class="brand-mark">
            <img src="<%= contextPath %>/images/Logo_noword.png" alt="CampusHub">
        </span>
        <span class="brand-copy">
            <strong>CampusHub</strong><small>校园综合社区</small>
        </span>
    </a>
    <a class="back-link" href="<%= contextPath %>/home#market">返回二手市场</a>
</header>

<main class="goods-detail-page">
    <article class="goods-detail-card card"
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
        <section class="goods-detail-gallery">
            <%
                List<String> detailImages = goods.getImageList().isEmpty()
                        ? java.util.List.of("images/default-goods.png")
                        : goods.getImageList();
                String mainImage = detailImages.get(0);
            %>
            <img class="goods-main-image"
                 src="<%= contextPath %><%= mainImage.startsWith("/") ? "" : "/" %><%=
                        HtmlUtils.escape(mainImage)
                 %>"
                 onerror="this.onerror=null;this.src='<%= contextPath %>/images/default-goods.png';"
                 alt="<%= HtmlUtils.escape(goods.getTitle()) %>">
            <% if (detailImages.size() > 1) { %>
            <div class="goods-thumbnails">
                <% for (String image : detailImages) { %>
                <button type="button"
                        data-goods-thumbnail="<%= contextPath %><%=
                                image.startsWith("/") ? "" : "/"
                        %><%=
                                HtmlUtils.escape(image)
                        %>">
                    <img src="<%= contextPath %><%=
                            image.startsWith("/") ? "" : "/"
                    %><%=
                            HtmlUtils.escape(image)
                    %>" alt="商品缩略图">
                </button>
                <% } %>
            </div>
            <% } %>
        </section>
        <section class="goods-detail-info">
            <div class="goods-detail-badges">
                <span class="goods-status <%= goods.getStatus() %>"><%=
                        statusText
                %></span>
                <% if (goods.getCategoryName() != null) { %>
                <span class="goods-category"><%=
                        HtmlUtils.escape(goods.getCategoryName())
                %></span>
                <% } %>
            </div>
            <h1><%= HtmlUtils.escape(goods.getTitle()) %></h1>
            <strong class="goods-detail-price">¥<%=
                    priceFormatter.format(goods.getPrice())
            %></strong>
            <dl class="goods-detail-meta">
                <div>
                    <dt>新旧程度</dt>
                    <dd><%= HtmlUtils.escape(
                            goods.getConditionLevel() == null
                                    ? "未填写"
                                    : goods.getConditionLevel()
                    ) %></dd>
                </div>
                <div>
                    <dt>交易方式</dt>
                    <dd><span class="trade-method-badge <%=
                            goods.getTradeMethod()
                    %>"><%= HtmlUtils.escape(
                            goods.getTradeMethodText()
                    ) %></span></dd>
                </div>
                <div>
                    <dt>交易地点</dt>
                    <dd><%= HtmlUtils.escape(
                            goods.getTradePlace() == null
                                    ? "地点待定"
                                    : goods.getTradePlace()
                    ) %></dd>
                </div>
                <div>
                    <dt>联系方式</dt>
                    <dd><%= HtmlUtils.escape(
                            goods.getContact() == null
                                    ? "未填写"
                                    : goods.getContact()
                    ) %></dd>
                </div>
                <div>
                    <dt>发布时间</dt>
                    <dd><%= goods.getCreatedAt() == null
                            ? ""
                            : goods.getCreatedAt().format(dateFormatter) %></dd>
                </div>
            </dl>
            <% if (goodsOwner) { %>
            <div class="goods-detail-owner-actions">
                <button type="button"
                        class="primary-btn"
                        data-goods-action="edit">编辑商品</button>
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
                        <option value="off_shelf">下架商品</option>
                    </select>
                </label>
                <button type="button"
                        class="goods-delete-btn"
                        data-goods-action="delete">下架商品</button>
            </div>
            <% } else { %>
            <div class="goods-detail-buyer-actions">
                <button type="button"
                        class="goods-favorite-btn goods-detail-favorite <%=
                                goods.isFavorited() ? "saved" : ""
                        %>"
                        data-goods-action="favorite"
                        data-goods-id="<%= goods.getId() %>"
                        aria-pressed="<%= goods.isFavorited() %>">
                    <svg><use href="#icon-bookmark"></use></svg>
                    收藏 <span><%= goods.getFavoriteCount() %></span>
                </button>
                <a href="<%= contextPath %>/private-messages/thread?receiverId=<%=
                        goods.getUserId()
                %>">私信卖家</a>
                <button type="button"
                        class="primary-btn"
                        data-want-goods
                        data-want-message="<%=
                                HtmlUtils.escape(goods.getWantMessage())
                        %>">我想要</button>
                <button type="button"
                        class="report-btn"
                        data-target-type="goods"
                        data-target-id="<%= goods.getId() %>">举报商品</button>
            </div>
            <% } %>
            <% if (!"offline".equals(goods.getTradeMethod())) { %>
            <p class="online-payment-notice">
                线上付款流程暂未开放，请联系卖家确认。
            </p>
            <% } %>
        </section>
    </article>

    <section class="goods-description-card card">
        <h2>商品描述</h2>
        <p><%= HtmlUtils.escape(goods.getDescription()) %></p>
    </section>

    <section class="goods-seller-card card">
        <span class="goods-seller-avatar large">
            <% if (goods.getSellerAvatar() != null
                    && !goods.getSellerAvatar().isBlank()) { %>
            <img src="<%= contextPath %><%=
                    HtmlUtils.escape(HtmlUtils.resourcePath(
                            goods.getSellerAvatar()
                    ))
            %>" alt="<%= HtmlUtils.escape(goods.getSellerNickname()) %>">
            <% } else { %>
            <%= HtmlUtils.escape(goods.getSellerInitial()) %>
            <% } %>
        </span>
        <div>
            <span>卖家</span>
            <h2><%= HtmlUtils.escape(goods.getSellerNickname()) %></h2>
            <p><%= HtmlUtils.escape(
                    goods.getSellerCollege() == null
                            ? "学院未填写"
                            : goods.getSellerCollege()
            ) %></p>
        </div>
    </section>
</main>

<div class="goods-modal" data-goods-modal hidden>
    <div class="goods-modal-backdrop" data-close-goods-modal></div>
    <section class="goods-modal-dialog"
             role="dialog"
             aria-modal="true"
             aria-labelledby="goodsModalTitle">
        <div class="goods-modal-heading">
            <div>
                <span>EDIT GOODS</span>
                <h2 id="goodsModalTitle">编辑商品</h2>
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
                        <% for (Category category : goodsCategories) { %>
                        <option value="<%= category.getId() %>"><%=
                                HtmlUtils.escape(category.getName())
                        %></option>
                        <% } %>
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
                <button type="submit" class="primary-btn">保存修改</button>
            </div>
        </form>
    </section>
</div>

<svg class="svg-sprite" aria-hidden="true">
    <symbol id="icon-bookmark" viewBox="0 0 24 24">
        <path d="M6 3h12v18l-6-4-6 4z"></path>
    </symbol>
</svg>
<div class="toast" id="toast" role="status"></div>
<script src="<%= contextPath %>/js/image-upload.js"></script>
<script src="<%= contextPath %>/js/market.js"></script>
<script src="<%= contextPath %>/js/report.js"></script>
</body>
</html>
