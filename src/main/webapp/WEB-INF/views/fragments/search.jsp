<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.SearchPageVO" %>
<%@ page import="cn.campushub.model.SearchResultVO" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%-- 渲染全站搜索页面，输出服务端数据与前端交互所需标记。 --%>
<%
    String searchContextPath = request.getContextPath();
    SearchPageVO searchPage =
            (SearchPageVO) request.getAttribute("searchPage");
    DateTimeFormatter searchDateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String[] searchTypes = {
            "post", "goods", "lost_found", "activity", "notice"
    };
    String[] searchTypeLabels = {
            "帖子", "二手商品", "失物招领", "校园活动", "校园公告"
    };
    String encodedKeyword = java.net.URLEncoder.encode(
            searchPage.keyword(),
            java.nio.charset.StandardCharsets.UTF_8
    );
%>
<section class="search-header card">
    <div>
        <span>GLOBAL SEARCH</span>
        <h1>全局搜索</h1>
        <p>搜索关键词：“<%= HtmlUtils.escape(searchPage.keyword()) %>”</p>
    </div>
    <form class="search-page-form" data-search-page-search>
        <svg><use href="#icon-search"></use></svg>
        <input type="search"
               name="keyword"
               maxlength="50"
               value="<%= HtmlUtils.escape(searchPage.keyword()) %>"
               placeholder="输入关键词搜索全站内容">
        <button type="submit">搜索</button>
    </form>
</section>

<nav class="search-tabs card" aria-label="搜索结果类型">
    <a class="<%= "all".equals(searchPage.type()) ? "active" : "" %>"
       href="#search?keyword=<%= encodedKeyword %>&type=all"
       data-search-type="all">全部
        <span><%= searchPage.totalCount() %></span>
    </a>
    <% for (int index = 0; index < searchTypes.length; index++) { %>
    <a class="<%= searchTypes[index].equals(searchPage.type())
            ? "active" : "" %>"
       href="#search?keyword=<%= encodedKeyword %>&type=<%= searchTypes[index] %>"
       data-search-type="<%= searchTypes[index] %>"><%=
            searchTypeLabels[index]
    %> <span><%= searchPage.count(searchTypes[index]) %></span></a>
    <% } %>
</nav>

<section class="search-summary card">
    <strong>共找到 <%= searchPage.totalCount() %> 条相关内容</strong>
    <div>
        <% for (int index = 0; index < searchTypes.length; index++) { %>
        <span><%= searchTypeLabels[index] %>
            <b><%= searchPage.count(searchTypes[index]) %></b>
        </span>
        <% } %>
    </div>
</section>

<% if (searchPage.validationMessage() != null) { %>
<section class="empty-state search-empty card">
    <h2><%= HtmlUtils.escape(searchPage.validationMessage()) %></h2>
    <p>请输入 1 到 50 个字符的关键词。</p>
</section>
<% } else if (searchPage.totalCount() == 0) { %>
<section class="empty-state search-empty card">
    <h2>没有找到相关内容</h2>
    <p>可以尝试更换关键词，或检查是否输入过长。</p>
</section>
<% } else {
    for (int typeIndex = 0; typeIndex < searchTypes.length; typeIndex++) {
        String resultType = searchTypes[typeIndex];
        if (!"all".equals(searchPage.type())
                && !resultType.equals(searchPage.type())) {
            continue;
        }
        List<SearchResultVO> typeResults = searchPage.resultsFor(resultType);
        if (typeResults.isEmpty() && "all".equals(searchPage.type())) {
            continue;
        }
%>
<section class="search-group">
    <div class="search-group-heading">
        <div>
            <span><%= searchTypeLabels[typeIndex] %></span>
            <strong><%= searchPage.count(resultType) %> 条结果</strong>
        </div>
        <% if ("all".equals(searchPage.type())
                && searchPage.count(resultType) > typeResults.size()) { %>
        <a href="#search?keyword=<%= encodedKeyword %>&type=<%= resultType %>"
           data-search-type="<%= resultType %>">查看更多</a>
        <% } %>
    </div>
    <div class="search-result-list">
        <% for (SearchResultVO result : typeResults) {
            boolean noticeResult = "notice".equals(result.type());
            String targetUrl = searchContextPath + result.targetUrl();
            String imageUrl = null;
            String defaultImage = null;
            if ("goods".equals(result.type())) {
                defaultImage =
                        searchContextPath
                                + "/images/default-goods.png?v=20260611";
            } else if ("lost_found".equals(result.type())) {
                defaultImage =
                        searchContextPath
                                + "/images/default-lostfound.png?v=20260611";
            } else if ("activity".equals(result.type())) {
                defaultImage =
                        searchContextPath
                                + "/images/default-activity.png?v=20260611";
            }
            if (result.image() != null && !result.image().isBlank()) {
                imageUrl = result.image().startsWith("http://")
                        || result.image().startsWith("https://")
                        ? result.image()
                        : searchContextPath
                                + (result.image().startsWith("/") ? "" : "/")
                                + result.image();
            } else {
                imageUrl = defaultImage;
            }
        %>
        <article class="search-result-card card">
            <% if (imageUrl != null) { %>
            <a class="search-result-image"
               href="<%= HtmlUtils.escape(targetUrl) %>">
                <img src="<%= HtmlUtils.escape(imageUrl) %>"
                     alt=""
                     loading="lazy"
                     <% if (defaultImage != null) { %>
                     onerror="this.onerror=null;this.src='<%=
                             HtmlUtils.escape(defaultImage)
                     %>';"
                     <% } %>>
            </a>
            <% } %>
            <div class="search-result-main">
                <div class="search-result-meta">
                    <span class="search-type-badge <%= result.type() %>"><%=
                            result.typeLabel()
                    %></span>
                    <% if (result.statusText() != null
                            && !result.statusText().isBlank()) { %>
                    <span class="search-status"><%=
                            HtmlUtils.escape(result.statusText())
                    %></span>
                    <% } %>
                    <time><%= result.createdAt() == null
                            ? ""
                            : result.createdAt().format(searchDateFormatter)
                    %></time>
                </div>
                <h2>
                    <a href="<%= HtmlUtils.escape(targetUrl) %>"><%=
                            HtmlUtils.escape(result.title())
                    %></a>
                </h2>
                <p><%= HtmlUtils.escape(result.summary()) %></p>
                <div class="search-result-footer">
                    <div>
                        <% if (result.authorName() != null
                                && !result.authorName().isBlank()) { %>
                        <span>发布者：<%=
                                HtmlUtils.escape(result.authorName())
                        %></span>
                        <% } %>
                        <span><%= HtmlUtils.escape(result.extraInfo()) %></span>
                    </div>
                    <a class="search-detail-link"
                       href="<%= HtmlUtils.escape(targetUrl) %>"><%=
                            noticeResult ? "查看公告" : "查看详情"
                    %></a>
                </div>
            </div>
        </article>
        <% } %>
    </div>
</section>
<%  }
   } %>
