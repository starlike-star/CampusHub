<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.Notice" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%-- 渲染校园广场页面，输出服务端数据与前端交互所需标记。 --%>
<%
    String squareContextPath = request.getContextPath();
    String activeTab = (String) request.getAttribute("activeTab");
    String keyword = (String) request.getAttribute("keyword");
    List<Notice> notices = (List<Notice>) request.getAttribute("notices");
    DateTimeFormatter noticeDateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    request.setAttribute("emptyTitle", "暂时没有匹配的广场动态");
    request.setAttribute("emptyMessage", "换个分类或搜索词试试。");
%>
<section class="square-header card">
    <div>
        <span class="square-eyebrow">CAMPUS SQUARE</span>
        <h1>校园广场</h1>
        <p>发现校园动态、公告通知与热门话题</p>
    </div>
    <form class="square-search" data-square-search>
        <svg><use href="#icon-search"></use></svg>
        <input type="search"
               name="q"
               maxlength="100"
               value="<%= HtmlUtils.escape(keyword) %>"
               placeholder="搜索广场内容...">
        <button type="submit">搜索</button>
    </form>
</section>

<nav class="square-tabs card" aria-label="校园广场分类">
    <a class="<%= "latest".equals(activeTab) ? "active" : "" %>"
       href="#square?tab=latest"
       data-route="square"
       data-tab="latest">最新动态</a>
    <a class="<%= "hot".equals(activeTab) ? "active" : "" %>"
       href="#square?tab=hot"
       data-route="square"
       data-tab="hot">热门动态</a>
    <a class="<%= "notice".equals(activeTab) ? "active" : "" %>"
       href="#square?tab=notice"
       data-route="square"
       data-tab="notice">校园公告</a>
    <a class="<%= "study".equals(activeTab) ? "active" : "" %>"
       href="#square?tab=study"
       data-route="square"
       data-tab="study">学习交流</a>
    <a class="<%= "life".equals(activeTab) ? "active" : "" %>"
       href="#square?tab=life"
       data-route="square"
       data-tab="life">校园生活</a>
    <a class="<%= "trade".equals(activeTab) ? "active" : "" %>"
       href="#square?tab=trade"
       data-route="square"
       data-tab="trade">二手交易</a>
</nav>

<% if ("notice".equals(activeTab)) { %>
<div class="square-list notice-square-list">
    <% if (notices == null || notices.isEmpty()) { %>
    <section class="empty-state card">
        <h2>暂时没有匹配的校园公告</h2>
        <p>换个搜索词，或稍后再来看看。</p>
    </section>
    <% } else {
        for (Notice notice : notices) {
            String typeText = "系统";
            if ("teaching".equals(notice.getType())) {
                typeText = "教务";
            } else if ("life".equals(notice.getType())) {
                typeText = "生活";
            } else if ("activity".equals(notice.getType())) {
                typeText = "活动";
            } else if ("urgent".equals(notice.getType())) {
                typeText = "紧急";
            }
    %>
    <article class="notice-card card">
        <div class="notice-card-meta">
            <span class="notice-type <%= "urgent".equals(notice.getType())
                    ? "urgent"
                    : "activity".equals(notice.getType()) ? "event" : "info"
            %>"><%= typeText %></span>
            <% if (notice.isTop()) { %>
            <span class="top-badge">置顶</span>
            <% } %>
            <time><%= notice.getCreatedAt() == null
                    ? ""
                    : notice.getCreatedAt().format(noticeDateFormatter) %></time>
        </div>
        <h2>
            <a href="<%= squareContextPath %>/notice/detail?id=<%=
                    notice.getId()
            %>"><%= HtmlUtils.escape(notice.getTitle()) %></a>
        </h2>
        <p><%= HtmlUtils.escape(notice.getSummary()) %></p>
        <a class="notice-detail-link"
           href="<%= squareContextPath %>/notice/detail?id=<%=
                   notice.getId()
           %>">查看详情</a>
    </article>
    <%  }
       } %>
</div>
<% } else { %>
<jsp:include page="post-list.jsp"/>
<% } %>
