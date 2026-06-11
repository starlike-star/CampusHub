<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%-- 渲染功能开发提示页面，输出服务端数据与前端交互所需标记。 --%>
<%
    String moduleName = (String) request.getAttribute("moduleName");
%>
<section class="development-state card">
    <span>COMING SOON</span>
    <h1><%= HtmlUtils.escape(moduleName) %></h1>
    <p>模块开发中，当前页面框架和导航切换已经预留。</p>
</section>
