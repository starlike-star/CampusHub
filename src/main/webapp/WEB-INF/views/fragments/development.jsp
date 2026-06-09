<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%
    String moduleName = (String) request.getAttribute("moduleName");
%>
<section class="development-state card">
    <span>COMING SOON</span>
    <h1><%= HtmlUtils.escape(moduleName) %></h1>
    <p>模块开发中，当前页面框架和导航切换已经预留。</p>
</section>
