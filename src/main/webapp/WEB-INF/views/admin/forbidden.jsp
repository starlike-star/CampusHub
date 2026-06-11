<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- 渲染无权限提示页面，输出服务端数据与前端交互所需标记。 --%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>无权访问 - CampusHub</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/admin.css">
</head>
<body class="admin-error-page">
<main class="admin-error-card">
    <span class="error-code">403</span>
    <h1>无权访问后台</h1>
    <p>后台管理仅对管理员账号开放。</p>
    <a class="admin-button primary" href="<%= request.getContextPath() %>/home">
        返回 CampusHub
    </a>
</main>
</body>
</html>
