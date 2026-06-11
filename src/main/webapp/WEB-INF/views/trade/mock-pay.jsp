<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.GoodsOrder" %>
<%@ page import="cn.campushub.model.TradeOrderResult" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.text.DecimalFormat" %>
<%-- 渲染模拟支付页面，输出服务端数据与前端交互所需标记。 --%>
<%
    GoodsOrder order = (GoodsOrder) request.getAttribute("order");
    TradeOrderResult payResult =
            (TradeOrderResult) request.getAttribute("payResult");
    DecimalFormat amountFormatter = new DecimalFormat("0.00");
    String token = order == null ? request.getParameter("token") : order.getPayToken();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CampusHub 模拟支付</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/trade.css">
</head>
<body class="mock-pay-page">
<main class="mock-pay-card">
    <span class="mock-pay-brand">CampusHub 模拟支付</span>
    <h1>模拟微信扫码确认支付</h1>
    <p class="mock-pay-disclaimer">
        本页面仅用于课设演示，不是微信官方支付，不涉及真实资金。
    </p>
    <% if (order != null) { %>
    <dl class="mock-pay-details">
        <div><dt>商品</dt><dd><%= HtmlUtils.escape(order.getGoodsTitle()) %></dd></div>
        <div><dt>金额</dt><dd>¥<%= amountFormatter.format(order.getAmount()) %></dd></div>
        <div><dt>订单编号</dt><dd><%= HtmlUtils.escape(order.getOrderNo()) %></dd></div>
    </dl>
    <% } %>
    <% if (payResult != null) { %>
    <section class="mock-pay-result <%= payResult.success() ? "success" : "error" %>">
        <h2><%= payResult.success() ? "模拟支付成功" : "无法完成模拟支付" %></h2>
        <p><%= HtmlUtils.escape(payResult.message()) %></p>
        <% if (payResult.success()) { %>
        <small>电脑端将在轮询到订单状态后自动更新。</small>
        <% } %>
    </section>
    <% } else if (order != null && "paid".equals(order.getStatus())) { %>
    <section class="mock-pay-result success">
        <h2>订单已支付</h2>
        <p>无需重复确认，电脑端会自动更新。</p>
    </section>
    <% } else if (order != null && "pending_payment".equals(order.getStatus())) { %>
    <form method="post"
          action="<%= request.getContextPath() %>/trade/mock-pay/confirm">
        <input type="hidden" name="token" value="<%= HtmlUtils.escape(token) %>">
        <button type="submit">确认模拟支付</button>
    </form>
    <% } else { %>
    <section class="mock-pay-result error">
        <h2>订单不可支付</h2>
        <p>当前订单状态：<%= order == null ? "未知" : HtmlUtils.escape(order.getStatus()) %></p>
    </section>
    <% } %>
</main>
</body>
</html>
