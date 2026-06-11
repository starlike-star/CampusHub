<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%
    String contextPath = request.getContextPath();
    String errorMessage = (String) request.getAttribute("errorMessage");
    String username = (String) request.getAttribute("username");
    boolean rememberMe = Boolean.TRUE.equals(request.getAttribute("rememberMe"));
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - CampusHub</title>
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
            <span class="intro-tag">CAMPUS COMMUNITY</span>
            <h1>连接校园生活，<br>发现身边精彩。</h1>
            <p>在这里交流学习、参与活动、寻找失物，也遇见志同道合的同学。</p>
        </div>
    </section>

    <section class="auth-panel">
        <div class="auth-card">
            <header>
                <span>欢迎回来</span>
                <h2>登录 CampusHub</h2>
                <p>使用用户名继续</p>
            </header>

            <% if (errorMessage != null) { %>
            <div class="form-message error"><%= HtmlUtils.escape(errorMessage) %></div>
            <% } %>

            <form action="<%= contextPath %>/login" method="post">
                <label>
                    <span>用户名</span>
                    <input type="text" name="username" maxlength="50"
                           value="<%= HtmlUtils.escape(username) %>"
                           placeholder="请输入用户名" autocomplete="username" required>
                </label>
                <label>
                    <span>密码</span>
                    <input type="password" name="password" maxlength="72"
                           placeholder="请输入密码" autocomplete="current-password" required>
                </label>
                <label class="captcha-field">
                    <span>验证码</span>
                    <span class="captcha-row">
                        <input type="text" name="captcha" maxlength="5"
                               placeholder="请输入验证码" autocomplete="off" required>
                        <img class="captcha-image"
                             src="<%= contextPath %>/captcha"
                             alt="登录验证码，点击刷新"
                             title="点击刷新验证码"
                             role="button"
                             tabindex="0"
                             data-captcha-image>
                    </span>
                </label>
                <label class="remember-option">
                    <input type="checkbox" name="rememberMe"
                           <%= rememberMe ? "checked" : "" %>>
                    <span>记住我（7 天内免密登录）</span>
                </label>
                <button class="auth-submit" type="submit">登录</button>
            </form>

            <p class="auth-switch">还没有账号？<a href="<%= contextPath %>/register">立即注册</a></p>
        </div>
    </section>
</main>
<script>
    (() => {
        const image = document.querySelector("[data-captcha-image]");
        if (!image) {
            return;
        }
        const refresh = () => {
            image.src = "<%= contextPath %>/captcha?t=" + Date.now();
        };
        image.addEventListener("click", refresh);
        image.addEventListener("keydown", (event) => {
            if (event.key === "Enter" || event.key === " ") {
                event.preventDefault();
                refresh();
            }
        });
    })();
</script>
</body>
</html>
