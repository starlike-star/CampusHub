<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.PublicUserProfile" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%-- 渲染公开用户主页页面，输出服务端数据与前端交互所需标记。 --%>
<%
    String contextPath = request.getContextPath();
    PublicUserProfile publicUser =
            (PublicUserProfile) request.getAttribute("publicUser");
    boolean viewingSelf =
            Boolean.TRUE.equals(request.getAttribute("viewingSelf"));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String nickname = publicUser.nickname() == null
            ? "校园用户"
            : publicUser.nickname();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= HtmlUtils.escape(nickname) %> - CampusHub</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/public-user-profile.css">
</head>
<body class="public-user-body">
<header class="simple-topbar">
    <a class="brand" href="<%= contextPath %>/home">
        <span class="brand-mark">
            <img src="<%= contextPath %>/images/Logo_noword.png" alt="CampusHub">
        </span>
        <span class="brand-copy">
            <strong>CampusHub</strong><small>校园综合社区</small>
        </span>
    </a>
    <a class="back-link" href="javascript:history.back()">返回上一页</a>
</header>
<main class="public-user-shell">
    <section class="public-user-card card">
        <span class="public-user-avatar">
            <% if (publicUser.avatar() != null
                    && !publicUser.avatar().isBlank()) { %>
            <img src="<%= contextPath %><%= HtmlUtils.escape(
                    HtmlUtils.resourcePath(publicUser.avatar())
            ) %>" alt="<%= HtmlUtils.escape(nickname) %>">
            <% } else { %>
            <%= HtmlUtils.escape(
                    nickname.isBlank() ? "U" : nickname.substring(0, 1)
            ) %>
            <% } %>
        </span>
        <div class="public-user-summary">
            <span>USER PROFILE</span>
            <h1><%= HtmlUtils.escape(nickname) %></h1>
            <p><%= HtmlUtils.escape(
                    publicUser.college() == null
                            ? "学院未填写"
                            : publicUser.college()
            ) %></p>
        </div>
        <% if (!viewingSelf) { %>
        <a class="primary-btn"
           href="<%= contextPath %>/private-messages/thread?receiverId=<%=
                    publicUser.id()
           %>">发送私信</a>
        <% } %>
    </section>

    <section class="public-user-details card">
        <h2>用户信息</h2>
        <dl>
            <div>
                <dt>学院</dt>
                <dd><%= HtmlUtils.escape(
                        publicUser.college() == null
                                ? "未填写"
                                : publicUser.college()
                ) %></dd>
            </div>
            <div>
                <dt>专业</dt>
                <dd><%= HtmlUtils.escape(
                        publicUser.major() == null
                                ? "未填写"
                                : publicUser.major()
                ) %></dd>
            </div>
            <div>
                <dt>年级</dt>
                <dd><%= HtmlUtils.escape(
                        publicUser.grade() == null
                                ? "未填写"
                                : publicUser.grade()
                ) %></dd>
            </div>
            <div>
                <dt>加入时间</dt>
                <dd><%= publicUser.createdAt() == null
                        ? ""
                        : publicUser.createdAt().format(dateFormatter) %></dd>
            </div>
        </dl>
    </section>
</main>
</body>
</html>
