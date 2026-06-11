<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.PrivateConversation" %>
<%@ page import="cn.campushub.model.PrivateMessage" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String contextPath = request.getContextPath();
    PrivateConversation conversation =
            (PrivateConversation) request.getAttribute("conversation");
    List<PrivateMessage> privateMessages =
            (List<PrivateMessage>) request.getAttribute("privateMessages");
    Long currentUserId = (Long) request.getAttribute("currentUserId");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="<%= contextPath %>">
    <title>与 <%= HtmlUtils.escape(conversation.otherNickname()) %> 的私信 - CampusHub</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/private-messages.css">
</head>
<body class="private-message-body">
<header class="simple-topbar">
    <a class="brand" href="<%= contextPath %>/private-messages">
        <span class="brand-mark">
            <img src="<%= contextPath %>/images/Logo_noword.png" alt="CampusHub">
        </span>
        <span class="brand-copy">
            <strong>CampusHub</strong><small>校园综合社区</small>
        </span>
    </a>
    <a class="back-link" href="<%= contextPath %>/private-messages">返回会话列表</a>
</header>
<main class="private-message-shell">
    <section class="private-thread card">
        <header class="private-thread-header">
            <div>
                <span>PRIVATE CHAT</span>
                <h1><%= HtmlUtils.escape(
                        conversation.otherNickname() == null
                                ? "校园用户"
                                : conversation.otherNickname()
                ) %></h1>
            </div>
            <button type="button" onclick="window.location.reload()">刷新消息</button>
        </header>

        <div class="private-thread-messages" data-private-thread-messages>
            <% if (privateMessages == null || privateMessages.isEmpty()) { %>
            <p class="private-thread-empty">还没有消息，发送第一条私信吧。</p>
            <% } else {
                for (PrivateMessage message : privateMessages) {
                    boolean mine = message.senderId() == currentUserId;
            %>
            <article class="private-message-bubble <%= mine ? "mine" : "theirs" %>">
                <p><%= HtmlUtils.escape(message.content()) %></p>
                <time><%= message.createdAt() == null
                        ? ""
                        : message.createdAt().format(formatter) %></time>
            </article>
            <%  }
               } %>
        </div>

        <form class="private-message-form"
              data-private-message-form
              action="<%= contextPath %>/private-messages/send"
              method="post">
            <input type="hidden"
                   name="receiverId"
                   value="<%= conversation.otherUserId() %>">
            <label for="privateMessageContent">发送私信</label>
            <textarea id="privateMessageContent"
                      name="content"
                      rows="4"
                      maxlength="1000"
                      required
                      placeholder="输入 1 到 1000 字"></textarea>
            <div>
                <span data-private-message-count>0 / 1000</span>
                <button type="submit" class="primary-btn">发送</button>
            </div>
            <p class="private-message-error" data-private-message-error hidden></p>
        </form>
    </section>
</main>
<script src="<%= contextPath %>/js/private-messages.js"></script>
</body>
</html>
