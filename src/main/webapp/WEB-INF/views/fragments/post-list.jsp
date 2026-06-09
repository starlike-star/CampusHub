<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.Post" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String postContextPath = request.getContextPath();
    SessionUser postLoginUser =
            (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
    List<Post> fragmentPosts = (List<Post>) request.getAttribute("posts");
    String emptyTitle = (String) request.getAttribute("emptyTitle");
    String emptyMessage = (String) request.getAttribute("emptyMessage");
    DateTimeFormatter postDateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>
<div class="square-list">
<% if (fragmentPosts == null || fragmentPosts.isEmpty()) { %>
    <section class="empty-state card">
        <h2><%= HtmlUtils.escape(
                emptyTitle == null ? "暂时没有匹配的内容" : emptyTitle
        ) %></h2>
        <p><%= HtmlUtils.escape(
                emptyMessage == null ? "换个筛选条件或搜索词试试。" : emptyMessage
        ) %></p>
        <a class="primary-btn publish-link"
           href="<%= postContextPath %>/post/publish">发布帖子</a>
    </section>
<% } else {
    for (Post post : fragmentPosts) {
        String college = post.getAuthorCollege() == null
                ? "学院未填写"
                : post.getAuthorCollege();
        String grade = post.getAuthorGrade() == null
                ? "年级未填写"
                : post.getAuthorGrade();
        boolean owner =
                postLoginUser != null && postLoginUser.id() == post.getUserId();
%>
    <article class="post-card card" data-post-id="<%= post.getId() %>">
        <textarea class="post-raw-content" hidden><%=
                HtmlUtils.escape(post.getContent())
        %></textarea>
        <div class="post-header">
            <span class="avatar avatar-blue post-avatar">
                <% if (post.getAuthorAvatar() != null
                        && !post.getAuthorAvatar().isBlank()) { %>
                <img src="<%= postContextPath %>/<%=
                        HtmlUtils.escape(post.getAuthorAvatar())
                %>" alt="<%= HtmlUtils.escape(post.getAuthorNickname()) %>">
                <% } else { %>
                <%= HtmlUtils.escape(post.getAuthorInitial()) %>
                <% } %>
            </span>
            <div class="post-author">
                <div>
                    <strong><%=
                            HtmlUtils.escape(post.getAuthorNickname())
                    %></strong>
                </div>
                <p>
                    <%= HtmlUtils.escape(college) %> ·
                    <%= HtmlUtils.escape(grade) %>
                    <span>·</span>
                    <%= post.getCreatedAt() == null
                            ? ""
                            : post.getCreatedAt().format(postDateFormatter) %>
                </p>
            </div>
            <span class="category-badge"><%=
                    HtmlUtils.escape(post.getCategoryName())
            %></span>
            <% if (owner) { %>
            <div class="post-owner-actions">
                <button type="button"
                        class="post-action edit-post-btn"
                        data-post-id="<%= post.getId() %>"
                        data-category-id="<%= post.getCategoryId() %>">编辑</button>
                <button type="button"
                        class="post-action delete-post-btn"
                        data-post-id="<%= post.getId() %>">删除</button>
            </div>
            <% } %>
        </div>

        <div class="post-click-area"
             role="link"
             tabindex="0"
             data-detail-url="<%= postContextPath %>/post/detail?id=<%=
                    post.getId()
             %>">
            <div class="post-body">
                <span class="topic-tag post-topic <%=
                        post.getTopic() == null ? "is-empty" : ""
                %>"><%= post.getTopic() == null
                        ? ""
                        : "#" + HtmlUtils.escape(post.getTopic()) %></span>
                <h2 class="post-title"><%=
                        HtmlUtils.escape(post.getTitle())
                %></h2>
                <p class="post-summary"><%=
                        HtmlUtils.escape(post.getSummary())
                %></p>
                <% if (!post.getImageList().isEmpty()) { %>
                <div class="database-photo-grid">
                    <% for (String image : post.getImageList()) { %>
                    <img src="<%= postContextPath %>/<%=
                            HtmlUtils.escape(image)
                    %>" alt="帖子图片" loading="lazy">
                    <% } %>
                </div>
                <% } %>
                <button type="button" class="view-detail-btn">查看详情</button>
            </div>
        </div>

        <div class="post-actions">
            <button type="button"
                    class="post-action like-btn <%=
                            post.isLiked() ? "liked" : ""
                    %>"
                    data-post-id="<%= post.getId() %>"
                    aria-pressed="<%= post.isLiked() %>">
                <svg><use href="#icon-heart"></use></svg>
                <span class="like-count"><%= post.getLikeCount() %></span>
            </button>
            <button type="button"
                    class="post-action comment-toggle-btn"
                    data-post-id="<%= post.getId() %>">
                <svg><use href="#icon-comment"></use></svg>
                <span class="comment-count"><%= post.getCommentCount() %></span>
            </button>
            <button type="button"
                    class="post-action favorite-btn <%=
                            post.isFavorited() ? "saved" : ""
                    %>"
                    data-post-id="<%= post.getId() %>"
                    aria-pressed="<%= post.isFavorited() %>">
                <svg><use href="#icon-bookmark"></use></svg>
                <span class="favorite-count-value"><%=
                        post.getFavoriteCount()
                %></span>
            </button>
            <span class="view-count">
                <svg><use href="#icon-eye"></use></svg>
                <%= post.getViewCount() %>
            </span>
        </div>

        <section class="quick-comment-panel" hidden>
            <form class="quick-comment-form">
                <textarea name="content"
                          maxlength="2000"
                          rows="3"
                          placeholder="写下你的评论"></textarea>
                <div class="quick-comment-actions">
                    <button type="button"
                            class="cancel-comment-btn">取消</button>
                    <button type="submit"
                            class="quick-comment-submit">发送</button>
                </div>
            </form>
            <div class="quick-comment-list"></div>
        </section>
    </article>
<%  }
   } %>
</div>
