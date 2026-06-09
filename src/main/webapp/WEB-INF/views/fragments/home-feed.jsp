<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%
    String homeContextPath = request.getContextPath();
    SessionUser homeLoginUser =
            (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
    request.setAttribute("emptyTitle", "暂时还没有校园动态");
    request.setAttribute("emptyMessage", "发布第一条帖子，和同学分享校园生活。");
%>
<section class="composer card">
    <div class="composer-main">
        <span class="avatar avatar-blue"><%=
                homeLoginUser == null
                        ? "U"
                        : HtmlUtils.escape(homeLoginUser.avatarText())
        %></span>
        <a class="composer-placeholder"
           href="<%= homeContextPath %>/post/publish">今天发生了什么？</a>
    </div>
    <div class="composer-footer">
        <div class="composer-tools">
            <a href="<%= homeContextPath %>/post/publish">
                <svg><use href="#icon-image"></use></svg>图片
            </a>
            <a href="<%= homeContextPath %>/post/publish">
                <svg><use href="#icon-topic"></use></svg>话题
            </a>
        </div>
        <a class="composer-submit"
           href="<%= homeContextPath %>/post/publish">发布</a>
    </div>
</section>
<div class="feed-tabs">
    <button class="feed-tab active" type="button">最新</button>
</div>
<jsp:include page="post-list.jsp"/>
