<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.Category" %>
<%@ page import="cn.campushub.model.Goods" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%
    String marketContextPath = request.getContextPath();
    SessionUser marketUser =
            (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
    List<Goods> goodsList =
            (List<Goods>) request.getAttribute("goodsList");
    List<Category> goodsCategories =
            (List<Category>) request.getAttribute("goodsCategories");
    String keyword = (String) request.getAttribute("keyword");
    Long selectedCategoryId =
            (Long) request.getAttribute("selectedCategoryId");
    String selectedStatus =
            (String) request.getAttribute("selectedStatus");
    String selectedTradeMethod =
            (String) request.getAttribute("selectedTradeMethod");
    String selectedSort =
            (String) request.getAttribute("selectedSort");
    boolean marketAdmin =
            marketUser != null && "admin".equalsIgnoreCase(marketUser.role());
    DateTimeFormatter goodsDateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DecimalFormat priceFormatter = new DecimalFormat("0.00");
    Map<String, Category> categoriesByName = new HashMap<>();
    if (goodsCategories != null) {
        for (Category category : goodsCategories) {
            categoriesByName.put(category.getName(), category);
        }
    }
    String[] preferredCategories = {
            "电子数码", "教材资料", "生活用品", "运动器材", "免费赠送"
    };
    Set<String> renderedCategories = new HashSet<>();
%>
<section class="market-header card">
    <div>
        <span class="market-eyebrow">CAMPUS MARKET</span>
        <h1>二手市场</h1>
        <p>发现校园闲置好物，让物品继续发光</p>
    </div>
    <button type="button"
            class="primary-btn market-publish-btn"
            data-open-goods-modal
            data-authenticated="<%= marketUser != null %>">+ 发布商品</button>
</section>

<section class="market-toolbar card">
    <form class="market-search" data-market-search>
        <svg><use href="#icon-search"></use></svg>
        <input type="search"
               name="keyword"
               maxlength="100"
               value="<%= HtmlUtils.escape(keyword) %>"
               placeholder="搜索商品名称、描述、交易地点...">
        <button type="submit">搜索</button>
    </form>

    <div class="market-filter-row">
        <span class="market-filter-label">分类</span>
        <div class="market-category-tabs">
            <button type="button"
                    class="<%= selectedCategoryId == null ? "active" : "" %>"
                    data-market-filter="categoryId"
                    data-filter-value="">全部</button>
            <% for (String categoryName : preferredCategories) {
                Category category = categoriesByName.get(categoryName);
                renderedCategories.add(categoryName);
            %>
            <button type="button"
                    class="<%= category != null
                            && category.getId().equals(selectedCategoryId)
                            ? "active"
                            : "" %>"
                    <% if (category != null) { %>
                    data-market-filter="categoryId"
                    data-filter-value="<%= category.getId() %>"
                    <% } else { %>
                    disabled title="数据库中暂未配置该分类"
                    <% } %>><%= categoryName %></button>
            <% } %>
            <% if (goodsCategories != null) {
                for (Category category : goodsCategories) {
                    if (renderedCategories.contains(category.getName())) {
                        continue;
                    }
            %>
            <button type="button"
                    class="<%= category.getId().equals(selectedCategoryId)
                            ? "active"
                            : "" %>"
                    data-market-filter="categoryId"
                    data-filter-value="<%= category.getId() %>"><%=
                    HtmlUtils.escape(category.getName())
            %></button>
            <%  }
               } %>
        </div>
    </div>

    <div class="market-filter-row market-status-row">
        <span class="market-filter-label">状态</span>
        <div class="market-category-tabs">
            <button type="button"
                    class="<%= selectedStatus == null ? "active" : "" %>"
                    data-market-filter="status"
                    data-filter-value="">全部</button>
            <button type="button"
                    class="<%= "on_sale".equals(selectedStatus)
                            ? "active"
                            : "" %>"
                    data-market-filter="status"
                    data-filter-value="on_sale">在售</button>
            <button type="button"
                    class="<%= "reserved".equals(selectedStatus)
                            ? "active"
                            : "" %>"
                    data-market-filter="status"
                    data-filter-value="reserved">已预订</button>
            <button type="button"
                    class="<%= "sold".equals(selectedStatus)
                            ? "active"
                            : "" %>"
                    data-market-filter="status"
                    data-filter-value="sold">已售出</button>
        </div>
        <label class="market-sort">
            <span>排序</span>
            <select data-market-sort>
                <option value="latest"
                        <%= "latest".equals(selectedSort)
                                ? "selected"
                                : "" %>>最新发布</option>
                <option value="price_asc"
                        <%= "price_asc".equals(selectedSort)
                                ? "selected"
                                : "" %>>价格从低到高</option>
                <option value="price_desc"
                        <%= "price_desc".equals(selectedSort)
                                ? "selected"
                                : "" %>>价格从高到低</option>
                <option value="hot"
                        <%= "hot".equals(selectedSort)
                                ? "selected"
                                : "" %>>热门收藏</option>
            </select>
        </label>
    </div>
    <div class="market-filter-row">
        <span class="market-filter-label">方式</span>
        <div class="market-category-tabs">
            <button type="button"
                    class="<%= selectedTradeMethod == null ? "active" : "" %>"
                    data-market-filter="tradeMethod"
                    data-filter-value="">全部方式</button>
            <button type="button"
                    class="<%= "offline".equals(selectedTradeMethod)
                            ? "active"
                            : "" %>"
                    data-market-filter="tradeMethod"
                    data-filter-value="offline">线下交易</button>
            <button type="button"
                    class="<%= "online".equals(selectedTradeMethod)
                            ? "active"
                            : "" %>"
                    data-market-filter="tradeMethod"
                    data-filter-value="online">线上付款</button>
            <button type="button"
                    class="<%= "both".equals(selectedTradeMethod)
                            ? "active"
                            : "" %>"
                    data-market-filter="tradeMethod"
                    data-filter-value="both">线上/线下均可</button>
        </div>
    </div>
</section>

<% if (goodsList == null || goodsList.isEmpty()) { %>
<section class="empty-state market-empty card">
    <h2>暂无商品</h2>
    <p>还没有同学发布闲置物品，快来发布第一个吧。</p>
    <button type="button"
            class="primary-btn"
            data-open-goods-modal
            data-authenticated="<%= marketUser != null %>">发布商品</button>
</section>
<% } else { %>
<div class="goods-grid">
    <% for (Goods goods : goodsList) {
        boolean owner = marketUser != null
                && (marketAdmin || marketUser.id() == goods.getUserId());
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
        <a class="goods-image"
           href="<%= marketContextPath %>/goods/detail?id=<%= goods.getId() %>">
            <img src="<%= marketContextPath %>/<%=
                    HtmlUtils.escape(goods.getFirstImage())
            %>"
                 onerror="this.onerror=null;this.src='<%= marketContextPath %>/images/default-goods.png';"
                 alt="<%= HtmlUtils.escape(goods.getTitle()) %>"
                 loading="lazy">
            <span class="goods-status <%= goods.getStatus() %>"><%=
                    statusText
            %></span>
            <% if (goods.getCategoryName() != null) { %>
            <span class="goods-category"><%=
                    HtmlUtils.escape(goods.getCategoryName())
            %></span>
            <% } %>
        </a>
        <div class="goods-card-body">
            <div class="goods-title-row">
                <a class="goods-title"
                   href="<%= marketContextPath %>/goods/detail?id=<%=
                            goods.getId()
                   %>"><%= HtmlUtils.escape(goods.getTitle()) %></a>
                <button type="button"
                        class="goods-favorite-btn <%=
                                goods.isFavorited() ? "saved" : ""
                        %>"
                        data-goods-action="favorite"
                        aria-pressed="<%= goods.isFavorited() %>"
                        aria-label="收藏商品">
                    <svg><use href="#icon-bookmark"></use></svg>
                    <span><%= goods.getFavoriteCount() %></span>
                </button>
            </div>
            <strong class="goods-price">¥<%=
                    priceFormatter.format(goods.getPrice())
            %></strong>
            <div class="goods-meta">
                <span><%= HtmlUtils.escape(
                        goods.getConditionLevel() == null
                                ? "成色未填写"
                                : goods.getConditionLevel()
                ) %></span>
                <span><%= HtmlUtils.escape(
                        goods.getTradePlace() == null
                                ? "地点待定"
                                : goods.getTradePlace()
                ) %></span>
            </div>
            <span class="trade-method-badge <%= goods.getTradeMethod() %>"><%=
                    HtmlUtils.escape(goods.getTradeMethodText())
            %></span>
            <div class="goods-seller">
                <span class="goods-seller-avatar">
                    <% if (goods.getSellerAvatar() != null
                            && !goods.getSellerAvatar().isBlank()) { %>
                    <img src="<%= marketContextPath %>/<%=
                            HtmlUtils.escape(goods.getSellerAvatar())
                    %>" alt="">
                    <% } else { %>
                    <%= HtmlUtils.escape(goods.getSellerInitial()) %>
                    <% } %>
                </span>
                <div>
                    <strong><%=
                            HtmlUtils.escape(goods.getSellerNickname())
                    %></strong>
                    <small><%= goods.getCreatedAt() == null
                            ? ""
                            : goods.getCreatedAt().format(goodsDateFormatter)
                    %></small>
                </div>
            </div>
            <div class="goods-actions">
                <% if (owner) { %>
                <button type="button"
                        data-goods-action="edit">编辑</button>
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
                    </select>
                </label>
                <button type="button"
                        class="goods-delete-btn"
                        data-goods-action="delete">下架</button>
                <% } %>
                <a class="goods-detail-btn"
                   href="<%= marketContextPath %>/goods/detail?id=<%=
                            goods.getId()
                   %>">查看详情</a>
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
                <span>SECOND-HAND MARKET</span>
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
                <input type="text"
                       name="title"
                       maxlength="150"
                       required>
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
                           placeholder="如：九成新"
                           required>
                </label>
                <label>交易地点
                    <input type="text"
                           name="tradePlace"
                           maxlength="150"
                           placeholder="如：图书馆门口">
                </label>
            </div>
            <div class="goods-form-row">
                <label>交易方式
                    <select name="tradeMethod" required>
                        <option value="offline" selected>线下交易</option>
                        <option value="online">线上付款</option>
                        <option value="both">线上/线下均可</option>
                    </select>
                </label>
                <label>联系方式
                    <input type="text"
                           name="contact"
                           maxlength="100"
                           placeholder="手机号、微信等"
                           required>
                </label>
            </div>
            <label>商品描述
                <textarea name="description"
                          rows="6"
                          required
                          placeholder="介绍物品情况、购买时间和使用痕迹"></textarea>
            </label>
            <div class="goods-form-row">
                <label>图片路径
                    <input type="text"
                           name="images"
                           placeholder="可为空，多个路径用逗号分隔">
                </label>
                <p class="goods-trade-hint" data-trade-place-hint>
                    线下交易建议填写明确的交易地点。
                </p>
            </div>
            <p class="goods-form-error" data-goods-form-error hidden></p>
            <div class="goods-modal-actions">
                <button type="button" data-close-goods-modal>取消</button>
                <button type="submit" class="primary-btn">确认发布</button>
            </div>
        </form>
    </section>
</div>
