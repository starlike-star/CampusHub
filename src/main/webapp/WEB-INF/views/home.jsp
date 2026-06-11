<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.Category" %>
<%@ page import="cn.campushub.model.HomeSidebarVO" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String contextPath = request.getContextPath();
    SessionUser loginUser =
            (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
    List<Category> categories =
            (List<Category>) request.getAttribute("categories");
    HomeSidebarVO sidebar =
            (HomeSidebarVO) request.getAttribute("sidebar");
    DateTimeFormatter sidebarDateFormatter =
            DateTimeFormatter.ofPattern("MM-dd HH:mm");
    HomeSidebarVO.CheckinStatus checkin = sidebar.checkin();
    int checkinProgress =
            Math.min(checkin.continuousDays() * 100 / 7, 100);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="CampusHub 校园综合社区首页">
    <meta name="context-path" content="<%= contextPath %>">
    <title>CampusHub - 校园综合社区</title>
    <link rel="stylesheet"
          href="<%= contextPath %>/css/index.css?v=20260611-campus-map">
    <link rel="stylesheet" href="<%= contextPath %>/css/profile.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/messages.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/lostfound.css">
    <link rel="stylesheet"
          href="<%= contextPath %>/css/activity.css?v=20260610-activity-sidebar-2">
    <link rel="stylesheet" href="<%= contextPath %>/css/report.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/image-upload.css">
</head>
<body data-report-authenticated="<%= loginUser != null %>">
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
    <symbol id="icon-heart" viewBox="0 0 24 24"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.7-7.5 1.1-1.1a5.5 5.5 0 0 0 0-7.8z"></path></symbol>
    <symbol id="icon-comment" viewBox="0 0 24 24"><path d="M21 15a4 4 0 0 1-4 4H8l-5 3V7a4 4 0 0 1 4-4h10a4 4 0 0 1 4 4z"></path></symbol>
    <symbol id="icon-eye" viewBox="0 0 24 24"><path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12z"></path><circle cx="12" cy="12" r="3"></circle></symbol>
    <symbol id="icon-chevron" viewBox="0 0 24 24"><path d="m8 10 4 4 4-4"></path></symbol>
</svg>

<div class="app-layout">
    <header class="topbar">
        <div class="topbar-inner">
            <a class="brand" href="#home" data-route="home"
               aria-label="CampusHub 首页">
                <span class="brand-mark">
                    <img src="<%= contextPath %>/images/Logo_noword.png"
                         alt="CampusHub Logo">
                </span>
                <span class="brand-copy">
                    <strong>CampusHub</strong>
                    <small>校园综合社区</small>
                </span>
            </a>
            <form class="global-search" data-global-search>
                <button class="global-search-submit"
                        type="submit"
                        aria-label="搜索">
                    <svg><use href="#icon-search"></use></svg>
                </button>
                <input type="search"
                       name="keyword"
                       maxlength="50"
                       placeholder="搜索帖子、商品、失物、活动、公告">
                <kbd>Ctrl K</kbd>
            </form>
            <div class="top-actions">
                <a class="primary-btn publish-link"
                   href="<%= contextPath %>/post/publish">
                    <span>+</span> 发布
                </a>
                <% if (loginUser != null) { %>
                <a class="icon-button notification-button"
                   href="#messages"
                   data-route="messages"
                   aria-label="我的消息">
                    <svg><use href="#icon-bell"></use></svg>
                    <span class="message-unread-badge"
                          data-unread-badge
                          hidden>0</span>
                </a>
                <div class="account-wrap">
                    <button class="user-entry"
                            id="accountMenuButton"
                            type="button"
                            aria-expanded="false">
                        <span class="avatar avatar-blue"
                              data-current-user-avatar>
                            <% if (loginUser.avatar() != null
                                    && !loginUser.avatar().isBlank()) { %>
                            <img src="<%= contextPath %><%=
                                    HtmlUtils.escape(HtmlUtils.resourcePath(
                                            loginUser.avatar()
                                    ))
                            %>" alt="<%= HtmlUtils.escape(
                                    loginUser.nickname()
                            ) %>">
                            <% } else { %>
                            <%= HtmlUtils.escape(loginUser.avatarText()) %>
                            <% } %>
                        </span>
                        <span class="user-name"><%=
                                HtmlUtils.escape(loginUser.nickname())
                        %></span>
                        <svg><use href="#icon-chevron"></use></svg>
                    </button>
                    <div class="account-menu" id="accountMenu">
                        <% if ("admin".equalsIgnoreCase(loginUser.role())) { %>
                        <a href="<%= contextPath %>/admin">后台管理</a>
                        <% } %>
                        <form action="<%= contextPath %>/logout" method="post">
                            <button type="submit">退出登录</button>
                        </form>
                    </div>
                </div>
                <% } else { %>
                <div class="guest-actions">
                    <a href="<%= contextPath %>/login">登录</a>
                    <a class="register-link"
                       href="<%= contextPath %>/register">注册</a>
                </div>
                <% } %>
            </div>
        </div>
    </header>

    <div class="app-body page-shell">
        <aside class="sidebar left-sidebar">
            <nav class="side-nav" aria-label="主导航">
                <a class="nav-item active"
                   href="#home"
                   data-route="home">
                    <svg><use href="#icon-home"></use></svg><span>首页</span>
                </a>
                <a class="nav-item"
                   href="#square"
                   data-route="square">
                    <svg><use href="#icon-square"></use></svg><span>校园广场</span>
                </a>
                <a class="nav-item"
                   href="#market"
                   data-route="market">
                    <svg><use href="#icon-bag"></use></svg><span>二手市场</span>
                </a>
                <a class="nav-item"
                   href="#lostfound"
                   data-route="lostfound">
                    <svg><use href="#icon-lost"></use></svg><span>失物招领</span>
                </a>
                <a class="nav-item"
                   href="#activity"
                   data-route="activity">
                    <svg><use href="#icon-calendar"></use></svg><span>校园活动</span>
                </a>
                <a class="nav-item"
                   href="#square?tab=notice"
                   data-route="square"
                   data-tab="notice">
                    <svg><use href="#icon-bell"></use></svg><span>公告通知</span>
                </a>
                <span class="nav-divider"></span>
                <a class="nav-item"
                   href="#profile"
                   data-route="profile">
                    <svg><use href="#icon-user"></use></svg><span>个人中心</span>
                </a>
                <a class="nav-item"
                   href="#messages"
                   data-route="messages">
                    <svg><use href="#icon-bell"></use></svg><span>我的消息</span>
                </a>
            </nav>
            <section class="campus-map card">
                <div class="section-heading">
                    <h2>校园地图</h2>
                    <button type="button" data-campus-map-open>查看全图</button>
                </div>
                <button class="campus-map-thumbnail"
                        type="button"
                        data-campus-map-open
                        aria-label="查看校园地图全图">
                    <img src="<%= contextPath %>/images/map.png"
                         alt="CampusHub 校园地图缩略图">
                    <span>点击查看完整地图</span>
                </button>
            </section>
        </aside>

        <main id="main-content"
              class="main-content feed-column"
              aria-live="polite">
            <jsp:include page="fragments/home-feed.jsp"/>
        </main>

        <aside class="right-panel right-sidebar">
            <section class="checkin-card card">
                <div class="checkin-top">
                    <div>
                        <span class="eyebrow">DAILY CHECK-IN</span>
                        <h2>每日签到</h2>
                        <p id="checkinStatus"><%=
                                !checkin.authenticated()
                                        ? "登录后签到"
                                        : checkin.checkedIn()
                                                ? "今日已签到"
                                                : "今日还未签到"
                        %></p>
                    </div>
                    <div class="points-badge">
                        <strong id="checkinPoints">+<%=
                                checkin.points()
                        %></strong>
                        <small>积分</small>
                    </div>
                </div>
                <div class="checkin-meta">
                    <span>连续签到
                        <strong id="streakDays"><%=
                                checkin.continuousDays()
                        %> 天</strong>
                    </span>
                </div>
                <div class="progress-track">
                    <span id="checkinProgress"
                          style="width: <%= checkinProgress %>%"></span>
                </div>
                <button class="checkin-button <%=
                                checkin.checkedIn() ? "checked" : ""
                        %>"
                        id="checkinButton"
                        type="button"
                        data-authenticated="<%= checkin.authenticated() %>"
                        <%= checkin.checkedIn() ? "disabled" : "" %>><%=
                        !checkin.authenticated()
                                ? "登录后签到"
                                : checkin.checkedIn() ? "已签到" : "立即签到"
                %></button>
            </section>

            <section class="side-card card">
                <div class="section-heading">
                    <h2>校园公告</h2>
                    <a href="#square?tab=notice"
                       data-route="square"
                       data-tab="notice">更多 &gt;</a>
                </div>
                <div class="notice-list">
                    <% if (sidebar.notices().isEmpty()) { %>
                    <p class="sidebar-empty">暂无公告</p>
                    <% } else {
                        for (HomeSidebarVO.NoticeItem notice : sidebar.notices()) {
                            String noticeClass = "info";
                            String noticeType = "系统";
                            if ("urgent".equals(notice.type())) {
                                noticeClass = "urgent";
                                noticeType = "重要";
                            } else if ("activity".equals(notice.type())) {
                                noticeClass = "event";
                                noticeType = "活动";
                            } else if ("teaching".equals(notice.type())) {
                                noticeType = "教务";
                            } else if ("life".equals(notice.type())) {
                                noticeType = "生活";
                            }
                    %>
                    <a href="#square?tab=notice"
                       data-route="square"
                       data-tab="notice">
                        <span class="notice-type <%= noticeClass %>"><%=
                                noticeType
                        %></span>
                        <div>
                            <strong><%=
                                    HtmlUtils.escape(notice.title())
                            %></strong>
                            <small><%= notice.createdAt() == null
                                    ? ""
                                    : notice.createdAt().format(
                                            sidebarDateFormatter
                                    ) %></small>
                        </div>
                    </a>
                    <%  }
                       } %>
                </div>
            </section>

            <section class="side-card card">
                <div class="section-heading">
                    <h2>活动推荐</h2>
                    <a href="#activity" data-route="activity">全部活动</a>
                </div>
                <div class="activity-list">
                    <% if (sidebar.activities().isEmpty()) { %>
                    <p class="sidebar-empty">暂无报名中的活动</p>
                    <% } else {
                        int activityIndex = 1;
                        for (HomeSidebarVO.ActivityItem activity
                                : sidebar.activities()) {
                            int activityNumber = activityIndex++;
                            int activityProgress = activity.maxMembers() <= 0
                                    ? 0
                                    : Math.min(
                                            100,
                                            activity.currentMembers() * 100
                                                    / activity.maxMembers()
                                    );
                            String homeActivityCover =
                                    activity.coverImage() == null
                                            || activity.coverImage().isBlank()
                                            ? "images/default-activity.png"
                                            : activity.coverImage();
                    %>
                    <a href="<%= contextPath %>/activity/detail?id=<%=
                            activity.id()
                       %>"
                       class="home-activity-item">
                        <span class="home-activity-thumb">
                            <img src="<%= contextPath %><%=
                                    homeActivityCover.startsWith("/")
                                            ? HtmlUtils.escape(homeActivityCover)
                                            : "/" + HtmlUtils.escape(homeActivityCover)
                            %>"
                                 onerror="this.onerror=null;this.src='<%= contextPath %>/images/default-activity.png';"
                                 alt="">
                            <b><%= String.format("%02d", activityNumber) %></b>
                        </span>
                        <div class="home-activity-main">
                            <strong><%=
                                    HtmlUtils.escape(activity.title())
                            %></strong>
                            <div class="home-activity-meta">
                                <span><%= activity.startTime() == null
                                        ? "时间待定"
                                        : activity.startTime().format(
                                                sidebarDateFormatter
                                        ) %></span>
                                <span><%= HtmlUtils.escape(
                                        activity.location() == null
                                                ? "地点待定"
                                                : activity.location()
                                ) %></span>
                            </div>
                            <div class="home-activity-progress">
                                <i><b style="width:<%= activityProgress %>%"></b></i>
                                <em><%= activity.currentMembers() %>/<%=
                                        activity.maxMembers() == 0
                                                ? "不限"
                                                : activity.maxMembers()
                                %></em>
                            </div>
                        </div>
                    </a>
                    <%  }
                       } %>
                </div>
            </section>

            <section class="side-card card">
                <div class="section-heading">
                    <h2>失物速递</h2>
                    <a href="#lostfound"
                       data-route="lostfound">查看更多</a>
                </div>
                <div class="lost-list">
                    <% if (sidebar.lostFoundItems().isEmpty()) { %>
                    <p class="sidebar-empty">暂无失物招领信息</p>
                    <% } else {
                        for (HomeSidebarVO.LostFoundItem item
                                : sidebar.lostFoundItems()) {
                            boolean found = "found".equals(item.type());
                    %>
                    <a href="#lostfound" data-route="lostfound">
                        <span class="lost-icon card-icon"><%=
                                found ? "招" : "失"
                        %></span>
                        <div>
                            <strong><%=
                                    HtmlUtils.escape(item.title())
                            %></strong>
                            <small><%= HtmlUtils.escape(
                                    item.place() == null
                                            ? "地点未填写"
                                            : item.place()
                            ) %> · <%= item.createdAt() == null
                                    ? ""
                                    : item.createdAt().format(
                                            sidebarDateFormatter
                                    ) %></small>
                        </div>
                        <em><%= found ? "招领" : "失物" %></em>
                    </a>
                    <%  }
                       } %>
                </div>
            </section>
        </aside>
    </div>
</div>

<div class="campus-map-modal"
     data-campus-map-modal
     role="dialog"
     aria-modal="true"
     aria-labelledby="campusMapTitle"
     hidden>
    <div class="campus-map-backdrop" data-campus-map-close></div>
    <section class="campus-map-dialog">
        <header class="campus-map-dialog-header">
            <div>
                <span>CAMPUS MAP</span>
                <h2 id="campusMapTitle">校园地图</h2>
            </div>
            <button class="campus-map-close"
                    type="button"
                    data-campus-map-close
                    aria-label="关闭校园地图">×</button>
        </header>
        <div class="campus-map-full-view">
            <img src="<%= contextPath %>/images/map.png"
                 alt="CampusHub 完整校园地图">
        </div>
    </section>
</div>

<div class="post-edit-modal" id="postEditModal" hidden>
    <div class="post-edit-backdrop" data-close-edit-modal></div>
    <section class="post-edit-dialog"
             role="dialog"
             aria-modal="true"
             aria-labelledby="editPostTitle">
        <div class="post-edit-heading">
            <div><span>快速编辑</span><h2 id="editPostTitle">编辑帖子</h2></div>
            <button type="button"
                    class="modal-close-btn"
                    data-close-edit-modal
                    aria-label="关闭">×</button>
        </div>
        <form id="postEditForm">
            <input type="hidden" name="postId">
            <label>标题
                <input type="text" name="title" maxlength="150" required>
            </label>
            <div class="edit-form-row">
                <label>分类
                    <select name="categoryId" required>
                        <% if (categories != null) {
                            for (Category category : categories) { %>
                        <option value="<%= category.getId() %>"><%=
                                HtmlUtils.escape(category.getName())
                        %></option>
                        <%  }
                           } %>
                    </select>
                </label>
                <label>话题
                    <input type="text" name="topic" maxlength="100">
                </label>
            </div>
            <label>内容
                <textarea name="content" rows="9" required></textarea>
            </label>
            <div class="image-upload" data-image-upload="post">
                <span>帖子图片</span>
                <input type="hidden"
                       name="images"
                       data-image-upload-value>
                <div class="image-upload-controls">
                    <input class="image-upload-file"
                           type="file"
                           multiple
                           accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp"
                           data-image-upload-input>
                    <button class="image-upload-button"
                            type="button"
                            data-image-upload-button>选择图片</button>
                    <small class="image-upload-status"
                           data-image-upload-status>可多选，单张最大 5MB</small>
                </div>
                <div class="image-upload-preview"
                     data-image-upload-preview
                     hidden></div>
            </div>
            <p class="edit-form-error" id="editFormError" hidden></p>
            <div class="post-edit-actions">
                <button type="button" data-close-edit-modal>取消</button>
                <button type="submit" class="save-post-btn">保存</button>
            </div>
        </form>
    </section>
</div>

<div class="toast" id="toast" role="status"></div>
<script src="<%= contextPath %>/js/textarea-autosize.js"></script>
<script src="<%= contextPath %>/js/image-upload.js"></script>
<script src="<%= contextPath %>/js/index.js"></script>
<script src="<%= contextPath %>/js/app-router.js?v=20260611-campus-map-fix"></script>
<script src="<%= contextPath %>/js/market.js"></script>
<script src="<%= contextPath %>/js/profile-actions.js"></script>
<script src="<%= contextPath %>/js/message-actions.js"></script>
<script src="<%= contextPath %>/js/lostfound-actions.js"></script>
<script src="<%= contextPath %>/js/activity-actions.js"></script>
<script src="<%= contextPath %>/js/report.js"></script>
</body>
</html>
