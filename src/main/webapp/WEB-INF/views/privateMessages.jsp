<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.PrivateConversation" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%-- 渲染私信列表页面，输出服务端数据与前端交互所需标记。 --%>
<%
    String contextPath = request.getContextPath();
    List<PrivateConversation> conversations =
            (List<PrivateConversation>) request.getAttribute("conversations");
    Integer privateUnreadCount =
            (Integer) request.getAttribute("privateUnreadCount");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的私信 - CampusHub</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/private-messages.css?v=20260611-1">
</head>
<body class="private-message-body">
<header class="simple-topbar">
    <a class="brand" href="<%= contextPath %>/home#messages">
        <span class="brand-mark">
            <img src="<%= contextPath %>/images/Logo_noword.png" alt="CampusHub">
        </span>
        <span class="brand-copy">
            <strong>CampusHub</strong><small>校园综合社区</small>
        </span>
    </a>
    <a class="back-link" href="<%= contextPath %>/home#messages">返回消息中心</a>
</header>
<main class="private-message-shell">
    <section class="private-message-heading card">
        <div>
            <span>PRIVATE MESSAGES</span>
            <h1>我的私信</h1>
            <p>与校园用户的一对一会话</p>
        </div>
        <strong><%= privateUnreadCount == null ? 0 : privateUnreadCount %> 条未读</strong>
    </section>

    <section class="private-conversation-list">
        <% if (conversations == null || conversations.isEmpty()) { %>
        <div class="private-message-empty card">
            <h2>暂无私信会话</h2>
            <p>可从商品、失物招领或活动详情页联系发布者。</p>
        </div>
        <% } else {
            for (PrivateConversation conversation : conversations) {
                LocalDateTime displayTime = conversation.lastMessageAt() == null
                        ? conversation.createdAt()
                        : conversation.lastMessageAt();
                String avatar = conversation.otherAvatar();
        %>
        <a class="private-conversation card"
           href="<%= contextPath %>/private-messages/thread?conversationId=<%=
                    conversation.id()
           %>">
            <span class="private-conversation-avatar">
                <% if (avatar != null && !avatar.isBlank()) { %>
                <img src="<%= contextPath %><%= HtmlUtils.escape(
                        HtmlUtils.resourcePath(avatar)
                ) %>" alt="">
                <% } else { %>
                <%= HtmlUtils.escape(
                        conversation.otherNickname() == null
                                || conversation.otherNickname().isBlank()
                                ? "U"
                                : conversation.otherNickname().substring(0, 1)
                ) %>
                <% } %>
            </span>
            <span class="private-conversation-content">
                <span class="private-conversation-line">
                    <strong><%= HtmlUtils.escape(
                            conversation.otherNickname() == null
                                    ? "校园用户"
                                    : conversation.otherNickname()
                    ) %></strong>
                    <time><%= displayTime == null
                            ? ""
                            : displayTime.format(formatter) %></time>
                </span>
                <span class="private-conversation-preview"><%= HtmlUtils.escape(
                        conversation.lastMessage() == null
                                ? "会话已创建，发送第一条消息吧"
                                : conversation.lastMessage()
                ) %></span>
            </span>
            <% if (conversation.unreadCount() > 0) { %>
            <span class="private-unread-count"><%=
                    conversation.unreadCount() > 99
                            ? "99+"
                            : conversation.unreadCount()
            %></span>
            <% } %>
        </a>
        <%  }
           } %>
    </section>
</main>
</body>
</html>
