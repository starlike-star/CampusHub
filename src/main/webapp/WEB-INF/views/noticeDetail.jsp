<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.Notice" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    String contextPath = request.getContextPath();
    Notice notice = (Notice) request.getAttribute("notice");
    String noticeError = (String) request.getAttribute("noticeError");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String typeText = "系统公告";
    String typeClass = "system";
    if (notice != null) {
        if ("teaching".equals(notice.getType())) {
            typeText = "教务公告";
            typeClass = "teaching";
        } else if ("life".equals(notice.getType())) {
            typeText = "生活公告";
            typeClass = "life";
        } else if ("activity".equals(notice.getType())) {
            typeText = "活动公告";
            typeClass = "activity";
        } else if ("urgent".equals(notice.getType())) {
            typeText = "紧急公告";
            typeClass = "urgent";
        }
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= notice == null
            ? "公告详情"
            : HtmlUtils.escape(notice.getTitle()) %> - CampusHub</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/notice-detail.css">
</head>
<body class="notice-detail-body">
<header class="notice-topbar">
    <a class="brand" href="<%= contextPath %>/home">
        <span class="brand-mark">
            <img src="<%= contextPath %>/images/Logo_noword.png" alt="CampusHub">
        </span>
        <span class="brand-copy">
            <strong>CampusHub</strong><small>校园综合社区</small>
        </span>
    </a>
    <a class="notice-back-link"
       href="<%= contextPath %>/home#square?tab=notice">返回校园公告</a>
</header>

<main class="notice-detail-page">
    <% if (notice == null) { %>
    <section class="notice-unavailable card">
        <span>NOTICE</span>
        <h1><%= HtmlUtils.escape(noticeError) %></h1>
        <p>该公告可能不存在、已被隐藏，或访问参数无效。</p>
        <a href="<%= contextPath %>/home#square?tab=notice">返回公告列表</a>
    </section>
    <% } else { %>
    <article class="notice-detail-card card">
        <div class="notice-detail-heading">
            <div class="notice-badges">
                <span class="notice-detail-type <%= typeClass %>"><%=
                        typeText
                %></span>
                <% if (notice.isTop()) { %>
                <span class="notice-detail-top">置顶</span>
                <% } %>
            </div>
            <h1><%= HtmlUtils.escape(notice.getTitle()) %></h1>
        </div>

        <dl class="notice-detail-meta">
            <div>
                <dt>发布时间</dt>
                <dd><%= notice.getCreatedAt() == null
                        ? "-"
                        : notice.getCreatedAt().format(formatter) %></dd>
            </div>
            <div>
                <dt>更新时间</dt>
                <dd><%= notice.getUpdatedAt() == null
                        ? "-"
                        : notice.getUpdatedAt().format(formatter) %></dd>
            </div>
            <% if (notice.getPublisherName() != null
                    && !notice.getPublisherName().isBlank()) { %>
            <div>
                <dt>发布人</dt>
                <dd><%= HtmlUtils.escape(notice.getPublisherName()) %></dd>
            </div>
            <% } %>
        </dl>

        <div class="notice-detail-content"><%=
                HtmlUtils.escape(notice.getContent())
        %></div>

        <div class="notice-detail-actions">
            <a href="<%= contextPath %>/home#square?tab=notice">返回公告列表</a>
        </div>
    </article>
    <% } %>
</main>
</body>
</html>
