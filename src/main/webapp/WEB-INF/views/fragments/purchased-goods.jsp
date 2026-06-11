<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.PurchasedGoodsVO" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String purchasedContextPath = request.getContextPath();
    List<PurchasedGoodsVO> purchasedGoods =
            (List<PurchasedGoodsVO>) request.getAttribute("purchasedGoods");
    DecimalFormat purchasedPriceFormatter = new DecimalFormat("0.00");
    DateTimeFormatter purchasedTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>
<section class="profile-section card">
    <div class="profile-section-heading">
        <div>
            <span>PURCHASED GOODS</span>
            <h2>我买到的商品</h2>
        </div>
        <p><%= purchasedGoods == null ? 0 : purchasedGoods.size() %> 件</p>
    </div>
    <% if (purchasedGoods == null || purchasedGoods.isEmpty()) { %>
    <div class="profile-inline-empty">
        还没有通过 CampusHub 模拟线上交易购买商品。
    </div>
    <% } else { %>
    <div class="goods-grid purchased-goods-grid">
        <% for (PurchasedGoodsVO goods : purchasedGoods) { %>
        <article class="goods-card card">
            <a class="goods-image"
               href="<%= purchasedContextPath %>/goods/detail?id=<%=
                        goods.goodsId()
               %>">
                <img src="<%= purchasedContextPath %><%=
                        goods.firstImage().startsWith("/") ? "" : "/"
                %><%= HtmlUtils.escape(goods.firstImage()) %>"
                     onerror="this.onerror=null;this.src='<%=
                            purchasedContextPath
                     %>/images/default-goods.png';"
                     alt="<%= HtmlUtils.escape(goods.title()) %>">
                <span class="goods-status sold">已买到</span>
                <% if (goods.categoryName() != null) { %>
                <span class="goods-category"><%=
                        HtmlUtils.escape(goods.categoryName())
                %></span>
                <% } %>
            </a>
            <div class="goods-card-body">
                <h3 class="goods-title"><%= HtmlUtils.escape(goods.title()) %></h3>
                <strong class="goods-price">¥<%=
                        purchasedPriceFormatter.format(goods.amount())
                %></strong>
                <span class="trade-method-badge <%= HtmlUtils.escape(
                        goods.tradeMethod()
                ) %>"><%= HtmlUtils.escape(goods.tradeMethodText()) %></span>
                <dl class="purchased-goods-meta">
                    <div>
                        <dt>卖家</dt>
                        <dd><%= HtmlUtils.escape(goods.sellerNickname()) %></dd>
                    </div>
                    <div>
                        <dt>订单编号</dt>
                        <dd><%= HtmlUtils.escape(goods.orderNo()) %></dd>
                    </div>
                    <div>
                        <dt>模拟支付时间</dt>
                        <dd><%= goods.paidAt() == null
                                ? ""
                                : goods.paidAt().format(purchasedTimeFormatter)
                        %></dd>
                    </div>
                </dl>
                <a class="goods-detail-btn"
                   href="<%= purchasedContextPath %>/goods/detail?id=<%=
                            goods.goodsId()
                   %>">查看商品详情</a>
            </div>
        </article>
        <% } %>
    </div>
    <% } %>
</section>
