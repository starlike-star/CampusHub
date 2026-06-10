<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.Activity" %>
<%@ page import="cn.campushub.model.ActivityRegistrationVO" %>
<%@ page import="cn.campushub.model.ActivityVO" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String contextPath = request.getContextPath();
    ActivityVO activityVO = (ActivityVO) request.getAttribute("activity");
    Activity activity = activityVO.activity();
    boolean registered = Boolean.TRUE.equals(request.getAttribute("registered"));
    boolean canManage =
            Boolean.TRUE.equals(request.getAttribute("canManageActivity"));
    List<ActivityRegistrationVO> registrations =
            (List<ActivityRegistrationVO>) request.getAttribute(
                    "activityRegistrations"
            );
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String statusText = "已结束";
    if ("signup".equals(activity.getStatus())) {
        statusText = "报名中";
    } else if ("closed".equals(activity.getStatus())) {
        statusText = "已截止";
    } else if ("ongoing".equals(activity.getStatus())) {
        statusText = "进行中";
    }
    String cover = activity.getCoverImage() == null
            || activity.getCoverImage().isBlank()
            ? "images/default-activity.png"
            : activity.getCoverImage();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="<%= contextPath %>">
    <title><%= HtmlUtils.escape(activity.getTitle()) %> - CampusHub</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/activity.css">
</head>
<body class="activity-detail-body">
<main class="activity-detail-shell">
    <a class="activity-back-link" href="<%= contextPath %>/home#activity">← 返回校园活动</a>
    <article class="activity-detail-card card">
        <div class="activity-detail-cover">
            <img src="<%= contextPath %>/<%= HtmlUtils.escape(cover) %>"
                 onerror="this.onerror=null;this.src='<%= contextPath %>/images/default-activity.png';"
                 alt="<%= HtmlUtils.escape(activity.getTitle()) %>">
            <span class="activity-status <%= activity.getStatus() %>"><%=
                    statusText
            %></span>
        </div>
        <div class="activity-detail-content">
            <div class="activity-detail-heading">
                <div>
                    <span>CAMPUS ACTIVITY</span>
                    <h1><%= HtmlUtils.escape(activity.getTitle()) %></h1>
                </div>
                <div class="activity-actions">
                    <% if (registered) { %>
                    <button type="button"
                            data-activity-cancel
                            data-id="<%= activity.getId() %>">取消报名</button>
                    <% } else { %>
                    <button type="button"
                            class="primary-btn"
                            data-activity-register
                            data-id="<%= activity.getId() %>"
                            <%= "signup".equals(activity.getStatus())
                                    ? "" : "disabled" %>>立即报名</button>
                    <% } %>
                </div>
            </div>
            <dl class="activity-detail-meta">
                <div><dt>地点</dt><dd><%= HtmlUtils.escape(
                        activity.getLocation()
                ) %></dd></div>
                <div><dt>开始时间</dt><dd><%= activity.getStartTime() == null
                        ? "待定" : activity.getStartTime().format(formatter)
                %></dd></div>
                <div><dt>结束时间</dt><dd><%= activity.getEndTime() == null
                        ? "待定" : activity.getEndTime().format(formatter)
                %></dd></div>
                <div><dt>报名截止</dt><dd><%= activity.getDeadline() == null
                        ? "待定" : activity.getDeadline().format(formatter)
                %></dd></div>
                <div><dt>报名人数</dt><dd data-activity-members><%=
                        activity.getCurrentMembers()
                %> / <%= activity.getMaxMembers() == 0
                        ? "不限人数" : activity.getMaxMembers()
                %></dd></div>
                <div><dt>发布时间</dt><dd><%= activity.getCreatedAt() == null
                        ? "" : activity.getCreatedAt().format(formatter)
                %></dd></div>
            </dl>
            <section class="activity-detail-section">
                <h2>活动内容</h2>
                <p><%= HtmlUtils.escape(activity.getContent()) %></p>
            </section>
            <section class="activity-organizer">
                <img src="<%= contextPath %>/<%= HtmlUtils.escape(
                        activityVO.creatorAvatar() == null
                                ? "images/default-avatar.png"
                                : activityVO.creatorAvatar()
                ) %>" alt="">
                <div>
                    <span>活动发布者</span>
                    <strong><%= HtmlUtils.escape(
                            activityVO.creatorNickname() == null
                                    ? "校园用户"
                                    : activityVO.creatorNickname()
                    ) %></strong>
                    <p><%= HtmlUtils.escape(
                            activityVO.creatorCollege() == null
                                    ? "学院未填写"
                                    : activityVO.creatorCollege()
                    ) %></p>
                </div>
            </section>
        </div>
    </article>

    <% if (canManage) { %>
    <section class="activity-manage card">
        <div class="activity-section-heading">
            <div><span>MANAGE ACTIVITY</span><h2>活动管理</h2></div>
        </div>
        <div class="activity-manage-actions">
            <button type="button"
                    data-activity-edit
                    data-id="<%= activity.getId() %>"
                    data-title="<%= HtmlUtils.escape(activity.getTitle()) %>"
                    data-content="<%= HtmlUtils.escape(activity.getContent()) %>"
                    data-cover-image="<%= HtmlUtils.escape(activity.getCoverImage()) %>"
                    data-location="<%= HtmlUtils.escape(activity.getLocation()) %>"
                    data-start-time="<%= activity.getStartTime() %>"
                    data-end-time="<%= activity.getEndTime() %>"
                    data-deadline="<%= activity.getDeadline() %>"
                    data-max-members="<%= activity.getMaxMembers() %>">编辑活动</button>
            <button type="button"
                    data-activity-status
                    data-id="<%= activity.getId() %>"
                    data-status="closed">关闭报名</button>
            <button type="button"
                    data-activity-status
                    data-id="<%= activity.getId() %>"
                    data-status="ongoing">标记进行中</button>
            <button type="button"
                    data-activity-status
                    data-id="<%= activity.getId() %>"
                    data-status="finished">标记已结束</button>
            <button type="button"
                    data-activity-status
                    data-id="<%= activity.getId() %>"
                    data-status="signup">重新开放报名</button>
        </div>
    </section>

    <section class="registration-list card">
        <div class="activity-section-heading">
            <div><span>REGISTRATIONS</span><h2>报名名单</h2></div>
            <p><%= registrations == null ? 0 : registrations.size() %> 人</p>
        </div>
        <% if (registrations == null || registrations.isEmpty()) { %>
        <p class="activity-inline-empty">暂时还没有人报名。</p>
        <% } else {
            for (ActivityRegistrationVO registration : registrations) { %>
        <article class="registration-card">
            <img src="<%= contextPath %>/<%= HtmlUtils.escape(
                    registration.avatar() == null
                            ? "images/default-avatar.png"
                            : registration.avatar()
            ) %>" alt="">
            <div>
                <strong><%= HtmlUtils.escape(registration.nickname()) %></strong>
                <p><%= HtmlUtils.escape(registration.college()) %> · <%=
                        HtmlUtils.escape(registration.major())
                %> · <%= HtmlUtils.escape(registration.grade()) %></p>
                <small><%= HtmlUtils.escape(registration.email()) %> · <%=
                        HtmlUtils.escape(registration.phone())
                %></small>
            </div>
            <time><%= registration.createdAt() == null
                    ? "" : registration.createdAt().format(formatter)
            %></time>
        </article>
        <%  }
           } %>
    </section>
    <% } %>
</main>

<div class="activity-modal" data-activity-modal hidden>
    <div class="activity-modal-backdrop" data-activity-close></div>
    <section class="activity-modal-dialog" role="dialog" aria-modal="true">
        <div class="activity-modal-heading">
            <div><span>EDIT ACTIVITY</span><h2>编辑活动</h2></div>
            <button type="button" data-activity-close aria-label="关闭">×</button>
        </div>
        <form data-activity-form>
            <input type="hidden" name="id">
            <label>活动标题<input type="text" name="title" maxlength="150" required></label>
            <label>活动内容<textarea name="content" rows="6" required></textarea></label>
            <div class="activity-form-row">
                <label>活动地点<input type="text" name="location" maxlength="150" required></label>
                <label>封面图片路径<input type="text" name="coverImage" maxlength="255"></label>
            </div>
            <div class="activity-form-row">
                <label>开始时间<input type="datetime-local" name="startTime" required></label>
                <label>结束时间<input type="datetime-local" name="endTime" required></label>
            </div>
            <div class="activity-form-row">
                <label>报名截止时间<input type="datetime-local" name="deadline" required></label>
                <label>人数上限<input type="number" name="maxMembers" min="0" required></label>
            </div>
            <p class="activity-form-error" data-activity-error hidden></p>
            <div class="activity-modal-actions">
                <button type="button" data-activity-close>取消</button>
                <button type="submit" class="primary-btn">保存修改</button>
            </div>
        </form>
    </section>
</div>
<script src="<%= contextPath %>/js/activity-actions.js"></script>
</body>
</html>
