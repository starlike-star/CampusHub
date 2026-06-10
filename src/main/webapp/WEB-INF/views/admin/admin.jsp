<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%!
    private String e(Object value) {
        return HtmlUtils.escape(value == null ? "" : String.valueOf(value));
    }

    private String selected(Map<String, String> filters, String key, String value) {
        return value.equals(filters.get(key)) ? " selected" : "";
    }

    private String active(String current, String expected) {
        return expected.equals(current) ? " active" : "";
    }

    private long stat(Map<String, Long> stats, String key) {
        return stats == null ? 0L : stats.getOrDefault(key, 0L);
    }
%>
<%
    String contextPath = request.getContextPath();
    String section = (String) request.getAttribute("section");
    SessionUser admin = (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
    Map<String, String> filters =
            (Map<String, String>) request.getAttribute("filters");
    if (filters == null) {
        filters = Collections.emptyMap();
    }
    List<Map<String, Object>> rows =
            (List<Map<String, Object>>) request.getAttribute("rows");
    if (rows == null) {
        rows = Collections.emptyList();
    }
    List<Map<String, Object>> categories =
            (List<Map<String, Object>>) request.getAttribute("categories");
    if (categories == null) {
        categories = Collections.emptyList();
    }
    Map<String, Long> stats =
            (Map<String, Long>) request.getAttribute("stats");
    Map<String, String> titles = Map.of(
            "dashboard", "数据总览",
            "users", "用户管理",
            "posts", "帖子管理",
            "goods", "商品管理",
            "lostfound", "失物招领管理",
            "activities", "活动管理",
            "notices", "公告管理",
            "reports", "举报管理"
    );
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= e(titles.get(section)) %> - CampusHub Admin</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/admin.css?v=20260610">
</head>
<body>
<div class="admin-shell">
    <aside class="admin-sidebar">
        <a class="admin-brand" href="<%= contextPath %>/admin/dashboard">
            <img src="<%= contextPath %>/images/Logo_noword.png" alt="">
            <span><strong>CampusHub</strong><small>Admin Console</small></span>
        </a>
        <nav class="admin-nav">
            <a class="<%= active(section, "dashboard") %>"
               href="<%= contextPath %>/admin/dashboard">数据总览</a>
            <a class="<%= active(section, "users") %>"
               href="<%= contextPath %>/admin/users">用户管理</a>
            <a class="<%= active(section, "posts") %>"
               href="<%= contextPath %>/admin/posts">帖子管理</a>
            <a class="<%= active(section, "goods") %>"
               href="<%= contextPath %>/admin/goods">商品管理</a>
            <a class="<%= active(section, "lostfound") %>"
               href="<%= contextPath %>/admin/lostfound">失物招领管理</a>
            <a class="<%= active(section, "activities") %>"
               href="<%= contextPath %>/admin/activities">活动管理</a>
            <a class="<%= active(section, "notices") %>"
               href="<%= contextPath %>/admin/notices">公告管理</a>
            <a class="<%= active(section, "reports") %>"
               href="<%= contextPath %>/admin/reports">举报管理</a>
        </nav>
        <a class="back-link" href="<%= contextPath %>/home">返回前台</a>
    </aside>

    <main class="admin-main">
        <header class="admin-topbar">
            <div>
                <span class="eyebrow">CampusHub 管理后台</span>
                <h1><%= e(titles.get(section)) %></h1>
            </div>
            <div class="admin-account">
                <span class="admin-avatar"><%= e(admin.avatarText()) %></span>
                <span><strong><%= e(admin.nickname()) %></strong><small>管理员</small></span>
                <form action="<%= contextPath %>/logout" method="post">
                    <button type="submit">退出</button>
                </form>
            </div>
        </header>

        <% if (request.getParameter("message") != null) { %>
        <div class="admin-alert success"><%= e(request.getParameter("message")) %></div>
        <% } %>
        <% if (request.getParameter("error") != null) { %>
        <div class="admin-alert error"><%= e(request.getParameter("error")) %></div>
        <% } %>
        <% if (request.getAttribute("loadError") != null) { %>
        <div class="admin-alert error"><%= e(request.getAttribute("loadError")) %></div>
        <% } %>

        <% if ("dashboard".equals(section)) { %>
        <section class="stat-grid">
            <article><span>用户总数</span><strong><%= stat(stats, "users") %></strong></article>
            <article><span>今日新增用户</span><strong><%= stat(stats, "todayUsers") %></strong></article>
            <article><span>正常帖子</span><strong><%= stat(stats, "posts") %></strong></article>
            <article><span>今日新增帖子</span><strong><%= stat(stats, "todayPosts") %></strong></article>
            <article><span>在架商品</span><strong><%= stat(stats, "goods") %></strong></article>
            <article><span>今日新增商品</span><strong><%= stat(stats, "todayGoods") %></strong></article>
            <article><span>开放失物招领</span><strong><%= stat(stats, "lostFound") %></strong></article>
            <article><span>活动总数</span><strong><%= stat(stats, "activities") %></strong></article>
            <article class="warning"><span>待处理举报</span><strong><%= stat(stats, "pendingReports") %></strong></article>
            <article><span>今日签到人数</span><strong><%= stat(stats, "todayCheckins") %></strong></article>
        </section>
        <section class="admin-card dashboard-note">
            <h2>治理中心</h2>
            <p>所有内容管理操作均使用状态变更，不会物理删除业务数据。举报处理会在同一数据库事务中处置目标并更新举报状态。</p>
        </section>
        <% } else { %>

        <% if ("users".equals(section)) { %>
        <form class="filter-bar" method="get">
            <input name="keyword" value="<%= e(filters.get("keyword")) %>"
                   placeholder="用户名、昵称、学号或学院">
            <select name="role">
                <option value="">全部角色</option>
                <option value="student"<%= selected(filters, "role", "student") %>>学生</option>
                <option value="admin"<%= selected(filters, "role", "admin") %>>管理员</option>
            </select>
            <select name="status">
                <option value="">全部状态</option>
                <option value="1"<%= selected(filters, "status", "1") %>>正常</option>
                <option value="0"<%= selected(filters, "status", "0") %>>禁用</option>
            </select>
            <button type="submit">筛选</button>
        </form>
        <section class="admin-card table-card">
            <div class="card-heading"><h2>用户列表</h2><span><%= rows.size() %> 条</span></div>
            <div class="table-scroll"><table>
                <thead><tr>
                    <th>ID</th><th>账号 / 昵称</th><th>学籍信息</th><th>联系方式</th>
                    <th>角色</th><th>状态</th><th>注册时间</th><th>操作</th>
                </tr></thead>
                <tbody>
                <% for (Map<String, Object> row : rows) { %>
                <tr>
                    <td>#<%= e(row.get("id")) %></td>
                    <td><strong><%= e(row.get("username")) %></strong><small><%= e(row.get("nickname")) %></small></td>
                    <td><%= e(row.get("student_no")) %><small><%= e(row.get("college")) %> / <%= e(row.get("major")) %> / <%= e(row.get("grade")) %></small></td>
                    <td><%= e(row.get("email")) %><small><%= e(row.get("phone")) %></small></td>
                    <td><span class="badge"><%= e(row.get("role")) %></span></td>
                    <td><span class="badge <%= "1".equals(String.valueOf(row.get("status"))) ? "green" : "red" %>"><%= "1".equals(String.valueOf(row.get("status"))) ? "正常" : "禁用" %></span></td>
                    <td><%= e(row.get("created_at")) %></td>
                    <td class="actions">
                        <form action="<%= contextPath %>/admin/users/status" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <input type="hidden" name="status" value="<%= "1".equals(String.valueOf(row.get("status"))) ? "0" : "1" %>">
                            <button class="<%= "1".equals(String.valueOf(row.get("status"))) ? "danger" : "" %>"
                                    type="submit"
                                    <%= String.valueOf(admin.id()).equals(String.valueOf(row.get("id"))) ? "disabled title=\"不能禁用自己\"" : "" %>>
                                <%= "1".equals(String.valueOf(row.get("status"))) ? "禁用" : "启用" %>
                            </button>
                        </form>
                        <form action="<%= contextPath %>/admin/users/reset-password" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <input class="password-input" type="password" name="password"
                                   minlength="8" maxlength="72" placeholder="新密码" required>
                            <button type="submit">重置密码</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table></div>
        </section>
        <% } %>

        <% if ("posts".equals(section)) { %>
        <form class="filter-bar" method="get">
            <input name="keyword" value="<%= e(filters.get("keyword")) %>"
                   placeholder="标题、内容或作者昵称">
            <select name="categoryId">
                <option value="">全部分类</option>
                <% for (Map<String, Object> category : categories) { %>
                <option value="<%= e(category.get("id")) %>"<%=
                        selected(filters, "categoryId", String.valueOf(category.get("id")))
                %>><%= e(category.get("name")) %></option>
                <% } %>
            </select>
            <select name="status">
                <option value="">全部状态</option>
                <option value="1"<%= selected(filters, "status", "1") %>>正常</option>
                <option value="0"<%= selected(filters, "status", "0") %>>已删除</option>
                <option value="2"<%= selected(filters, "status", "2") %>>审核中</option>
            </select>
            <button type="submit">筛选</button>
        </form>
        <section class="admin-card table-card">
            <div class="card-heading"><h2>帖子列表</h2><span><%= rows.size() %> 条</span></div>
            <div class="table-scroll"><table>
                <thead><tr><th>ID</th><th>标题</th><th>作者</th><th>分类 / 话题</th><th>互动</th><th>状态</th><th>发布时间</th><th>操作</th></tr></thead>
                <tbody>
                <% for (Map<String, Object> row : rows) { %>
                <tr>
                    <td>#<%= e(row.get("id")) %></td>
                    <td>
                        <strong><%= e(row.get("title")) %></strong>
                        <details><summary>查看正文</summary><p><%= e(row.get("content")) %></p></details>
                    </td>
                    <td><%= e(row.get("author_nickname")) %></td>
                    <td><%= e(row.get("category_name")) %><small><%= e(row.get("topic")) %></small></td>
                    <td class="metrics">赞 <%= e(row.get("like_count")) %> · 评 <%= e(row.get("comment_count")) %> · 藏 <%= e(row.get("favorite_count")) %> · 阅 <%= e(row.get("view_count")) %></td>
                    <td><span class="badge"><%= e(row.get("status")) %></span></td>
                    <td><%= e(row.get("created_at")) %></td>
                    <td class="actions">
                        <% String postStatus = String.valueOf(row.get("status")); %>
                        <form action="<%= contextPath %>/admin/posts/status" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <input type="hidden" name="status" value="<%= "0".equals(postStatus) ? "1" : "0" %>">
                            <button class="<%= "0".equals(postStatus) ? "" : "danger" %>" type="submit"><%= "0".equals(postStatus) ? "恢复" : "删除" %></button>
                        </form>
                        <form action="<%= contextPath %>/admin/posts/status" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <input type="hidden" name="status" value="2">
                            <button type="submit">设为审核中</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table></div>
        </section>
        <% } %>

        <% if ("goods".equals(section)) { %>
        <form class="filter-bar" method="get">
            <input name="keyword" value="<%= e(filters.get("keyword")) %>"
                   placeholder="标题、描述或卖家昵称">
            <select name="status">
                <option value="">全部状态</option>
                <option value="on_sale"<%= selected(filters, "status", "on_sale") %>>在售</option>
                <option value="reserved"<%= selected(filters, "status", "reserved") %>>已预订</option>
                <option value="sold"<%= selected(filters, "status", "sold") %>>已售出</option>
                <option value="off_shelf"<%= selected(filters, "status", "off_shelf") %>>已下架</option>
            </select>
            <select name="tradeMethod">
                <option value="">全部交易方式</option>
                <option value="offline"<%= selected(filters, "tradeMethod", "offline") %>>线下</option>
                <option value="online"<%= selected(filters, "tradeMethod", "online") %>>线上</option>
                <option value="both"<%= selected(filters, "tradeMethod", "both") %>>均可</option>
            </select>
            <button type="submit">筛选</button>
        </form>
        <section class="admin-card table-card">
            <div class="card-heading"><h2>商品列表</h2><span><%= rows.size() %> 条</span></div>
            <div class="table-scroll"><table>
                <thead><tr><th>ID</th><th>商品</th><th>价格</th><th>卖家</th><th>分类 / 成色</th><th>交易</th><th>状态</th><th>操作</th></tr></thead>
                <tbody>
                <% for (Map<String, Object> row : rows) { %>
                <tr>
                    <td>#<%= e(row.get("id")) %></td>
                    <td>
                        <strong><%= e(row.get("title")) %></strong>
                        <small><%= e(row.get("created_at")) %></small>
                        <details><summary>查看描述</summary><p><%= e(row.get("description")) %></p></details>
                    </td>
                    <td class="price">¥<%= e(row.get("price")) %></td>
                    <td><%= e(row.get("seller_nickname")) %></td>
                    <td><%= e(row.get("category_name")) %><small><%= e(row.get("condition_level")) %></small></td>
                    <td><%= e(row.get("trade_method")) %><small><%= e(row.get("trade_place")) %></small></td>
                    <td><span class="badge"><%= e(row.get("status")) %></span></td>
                    <td class="actions">
                        <% String goodsStatus = String.valueOf(row.get("status")); %>
                        <form action="<%= contextPath %>/admin/goods/status" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <input type="hidden" name="status" value="<%= "off_shelf".equals(goodsStatus) ? "on_sale" : "off_shelf" %>">
                            <button class="<%= "off_shelf".equals(goodsStatus) ? "" : "danger" %>" type="submit"><%= "off_shelf".equals(goodsStatus) ? "恢复在售" : "下架" %></button>
                        </form>
                        <form action="<%= contextPath %>/admin/goods/status" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <input type="hidden" name="status" value="sold">
                            <button type="submit">标记售出</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table></div>
        </section>
        <% } %>

        <% if ("lostfound".equals(section)) { %>
        <form class="filter-bar" method="get">
            <input name="keyword" value="<%= e(filters.get("keyword")) %>"
                   placeholder="标题、物品、地点或发布者">
            <select name="type">
                <option value="">全部类型</option>
                <option value="lost"<%= selected(filters, "type", "lost") %>>失物</option>
                <option value="found"<%= selected(filters, "type", "found") %>>招领</option>
            </select>
            <select name="status">
                <option value="">全部状态</option>
                <option value="pending"<%= selected(filters, "status", "pending") %>>待认领</option>
                <option value="claiming"<%= selected(filters, "status", "claiming") %>>认领中</option>
                <option value="completed"<%= selected(filters, "status", "completed") %>>已完成</option>
                <option value="closed"<%= selected(filters, "status", "closed") %>>已关闭</option>
            </select>
            <button type="submit">筛选</button>
        </form>
        <section class="admin-card table-card">
            <div class="card-heading"><h2>失物招领列表</h2><span><%= rows.size() %> 条</span></div>
            <div class="table-scroll"><table>
                <thead><tr><th>ID</th><th>类型</th><th>物品 / 标题</th><th>地点</th><th>发布者</th><th>状态</th><th>发布时间</th><th>操作</th></tr></thead>
                <tbody>
                <% for (Map<String, Object> row : rows) { %>
                <tr>
                    <td>#<%= e(row.get("id")) %></td><td><span class="badge"><%= e(row.get("type")) %></span></td>
                    <td>
                        <a class="row-link" href="<%= contextPath %>/lostfound/detail?id=<%= e(row.get("id")) %>"><%= e(row.get("item_name")) %></a>
                        <small><%= e(row.get("title")) %></small>
                        <details><summary>查看描述</summary><p><%= e(row.get("description")) %></p></details>
                    </td>
                    <td><%= e(row.get("place")) %></td><td><%= e(row.get("author_nickname")) %></td>
                    <td><span class="badge"><%= e(row.get("status")) %></span></td><td><%= e(row.get("created_at")) %></td>
                    <td class="actions">
                        <form action="<%= contextPath %>/admin/lostfound/status" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <% String lostStatus = String.valueOf(row.get("status")); %>
                            <select name="status">
                                <option value="pending"<%= "pending".equals(lostStatus) ? " selected" : "" %>>待认领</option>
                                <option value="claiming"<%= "claiming".equals(lostStatus) ? " selected" : "" %>>认领中</option>
                                <option value="completed"<%= "completed".equals(lostStatus) ? " selected" : "" %>>已完成</option>
                                <option value="closed"<%= "closed".equals(lostStatus) ? " selected" : "" %>>关闭</option>
                            </select>
                            <button type="submit">更新</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table></div>
        </section>
        <% } %>

        <% if ("activities".equals(section)) { %>
        <form class="filter-bar" method="get">
            <input name="keyword" value="<%= e(filters.get("keyword")) %>"
                   placeholder="标题、地点或发布者">
            <select name="status">
                <option value="">全部状态</option>
                <option value="signup"<%= selected(filters, "status", "signup") %>>报名中</option>
                <option value="closed"<%= selected(filters, "status", "closed") %>>已截止</option>
                <option value="ongoing"<%= selected(filters, "status", "ongoing") %>>进行中</option>
                <option value="finished"<%= selected(filters, "status", "finished") %>>已结束</option>
            </select>
            <button type="submit">筛选</button>
        </form>
        <section class="admin-card table-card">
            <div class="card-heading"><h2>活动列表</h2><span><%= rows.size() %> 条</span></div>
            <div class="table-scroll"><table>
                <thead><tr><th>ID</th><th>活动</th><th>地点</th><th>时间</th><th>人数</th><th>发布者</th><th>状态</th><th>操作</th></tr></thead>
                <tbody>
                <% for (Map<String, Object> row : rows) { %>
                <tr>
                    <td>#<%= e(row.get("id")) %></td>
                    <td>
                        <a class="row-link" href="<%= contextPath %>/activity/detail?id=<%= e(row.get("id")) %>"><%= e(row.get("title")) %></a>
                        <small><%= e(row.get("created_at")) %></small>
                        <details><summary>查看内容</summary><p><%= e(row.get("content")) %></p></details>
                    </td>
                    <td><%= e(row.get("location")) %></td>
                    <td><%= e(row.get("start_time")) %><small>截止 <%= e(row.get("deadline")) %></small></td>
                    <td><%= e(row.get("current_members")) %> / <%= e(row.get("max_members")) %></td>
                    <td><%= e(row.get("author_nickname")) %></td><td><span class="badge"><%= e(row.get("status")) %></span></td>
                    <td class="actions">
                        <form action="<%= contextPath %>/admin/activities/status" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <% String activityStatus = String.valueOf(row.get("status")); %>
                            <select name="status">
                                <option value="signup"<%= "signup".equals(activityStatus) ? " selected" : "" %>>报名中</option>
                                <option value="closed"<%= "closed".equals(activityStatus) ? " selected" : "" %>>截止报名</option>
                                <option value="ongoing"<%= "ongoing".equals(activityStatus) ? " selected" : "" %>>进行中</option>
                                <option value="finished"<%= "finished".equals(activityStatus) ? " selected" : "" %>>已结束</option>
                            </select>
                            <button type="submit">更新</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table></div>
        </section>
        <% } %>

        <% if ("notices".equals(section)) { %>
        <section class="admin-card notice-editor">
            <div class="card-heading"><h2>发布公告</h2><span>发布后立即在前台公告区域展示</span></div>
            <form action="<%= contextPath %>/admin/notices/create" method="post">
                <input name="title" maxlength="150" placeholder="公告标题" required>
                <select name="type">
                    <option value="system">系统通知</option><option value="teaching">教务通知</option>
                    <option value="life">生活通知</option><option value="activity">活动通知</option>
                    <option value="urgent">紧急通知</option>
                </select>
                <textarea name="content" rows="4" placeholder="公告内容" required></textarea>
                <button type="submit">发布公告</button>
            </form>
        </section>
        <form class="filter-bar" method="get">
            <select name="type">
                <option value="">全部类型</option>
                <option value="system"<%= selected(filters, "type", "system") %>>系统</option>
                <option value="teaching"<%= selected(filters, "type", "teaching") %>>教务</option>
                <option value="life"<%= selected(filters, "type", "life") %>>生活</option>
                <option value="activity"<%= selected(filters, "type", "activity") %>>活动</option>
                <option value="urgent"<%= selected(filters, "type", "urgent") %>>紧急</option>
            </select>
            <button type="submit">筛选</button>
        </form>
        <section class="notice-list">
            <% for (Map<String, Object> row : rows) { %>
            <article class="admin-card notice-item">
                <form action="<%= contextPath %>/admin/notices/update" method="post">
                    <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                    <div class="notice-meta">
                        <span class="badge"><%= e(row.get("type")) %></span>
                        <% if ("1".equals(String.valueOf(row.get("is_top")))) { %><span class="badge orange">置顶</span><% } %>
                        <span><%= e(row.get("creator_nickname")) %> · <%= e(row.get("created_at")) %></span>
                    </div>
                    <input name="title" maxlength="150" value="<%= e(row.get("title")) %>" required>
                    <select name="type">
                        <% String noticeType = String.valueOf(row.get("type")); %>
                        <option value="system"<%= "system".equals(noticeType) ? " selected" : "" %>>系统通知</option>
                        <option value="teaching"<%= "teaching".equals(noticeType) ? " selected" : "" %>>教务通知</option>
                        <option value="life"<%= "life".equals(noticeType) ? " selected" : "" %>>生活通知</option>
                        <option value="activity"<%= "activity".equals(noticeType) ? " selected" : "" %>>活动通知</option>
                        <option value="urgent"<%= "urgent".equals(noticeType) ? " selected" : "" %>>紧急通知</option>
                    </select>
                    <textarea name="content" rows="4" required><%= e(row.get("content")) %></textarea>
                    <button type="submit">保存编辑</button>
                </form>
                <div class="notice-actions">
                    <form action="<%= contextPath %>/admin/notices/status" method="post">
                        <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                        <input type="hidden" name="status" value="<%= "1".equals(String.valueOf(row.get("status"))) ? "0" : "1" %>">
                        <button type="submit"><%= "1".equals(String.valueOf(row.get("status"))) ? "隐藏" : "显示" %></button>
                    </form>
                    <form action="<%= contextPath %>/admin/notices/top" method="post">
                        <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                        <input type="hidden" name="top" value="<%= "1".equals(String.valueOf(row.get("is_top"))) ? "0" : "1" %>">
                        <button type="submit"><%= "1".equals(String.valueOf(row.get("is_top"))) ? "取消置顶" : "置顶" %></button>
                    </form>
                </div>
            </article>
            <% } %>
        </section>
        <% } %>

        <% if ("reports".equals(section)) { %>
        <form class="filter-bar" method="get">
            <select name="status">
                <option value="">全部状态</option>
                <option value="pending"<%= selected(filters, "status", "pending") %>>待处理</option>
                <option value="handled"<%= selected(filters, "status", "handled") %>>已处理</option>
                <option value="rejected"<%= selected(filters, "status", "rejected") %>>已驳回</option>
            </select>
            <select name="targetType">
                <option value="">全部目标</option>
                <option value="post"<%= selected(filters, "targetType", "post") %>>帖子</option>
                <option value="comment"<%= selected(filters, "targetType", "comment") %>>评论</option>
                <option value="goods"<%= selected(filters, "targetType", "goods") %>>商品</option>
                <option value="lost_found"<%= selected(filters, "targetType", "lost_found") %>>失物招领</option>
            </select>
            <button type="submit">筛选</button>
        </form>
        <section class="admin-card table-card">
            <div class="card-heading"><h2>举报列表</h2><span><%= rows.size() %> 条</span></div>
            <div class="table-scroll"><table>
                <thead><tr><th>ID</th><th>举报人</th><th>目标</th><th>原因</th><th>状态</th><th>时间</th><th>处理人</th><th>操作</th></tr></thead>
                <tbody>
                <% for (Map<String, Object> row : rows) { %>
                <tr>
                    <td>#<%= e(row.get("id")) %></td><td><%= e(row.get("reporter_nickname")) %></td>
                    <td><span class="badge"><%= e(row.get("target_type")) %></span><small>#<%= e(row.get("target_id")) %></small></td>
                    <td class="reason"><%= e(row.get("reason")) %></td>
                    <td><span class="badge"><%= e(row.get("status")) %></span></td>
                    <td><%= e(row.get("created_at")) %><small><%= e(row.get("handled_at")) %></small></td>
                    <td><%= e(row.get("handler_nickname")) %></td>
                    <td class="actions">
                        <% if ("pending".equals(String.valueOf(row.get("status")))) { %>
                        <form action="<%= contextPath %>/admin/reports/handle" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <button class="danger" type="submit">处理并处置目标</button>
                        </form>
                        <form action="<%= contextPath %>/admin/reports/reject" method="post">
                            <input type="hidden" name="id" value="<%= e(row.get("id")) %>">
                            <button type="submit">驳回</button>
                        </form>
                        <% } else { %><span class="muted">已完成</span><% } %>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table></div>
        </section>
        <% } %>

        <% if (rows.isEmpty() && !"notices".equals(section)) { %>
        <div class="empty-state">当前筛选条件下没有数据。</div>
        <% } %>
        <% } %>
    </main>
</div>
</body>
</html>
