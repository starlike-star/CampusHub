<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%-- 渲染用户注册页面，输出服务端数据与前端交互所需标记。 --%>
<%
    String contextPath = request.getContextPath();
    String errorMessage = (String) request.getAttribute("errorMessage");
    String username = (String) request.getAttribute("username");
    String email = (String) request.getAttribute("email");
    String nickname = (String) request.getAttribute("nickname");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册 - CampusHub</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/auth.css">
</head>
<body>
<main class="auth-page branded-auth-page">
    <section class="auth-intro">
        <a class="auth-brand" href="<%= contextPath %>/">
            <div>
                <strong><span>Campus</span><span>Hub</span></strong>
                <small>校园综合社区</small>
            </div>
        </a>
        <div class="intro-copy">
            <span class="intro-tag">JOIN CAMPUSHUB</span>
            <h1>从今天开始，<br>记录你的校园。</h1>
            <p>注册后即可发布动态、收藏内容，并使用 CampusHub 的全部校园功能。</p>
        </div>
    </section>

    <section class="auth-panel">
        <div class="auth-card register-card">
            <header>
                <span>创建账号</span>
                <h2>加入 CampusHub</h2>
                <p>填写基础信息完成注册</p>
            </header>

            <% if (errorMessage != null) { %>
            <div class="form-message error"><%= HtmlUtils.escape(errorMessage) %></div>
            <% } %>

            <form id="registerForm" action="<%= contextPath %>/register" method="post" novalidate>
                <div class="form-row">
                    <label>
                        <span>用户名</span>
                        <input id="username" type="text" name="username" minlength="4" maxlength="50"
                               value="<%= HtmlUtils.escape(username) %>"
                               placeholder="例如 campushub_01" autocomplete="username" required>
                        <small class="field-warning" id="usernameWarning"></small>
                    </label>
                    <label>
                        <span>昵称</span>
                        <input id="nickname" type="text" name="nickname" minlength="2" maxlength="50"
                               value="<%= HtmlUtils.escape(nickname) %>"
                               placeholder="社区显示名称" required>
                        <small class="field-warning" id="nicknameWarning"></small>
                    </label>
                </div>
                <label>
                    <span>邮箱</span>
                    <input type="email" name="email" maxlength="100"
                           value="<%= HtmlUtils.escape(email) %>"
                           placeholder="请输入常用邮箱" autocomplete="email" required>
                </label>
                <div class="form-row">
                    <label>
                        <span>密码</span>
                        <input id="password" type="password" name="password" minlength="8" maxlength="72"
                               placeholder="至少 8 位，含字母和数字"
                               autocomplete="new-password" required>
                    </label>
                    <label>
                        <span>确认密码</span>
                        <input id="confirmPassword" type="password" name="confirmPassword"
                               minlength="8" maxlength="72"
                               placeholder="再次输入密码"
                               autocomplete="new-password" required>
                        <small class="field-warning" id="passwordWarning"></small>
                    </label>
                </div>
                <button class="auth-submit" type="submit">注册并登录</button>
            </form>

            <p class="auth-switch">已有账号？<a href="<%= contextPath %>/login">返回登录</a></p>
        </div>
    </section>
</main>
<script src="<%= contextPath %>/js/register.js"></script>
</body>
</html>
