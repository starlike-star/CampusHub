<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.Goods" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.List" %>
<%
    String favoritesContextPath = request.getContextPath();
    boolean loginRequired =
            Boolean.TRUE.equals(request.getAttribute("loginRequired"));
    List<Goods> goodsList =
            (List<Goods>) request.getAttribute("goodsList");
    DecimalFormat favoritePriceFormatter = new DecimalFormat("0.00");
%>
<section class="market-header card">
    <div>
        <span class="market-eyebrow">MY FAVORITES</span>
        <h1>我的收藏</h1>
        <p>查看已收藏的二手商品</p>
    </div>
</section>

<% if (loginRequired) { %>
<section class="empty-state card">
    <h2>登录后查看收藏</h2>
    <p>登录后可以收藏并集中查看感兴趣的商品。</p>
    <a class="primary-btn"
       href="<%= favoritesContextPath %>/login">去登录</a>
</section>
<% } else if (goodsList == null || goodsList.isEmpty()) { %>
<section class="empty-state card">
    <h2>还没有收藏商品</h2>
    <p>去二手市场看看校园闲置好物吧。</p>
    <a class="primary-btn"
       href="#market"
       data-route="market">浏览二手市场</a>
</section>
<% } else { %>
<div class="goods-grid">
    <% for (Goods goods : goodsList) {
        String statusText = "在售";
        if ("reserved".equals(goods.getStatus())) {
            statusText = "已预订";
        } else if ("sold".equals(goods.getStatus())) {
            statusText = "已售出";
        }
    %>
    <article class="goods-card card" data-goods-id="<%= goods.getId() %>">
        <a class="goods-image"
           href="<%= favoritesContextPath %>/goods/detail?id=<%=
                    goods.getId()
           %>">
            <img src="<%= favoritesContextPath %>/<%=
                    HtmlUtils.escape(goods.getFirstImage())
            %>"
                 onerror="this.onerror=null;this.src='<%= favoritesContextPath %>/images/default-goods.png';"
                 alt="<%= HtmlUtils.escape(goods.getTitle()) %>">
            <span class="goods-status <%= goods.getStatus() %>"><%=
                    statusText
            %></span>
            <span class="goods-category"><%=
                    HtmlUtils.escape(goods.getCategoryName())
            %></span>
        </a>
        <div class="goods-card-body">
            <div class="goods-title-row">
                <a class="goods-title"
                   href="<%= favoritesContextPath %>/goods/detail?id=<%=
                            goods.getId()
                   %>"><%= HtmlUtils.escape(goods.getTitle()) %></a>
                <button type="button"
                        class="goods-favorite-btn saved"
                        data-goods-action="favorite"
                        aria-pressed="true"
                        aria-label="取消收藏">
                    <svg><use href="#icon-bookmark"></use></svg>
                    <span><%= goods.getFavoriteCount() %></span>
                </button>
            </div>
            <strong class="goods-price">¥<%=
                    favoritePriceFormatter.format(goods.getPrice())
            %></strong>
            <div class="goods-meta">
                <span><%= HtmlUtils.escape(goods.getConditionLevel()) %></span>
                <span><%= HtmlUtils.escape(
                        goods.getTradePlace() == null
                                ? "地点待定"
                                : goods.getTradePlace()
                ) %></span>
            </div>
            <span class="trade-method-badge <%= goods.getTradeMethod() %>"><%=
                    HtmlUtils.escape(goods.getTradeMethodText())
            %></span>
            <div class="goods-actions">
                <a class="goods-detail-btn"
                   href="<%= favoritesContextPath %>/goods/detail?id=<%=
                            goods.getId()
                   %>">查看详情</a>
            </div>
        </div>
    </article>
    <% } %>
</div>
<% } %>
