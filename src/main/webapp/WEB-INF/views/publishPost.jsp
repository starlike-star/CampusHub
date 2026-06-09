<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.Category" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.util.List" %>
<%
    String contextPath = request.getContextPath();
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String selectedCategory = (String) request.getAttribute("categoryId");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>发布帖子 - CampusHub</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/post.css">
</head>
<body>
<header class="simple-topbar">
    <a class="brand" href="<%= contextPath %>/home">
        <span class="brand-mark"><img src="<%= contextPath %>/images/Logo_noword.png" alt="CampusHub"></span>
        <span class="brand-copy"><strong>CampusHub</strong><small>校园综合社区</small></span>
    </a>
    <a class="back-link" href="<%= contextPath %>/home">返回首页</a>
</header>
<main class="form-page">
    <section class="form-card card">
        <div class="form-heading">
            <span>校园动态</span>
            <h1>发布帖子</h1>
            <p>分享校园见闻、学习交流和生活经验。</p>
        </div>
        <% if (errorMessage != null) { %>
        <div class="form-error"><%= HtmlUtils.escape(errorMessage) %></div>
        <% } %>
        <form action="<%= contextPath %>/post/publish" method="post">
            <label>标题
                <input type="text" name="title" maxlength="150" required
                       value="<%= HtmlUtils.escape((String) request.getAttribute("title")) %>"
                       placeholder="请输入帖子标题">
            </label>
            <div class="form-row">
                <label>分类
                    <select name="category_id" required>
                        <option value="">请选择分类</option>
                        <% if (categories != null) {
                            for (Category category : categories) {
                                String id = String.valueOf(category.getId());
                        %>
                        <option value="<%= id %>" <%= id.equals(selectedCategory) ? "selected" : "" %>>
                            <%= HtmlUtils.escape(category.getName()) %>
                        </option>
                        <%  }
                           } %>
                    </select>
                </label>
                <label>话题
                    <input type="text" name="topic" maxlength="100"
                           value="<%= HtmlUtils.escape((String) request.getAttribute("topic")) %>"
                           placeholder="例如：校园生活">
                </label>
            </div>
            <label>内容
                <textarea name="content" rows="12" required
                          placeholder="请输入帖子完整内容"><%= HtmlUtils.escape((String) request.getAttribute("content")) %></textarea>
            </label>
            <p class="form-hint">图片上传将在后续版本开放，本版 images 字段保持为空。</p>
            <div class="form-actions">
                <a href="<%= contextPath %>/home">取消</a>
                <button class="primary-btn" type="submit">发布帖子</button>
            </div>
        </form>
    </section>
</main>
<script src="<%= contextPath %>/js/textarea-autosize.js"></script>
</body>
</html>
