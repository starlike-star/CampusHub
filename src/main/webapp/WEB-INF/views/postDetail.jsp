<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.Comment" %>
<%@ page import="cn.campushub.model.Post" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String contextPath = request.getContextPath();
    Post post = (Post) request.getAttribute("post");
    List<Comment> comments = (List<Comment>) request.getAttribute("comments");
    boolean liked = Boolean.TRUE.equals(request.getAttribute("liked"));
    boolean favorited = Boolean.TRUE.equals(request.getAttribute("favorited"));
    SessionUser loginUser =
            (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
    String postMessage = (String) session.getAttribute("postMessage");
    session.removeAttribute("postMessage");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="<%= contextPath %>">
    <title><%= HtmlUtils.escape(post.getTitle()) %> - CampusHub</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/post.css">
</head>
<body>
<header class="simple-topbar">
    <a class="brand" href="<%= contextPath %>/home">
        <span class="brand-mark"><img src="<%= contextPath %>/images/Logo_noword.png" alt="CampusHub"></span>
        <span class="brand-copy"><strong>CampusHub</strong><small>校园综合社区</small></span>
    </a>
    <div class="detail-top-actions">
        <a class="back-link" href="<%= contextPath %>/home">返回首页</a>
        <a class="primary-btn publish-link" href="<%= contextPath %>/post/publish">发布</a>
    </div>
</header>
<main class="detail-page">
    <article class="detail-card card">
        <div class="post-header">
            <span class="avatar avatar-blue post-avatar">
                <% if (post.getAuthorAvatar() != null && !post.getAuthorAvatar().isBlank()) { %>
                <img src="<%= contextPath %>/<%= HtmlUtils.escape(post.getAuthorAvatar()) %>"
                     alt="<%= HtmlUtils.escape(post.getAuthorNickname()) %>">
                <% } else { %>
                <%= HtmlUtils.escape(post.getAuthorInitial()) %>
                <% } %>
            </span>
            <div class="post-author">
                <div><strong><%= HtmlUtils.escape(post.getAuthorNickname()) %></strong></div>
                <p>
                    <%= HtmlUtils.escape(post.getAuthorCollege()) %> ·
                    <%= HtmlUtils.escape(post.getAuthorGrade()) %> ·
                    <%= post.getCreatedAt() == null ? "" : post.getCreatedAt().format(dateFormatter) %>
                </p>
            </div>
            <span class="category-badge"><%= HtmlUtils.escape(post.getCategoryName()) %></span>
        </div>
        <div class="detail-content">
            <% if (post.getTopic() != null && !post.getTopic().isBlank()) { %>
            <span class="topic-tag">#<%= HtmlUtils.escape(post.getTopic()) %></span>
            <% } %>
            <h1><%= HtmlUtils.escape(post.getTitle()) %></h1>
            <div class="post-full-content"><%= HtmlUtils.escape(post.getContent()) %></div>
            <% if (!post.getImageList().isEmpty()) { %>
            <div class="database-photo-grid">
                <% for (String image : post.getImageList()) { %>
                <img src="<%= contextPath %>/<%= HtmlUtils.escape(image) %>" alt="帖子图片">
                <% } %>
            </div>
            <% } %>
        </div>
        <div class="detail-stats">
            <button class="interaction-button detail-like-btn <%= liked ? "liked" : "" %>"
                    type="button"
                    data-post-id="<%= post.getId() %>"
                    aria-pressed="<%= liked %>">
                <span class="detail-like-label"><%= liked ? "取消点赞" : "点赞" %></span>
                · <span class="detail-like-count"><%= post.getLikeCount() %></span>
            </button>
            <span>评论 <strong><%= post.getCommentCount() %></strong></span>
            <button class="interaction-button detail-favorite-btn <%=
                            favorited ? "saved" : ""
                    %>"
                    type="button"
                    data-post-id="<%= post.getId() %>"
                    aria-pressed="<%= favorited %>">
                <span class="detail-favorite-label"><%=
                        favorited ? "取消收藏" : "收藏"
                %></span>
                · <span class="detail-favorite-count"><%=
                        post.getFavoriteCount()
                %></span>
            </button>
            <span>浏览 <strong><%= post.getViewCount() %></strong></span>
        </div>
    </article>

    <section class="comments-card card" id="comments">
        <div class="comments-heading">
            <h2>评论</h2><span><%= comments == null ? 0 : comments.size() %> 条</span>
        </div>
        <% if (postMessage != null) { %>
        <div class="form-error"><%= HtmlUtils.escape(postMessage) %></div>
        <% } %>
        <% if (loginUser != null) { %>
        <form class="comment-form detail-comment-form" data-post-id="<%= post.getId() %>">
            <textarea name="content" rows="4" maxlength="2000" required placeholder="写下你的评论"></textarea>
            <button class="primary-btn" type="submit">发表评论</button>
        </form>
        <% } else { %>
        <div class="login-prompt">
            登录后可以发表评论。<a href="<%= contextPath %>/login">去登录</a>
        </div>
        <% } %>

        <div class="comment-list">
            <% if (comments == null || comments.isEmpty()) { %>
            <p class="empty-comments">还没有评论，来发表第一条评论吧。</p>
            <% } else {
                for (Comment comment : comments) {
            %>
            <article class="comment-item">
                <span class="avatar avatar-purple post-avatar">
                    <% if (comment.getAuthorAvatar() != null && !comment.getAuthorAvatar().isBlank()) { %>
                    <img src="<%= contextPath %>/<%= HtmlUtils.escape(comment.getAuthorAvatar()) %>"
                         alt="<%= HtmlUtils.escape(comment.getAuthorNickname()) %>">
                    <% } else { %>
                    <%= HtmlUtils.escape(comment.getAuthorInitial()) %>
                    <% } %>
                </span>
                <div>
                    <header>
                        <strong><%= HtmlUtils.escape(comment.getAuthorNickname()) %></strong>
                        <span><%= HtmlUtils.escape(comment.getAuthorCollege()) %> · <%= HtmlUtils.escape(comment.getAuthorGrade()) %></span>
                        <time><%= comment.getCreatedAt() == null ? "" : comment.getCreatedAt().format(dateFormatter) %></time>
                    </header>
                    <p><%= HtmlUtils.escape(comment.getContent()) %></p>
                    <button type="button"
                            class="comment-like-btn <%=
                                    comment.isLiked() ? "active" : ""
                            %>"
                            data-comment-like
                            data-comment-id="<%= comment.getId() %>"
                            aria-pressed="<%= comment.isLiked() %>">
                        <span>点赞</span>
                        <strong><%= comment.getLikeCount() %></strong>
                    </button>
                </div>
            </article>
            <%  }
               } %>
        </div>
    </section>
</main>
<script src="<%= contextPath %>/js/textarea-autosize.js"></script>
<script src="<%= contextPath %>/js/post-detail.js"></script>
</body>
</html>
