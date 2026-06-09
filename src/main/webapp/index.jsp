<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%
    String contextPath = request.getContextPath();
    SessionUser loginUser =
            (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="CampusHub 校园综合社区首页">
    <title>CampusHub - 校园综合社区</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
</head>
<body>
<svg class="svg-sprite" aria-hidden="true">
    <symbol id="icon-search" viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"></circle><path d="m20 20-4-4"></path></symbol>
    <symbol id="icon-home" viewBox="0 0 24 24"><path d="m3 10 9-7 9 7v10a1 1 0 0 1-1 1h-5v-7H9v7H4a1 1 0 0 1-1-1z"></path></symbol>
    <symbol id="icon-square" viewBox="0 0 24 24"><path d="M4 5h16v14H4z"></path><path d="M8 9h8M8 13h5"></path></symbol>
    <symbol id="icon-bag" viewBox="0 0 24 24"><path d="M5 8h14l-1 13H6z"></path><path d="M9 8V6a3 3 0 0 1 6 0v2"></path></symbol>
    <symbol id="icon-lost" viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"></circle><path d="m20 20-4-4M11 8v3M11 14h.01"></path></symbol>
    <symbol id="icon-calendar" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="16" rx="2"></rect><path d="M8 3v4M16 3v4M3 10h18"></path></symbol>
    <symbol id="icon-bell" viewBox="0 0 24 24"><path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9"></path><path d="M10 21h4"></path></symbol>
    <symbol id="icon-bookmark" viewBox="0 0 24 24"><path d="M6 3h12v18l-6-4-6 4z"></path></symbol>
    <symbol id="icon-user" viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"></circle><path d="M4 21a8 8 0 0 1 16 0"></path></symbol>
    <symbol id="icon-image" viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="16" rx="2"></rect><circle cx="9" cy="10" r="2"></circle><path d="m21 15-5-5L5 20"></path></symbol>
    <symbol id="icon-topic" viewBox="0 0 24 24"><path d="M10 3 8 21M16 3l-2 18M4 9h16M3 15h16"></path></symbol>
    <symbol id="icon-smile" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"></circle><path d="M8 14s1.5 2 4 2 4-2 4-2M9 9h.01M15 9h.01"></path></symbol>
    <symbol id="icon-heart" viewBox="0 0 24 24"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.7-7.5 1.1-1.1a5.5 5.5 0 0 0 0-7.8z"></path></symbol>
    <symbol id="icon-comment" viewBox="0 0 24 24"><path d="M21 15a4 4 0 0 1-4 4H8l-5 3V7a4 4 0 0 1 4-4h10a4 4 0 0 1 4 4z"></path></symbol>
    <symbol id="icon-eye" viewBox="0 0 24 24"><path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12z"></path><circle cx="12" cy="12" r="3"></circle></symbol>
    <symbol id="icon-clock" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"></circle><path d="M12 7v5l3 2"></path></symbol>
    <symbol id="icon-location" viewBox="0 0 24 24"><path d="M20 10c0 5-8 11-8 11S4 15 4 10a8 8 0 1 1 16 0z"></path><circle cx="12" cy="10" r="2"></circle></symbol>
    <symbol id="icon-chevron" viewBox="0 0 24 24"><path d="m8 10 4 4 4-4"></path></symbol>
</svg>

<header class="topbar">
    <div class="topbar-inner">
        <a class="brand" href="<%= contextPath %>/" aria-label="CampusHub 首页">
            <span class="brand-mark">
                <img src="<%= contextPath %>/images/Logo_noword.png" alt="CampusHub Logo">
            </span>
            <span class="brand-copy">
                <strong>CampusHub</strong>
                <small>校园综合社区</small>
            </span>
        </a>

        <label class="global-search">
            <svg><use href="#icon-search"></use></svg>
            <input type="search" placeholder="搜索校园内容（帖子 / 商品 / 失物 / 活动 / 用户）">
            <kbd>Ctrl K</kbd>
        </label>

        <div class="top-actions">
            <div class="publish-wrap">
                <button class="primary-btn" id="publishMenuButton" type="button" aria-expanded="false">
                    <span>+</span> 发布
                </button>
                <div class="publish-menu" id="publishMenu">
                    <button type="button"><span class="menu-icon blue">✦</span>发布动态</button>
                    <button type="button"><span class="menu-icon orange">¥</span>发布商品</button>
                    <button type="button"><span class="menu-icon purple">?</span>发布失物</button>
                    <button type="button"><span class="menu-icon green">✓</span>发布招领</button>
                </div>
            </div>
            <button class="icon-button notification-button" type="button" aria-label="消息通知">
                <svg><use href="#icon-bell"></use></svg>
                <span class="notification-dot">3</span>
            </button>
            <% if (loginUser != null) { %>
            <div class="account-wrap">
                <button class="user-entry" id="accountMenuButton" type="button" aria-expanded="false">
                    <span class="avatar avatar-blue"><%= HtmlUtils.escape(loginUser.avatarText()) %></span>
                    <span class="user-name"><%= HtmlUtils.escape(loginUser.nickname()) %></span>
                    <svg><use href="#icon-chevron"></use></svg>
                </button>
                <div class="account-menu" id="accountMenu">
                    <a href="<%= contextPath %>/profile">个人中心</a>
                    <a href="<%= contextPath %>/favorites">我的收藏</a>
                    <form action="<%= contextPath %>/logout" method="post">
                        <button type="submit">退出登录</button>
                    </form>
                </div>
            </div>
            <% } else { %>
            <div class="guest-actions">
                <a href="<%= contextPath %>/login">登录</a>
                <a class="register-link" href="<%= contextPath %>/register">注册</a>
            </div>
            <% } %>
        </div>
    </div>
</header>

<main class="page-shell">
    <aside class="left-sidebar">
        <nav class="side-nav" aria-label="主导航">
            <a class="nav-item active" href="#"><svg><use href="#icon-home"></use></svg><span>首页</span></a>
            <a class="nav-item" href="#"><svg><use href="#icon-square"></use></svg><span>校园广场</span></a>
            <a class="nav-item" href="#"><svg><use href="#icon-bag"></use></svg><span>二手市场</span><em>12</em></a>
            <a class="nav-item" href="#"><svg><use href="#icon-lost"></use></svg><span>失物招领</span></a>
            <a class="nav-item" href="#"><svg><use href="#icon-calendar"></use></svg><span>校园活动</span></a>
            <a class="nav-item" href="#"><svg><use href="#icon-bell"></use></svg><span>公告通知</span></a>
            <span class="nav-divider"></span>
            <a class="nav-item" href="<%= contextPath %>/favorites"><svg><use href="#icon-bookmark"></use></svg><span>我的收藏</span></a>
            <a class="nav-item" href="<%= contextPath %>/profile"><svg><use href="#icon-user"></use></svg><span>个人中心</span></a>
        </nav>

        <section class="campus-map card">
            <div class="section-heading">
                <h2>校园地图</h2>
                <a href="#">查看大图</a>
            </div>
            <div class="map-canvas" aria-label="校园地图示意图">
                <span class="map-road road-a"></span>
                <span class="map-road road-b"></span>
                <span class="map-lake"></span>
                <span class="map-building building-a">教学楼</span>
                <span class="map-building building-b">图书馆</span>
                <span class="map-building building-c">食堂</span>
                <span class="map-field">操场</span>
                <i class="map-pin pin-a"></i>
                <i class="map-pin pin-b"></i>
                <i class="map-pin pin-c"></i>
                <i class="map-pin pin-d"></i>
            </div>
            <div class="map-tags">
                <button type="button"><i class="dot blue"></i>图书馆</button>
                <button type="button"><i class="dot purple"></i>教学楼</button>
                <button type="button"><i class="dot orange"></i>食堂</button>
                <button type="button"><i class="dot green"></i>操场</button>
            </div>
        </section>
    </aside>

    <section class="feed-column">
        <section class="composer card">
            <div class="composer-main">
                <span class="avatar avatar-blue">林</span>
                <button class="composer-placeholder" type="button">今天发生了什么？</button>
            </div>
            <div class="composer-footer">
                <div class="composer-tools">
                    <button type="button"><svg><use href="#icon-image"></use></svg>图片</button>
                    <button type="button"><svg><use href="#icon-topic"></use></svg>话题</button>
                    <button type="button"><svg><use href="#icon-smile"></use></svg>表情</button>
                </div>
                <button class="composer-submit" type="button">发布</button>
            </div>
        </section>

        <div class="feed-tabs" role="tablist">
            <button class="feed-tab active" type="button" role="tab">最新</button>
            <button class="feed-tab" type="button" role="tab">热门</button>
            <button class="feed-tab" type="button" role="tab">关注</button>
        </div>

        <article class="post-card card">
            <div class="post-header">
                <span class="avatar avatar-orange">苏</span>
                <div class="post-author">
                    <div><strong>苏小满</strong><span class="verified">✓</span></div>
                    <p>新闻传播学院 · 2024级 <span>·</span> 12分钟前</p>
                </div>
                <button class="more-button" type="button" aria-label="更多操作">•••</button>
            </div>
            <div class="post-body">
                <a class="topic-tag" href="#">#校园生活</a>
                <p>二食堂新开的轻食窗口真的很不错！今天试了照烧鸡腿饭，分量足、味道也在线，学生价只要 15 元。中午排队的人有点多，建议大家错峰去尝尝～</p>
                <div class="photo-grid three">
                    <div class="mock-photo food-one"><span>今日新品</span></div>
                    <div class="mock-photo food-two"></div>
                    <div class="mock-photo food-three"></div>
                </div>
            </div>
            <div class="post-actions">
                <button class="like-button" type="button" data-count="86"><svg><use href="#icon-heart"></use></svg><span>86</span></button>
                <button type="button"><svg><use href="#icon-comment"></use></svg><span>24</span></button>
                <button class="bookmark-button" type="button"><svg><use href="#icon-bookmark"></use></svg><span>收藏</span></button>
                <span class="view-count"><svg><use href="#icon-eye"></use></svg>1.2k</span>
            </div>
        </article>

        <article class="post-card card">
            <div class="post-header">
                <span class="avatar avatar-purple">周</span>
                <div class="post-author">
                    <div><strong>周予安</strong></div>
                    <p>计算机学院 · 2023级 <span>·</span> 36分钟前</p>
                </div>
                <button class="more-button" type="button" aria-label="更多操作">•••</button>
            </div>
            <div class="post-body">
                <a class="topic-tag purple-tag" href="#">#学习交流</a>
                <p>求助！下周数据库原理考试，有没有学长学姐整理过复习重点？特别是关系代数、范式和事务并发控制这几章，感觉知识点有点多。可以用高数笔记交换！</p>
                <div class="resource-preview">
                    <span class="file-icon">PDF</span>
                    <div>
                        <strong>数据库原理复习提纲.pdf</strong>
                        <small>待完善 · 2.4 MB</small>
                    </div>
                    <button type="button">求补充</button>
                </div>
            </div>
            <div class="post-actions">
                <button class="like-button" type="button" data-count="42"><svg><use href="#icon-heart"></use></svg><span>42</span></button>
                <button type="button"><svg><use href="#icon-comment"></use></svg><span>31</span></button>
                <button class="bookmark-button" type="button"><svg><use href="#icon-bookmark"></use></svg><span>收藏</span></button>
                <span class="view-count"><svg><use href="#icon-eye"></use></svg>678</span>
            </div>
        </article>

        <article class="post-card card">
            <div class="post-header">
                <span class="avatar avatar-green">陈</span>
                <div class="post-author">
                    <div><strong>陈墨</strong></div>
                    <p>经济管理学院 · 2022级 <span>·</span> 1小时前</p>
                </div>
                <button class="more-button" type="button" aria-label="更多操作">•••</button>
            </div>
            <div class="post-body">
                <a class="topic-tag green-tag" href="#">#二手交易</a>
                <p>毕业清理宿舍，出《管理学原理》《微观经济学》教材各一本，成色八新，有少量笔记；另出一副蓝牙降噪耳机。校内可面交，打包带走可小刀。</p>
                <div class="sale-panel">
                    <div class="sale-visual">
                        <span class="book-shape book-one">管理学</span>
                        <span class="book-shape book-two">经济学</span>
                        <span class="headphone-shape">◖◗</span>
                    </div>
                    <div class="sale-info">
                        <small>教材 + 蓝牙耳机</small>
                        <strong>¥ 98</strong>
                        <span>校内面交 · 支持小刀</span>
                    </div>
                </div>
            </div>
            <div class="post-actions">
                <button class="like-button" type="button" data-count="19"><svg><use href="#icon-heart"></use></svg><span>19</span></button>
                <button type="button"><svg><use href="#icon-comment"></use></svg><span>8</span></button>
                <button class="bookmark-button" type="button"><svg><use href="#icon-bookmark"></use></svg><span>收藏</span></button>
                <span class="view-count"><svg><use href="#icon-eye"></use></svg>356</span>
            </div>
        </article>
    </section>

    <aside class="right-sidebar">
        <section class="checkin-card card">
            <div class="checkin-top">
                <div>
                    <span class="eyebrow">DAILY CHECK-IN</span>
                    <h2>每日签到</h2>
                    <p id="checkinStatus">今日还未签到</p>
                </div>
                <div class="points-badge"><strong>+5</strong><small>积分</small></div>
            </div>
            <div class="checkin-meta">
                <span>连续签到 <strong id="streakDays">3 天</strong></span>
                <span>今日奖励 <strong>+5 积分</strong></span>
            </div>
            <div class="progress-track"><span id="checkinProgress"></span></div>
            <button class="checkin-button" id="checkinButton" type="button">立即签到</button>
        </section>

        <section class="side-card card">
            <div class="section-heading">
                <h2>校园公告</h2>
                <a href="#">更多 &gt;</a>
            </div>
            <div class="notice-list">
                <a href="#"><span class="notice-type urgent">重要</span><div><strong>停水通知：本周六全校停水...</strong><small>后勤管理处 · 2小时前</small></div></a>
                <a href="#"><span class="notice-type event">活动</span><div><strong>校运会安排及报名通知</strong><small>校团委 · 昨天</small></div></a>
                <a href="#"><span class="notice-type info">教务</span><div><strong>关于期末考试安排的通知</strong><small>教务处 · 2天前</small></div></a>
            </div>
        </section>

        <section class="side-card card">
            <div class="section-heading">
                <h2>活动推荐</h2>
                <a href="#">全部活动</a>
            </div>
            <div class="activity-list">
                <a href="#" class="activity-item">
                    <span class="activity-cover coding">01</span>
                    <div><strong>编程之美挑战赛</strong><small><svg><use href="#icon-clock"></use></svg>6月15日 14:00</small><span><em>报名中</em> 128 人已报名</span></div>
                </a>
                <a href="#" class="activity-item">
                    <span class="activity-cover photo">02</span>
                    <div><strong>校园摄影大赛</strong><small><svg><use href="#icon-clock"></use></svg>截止6月20日</small><span><em>征集中</em> 86 人已报名</span></div>
                </a>
                <a href="#" class="activity-item">
                    <span class="activity-cover sport">03</span>
                    <div><strong>羽毛球友谊赛</strong><small><svg><use href="#icon-clock"></use></svg>6月18日 16:00</small><span><em>报名中</em> 42 人已报名</span></div>
                </a>
            </div>
        </section>

        <section class="side-card card">
            <div class="section-heading">
                <h2>失物速递</h2>
                <a href="#">查看更多</a>
            </div>
            <div class="lost-list">
                <a href="#"><span class="lost-icon wallet">▰</span><div><strong>捡到黑色钱包</strong><small>一食堂门口 · 20分钟前</small></div><em>失物</em></a>
                <a href="#"><span class="lost-icon card-icon">▣</span><div><strong>招领校园卡</strong><small>图书馆三楼 · 1小时前</small></div><em>招领</em></a>
                <a href="#"><span class="lost-icon key">⚿</span><div><strong>捡到一串钥匙</strong><small>东操场 · 2小时前</small></div><em>失物</em></a>
            </div>
        </section>

        <footer class="site-footer">
            <p>© 2026 CampusHub · 校园综合社区</p>
            <p><a href="#">关于我们</a><a href="#">社区规范</a><a href="#">帮助中心</a></p>
        </footer>
    </aside>
</main>

<div class="toast" id="toast" role="status">签到成功，积分 +5</div>
<script src="<%= contextPath %>/js/index.js"></script>
</body>
</html>
