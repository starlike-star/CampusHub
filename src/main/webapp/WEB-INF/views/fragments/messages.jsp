<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.Message" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String messagesContextPath = request.getContextPath();
    boolean messagesLoginRequired =
            Boolean.TRUE.equals(request.getAttribute("loginRequired"));
    String activeTab = (String) request.getAttribute("activeTab");
    if (activeTab == null) {
        activeTab = "all";
    }
    List<Message> messages =
            (List<Message>) request.getAttribute("messages");
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    Integer messageCount = (Integer) request.getAttribute("messageCount");
    DateTimeFormatter messageDateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>
<% if (messagesLoginRequired) { %>
<section class="messages-empty card">
    <span>MESSAGE CENTER</span>
    <h1>登录后查看消息</h1>
    <p>评论、点赞、收藏和系统通知会集中显示在这里。</p>
    <a class="primary-btn" href="<%= messagesContextPath %>/login">去登录</a>
</section>
<% } else { %>
<div class="messages-page">
    <section class="messages-header card">
        <div>
            <span>MESSAGE CENTER</span>
            <h1>消息中心</h1>
            <p>查看与你相关的评论、点赞、收藏和系统通知</p>
        </div>
        <button type="button"
                class="mark-all-read-btn"
                data-message-read-all
                data-message-type="<%= "all".equals(activeTab)
                        ? ""
                        : activeTab %>"
                <%= unreadCount == null || unreadCount == 0
                        ? "disabled"
                        : "" %>>全部标记已读</button>
    </section>

    <section class="messages-stats">
        <article class="card">
            <span>未读消息</span>
            <strong data-message-page-unread><%=
                    unreadCount == null ? 0 : unreadCount
            %></strong>
        </article>
        <article class="card">
            <span>全部消息</span>
            <strong><%= messageCount == null ? 0 : messageCount %></strong>
        </article>
    </section>

    <nav class="messages-tabs card" aria-label="消息类型">
        <a href="#messages?tab=all"
           data-route="messages"
           data-tab="all"
           class="<%= "all".equals(activeTab) ? "active" : "" %>">全部</a>
        <a href="#messages?tab=comment"
           data-route="messages"
           data-tab="comment"
           class="<%= "comment".equals(activeTab) ? "active" : "" %>">评论</a>
        <a href="#messages?tab=like"
           data-route="messages"
           data-tab="like"
           class="<%= "like".equals(activeTab) ? "active" : "" %>">点赞</a>
        <a href="#messages?tab=favorite"
           data-route="messages"
           data-tab="favorite"
           class="<%= "favorite".equals(activeTab) ? "active" : "" %>">收藏</a>
        <a href="#messages?tab=system"
           data-route="messages"
           data-tab="system"
           class="<%= "system".equals(activeTab) ? "active" : "" %>">系统</a>
    </nav>

    <section class="message-list">
        <% if (messages == null || messages.isEmpty()) { %>
        <div class="messages-empty card">
            <h2>暂无消息</h2>
            <p>有新的评论、点赞或收藏时，会出现在这里。</p>
        </div>
        <% } else {
            for (Message message : messages) {
                String typeText = switch (message.getType()) {
                    case "comment" -> "评论";
                    case "like" -> "点赞";
                    case "favorite" -> "收藏";
                    case "system" -> "系统";
                    case "activity" -> "活动";
                    case "claim" -> "认领";
                    default -> "通知";
                };
        %>
        <article class="message-card card <%=
                message.isRead() ? "message-read" : "message-unread"
        %>"
                 data-message-card
                 data-message-id="<%= message.getId() %>">
            <span class="unread-dot" aria-hidden="true"></span>
            <div class="message-card-content">
                <div class="message-card-heading">
                    <span class="message-type-badge <%= message.getType() %>"><%=
                            typeText
                    %></span>
                    <time><%= message.getCreatedAt() == null
                            ? ""
                            : message.getCreatedAt().format(
                                    messageDateFormatter
                            ) %></time>
                </div>
                <h2><%= HtmlUtils.escape(message.getTitle()) %></h2>
                <p><%= HtmlUtils.escape(message.getContent()) %></p>
            </div>
            <% if (!message.isRead()) { %>
            <button type="button"
                    class="message-read-btn"
                    data-message-read>标记已读</button>
            <% } %>
        </article>
        <%  }
           } %>
    </section>
</div>
<% } %>
