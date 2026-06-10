<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.Activity" %>
<%@ page import="cn.campushub.model.ActivityVO" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%
    String activityContextPath = request.getContextPath();
    List<ActivityVO> activities =
            (List<ActivityVO>) request.getAttribute("activities");
    Set<Long> registeredActivityIds =
            (Set<Long>) request.getAttribute("registeredActivityIds");
    boolean activityLoggedIn =
            Boolean.TRUE.equals(request.getAttribute("activityLoggedIn"));
    String selectedStatus = (String) request.getAttribute("selectedStatus");
    String selectedSort = (String) request.getAttribute("selectedSort");
    String keyword = (String) request.getAttribute("keyword");
    DateTimeFormatter activityDateTime =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    if (selectedStatus == null) selectedStatus = "all";
    if (selectedSort == null) selectedSort = "latest";
    if (registeredActivityIds == null) registeredActivityIds = Set.of();
%>
<div class="activity-page">
    <section class="activity-header card">
        <div>
            <span>CAMPUS ACTIVITIES</span>
            <h1>校园活动</h1>
            <p>发现校园精彩活动，报名参与属于你的校园生活</p>
        </div>
        <button type="button"
                class="primary-btn"
                data-activity-publish>发布活动</button>
    </section>

    <form class="activity-toolbar card" data-activity-filter>
        <label class="activity-search">
            <span>搜索</span>
            <input type="search"
                   name="keyword"
                   maxlength="100"
                   value="<%= HtmlUtils.escape(keyword) %>"
                   placeholder="搜索活动标题、地点、内容...">
        </label>
        <div class="activity-filter-row">
            <label>状态
                <select name="status">
                    <option value="all" <%= "all".equals(selectedStatus)
                            ? "selected" : "" %>>全部</option>
                    <option value="signup" <%= "signup".equals(selectedStatus)
                            ? "selected" : "" %>>报名中</option>
                    <option value="closed" <%= "closed".equals(selectedStatus)
                            ? "selected" : "" %>>已截止</option>
                    <option value="ongoing" <%= "ongoing".equals(selectedStatus)
                            ? "selected" : "" %>>进行中</option>
                    <option value="finished" <%= "finished".equals(selectedStatus)
                            ? "selected" : "" %>>已结束</option>
                </select>
            </label>
            <label>排序
                <select name="sort">
                    <option value="latest" <%= "latest".equals(selectedSort)
                            ? "selected" : "" %>>最新发布</option>
                    <option value="hot" <%= "hot".equals(selectedSort)
                            ? "selected" : "" %>>报名人数最多</option>
                    <option value="soon" <%= "soon".equals(selectedSort)
                            ? "selected" : "" %>>即将开始</option>
                </select>
            </label>
            <button type="submit">应用筛选</button>
        </div>
    </form>

    <% if (activities == null || activities.isEmpty()) { %>
    <section class="activity-empty card">
        <span>NO ACTIVITIES</span>
        <h2>暂无校园活动</h2>
        <p>还没有相关活动，等一个热心同学或者管理员来发布吧。</p>
        <button type="button"
                class="primary-btn"
                data-activity-publish>发布活动</button>
    </section>
    <% } else { %>
    <section class="activity-grid">
        <% for (ActivityVO item : activities) {
            Activity activity = item.activity();
            boolean registered = registeredActivityIds.contains(activity.getId());
            boolean canRegister = "signup".equals(activity.getStatus())
                    && (activity.getDeadline() == null
                    || !LocalDateTime.now().isAfter(activity.getDeadline()))
                    && (activity.getMaxMembers() == 0
                    || activity.getCurrentMembers() < activity.getMaxMembers());
            String statusText = "已结束";
            if ("signup".equals(activity.getStatus())) {
                statusText = "报名中";
            } else if ("closed".equals(activity.getStatus())) {
                statusText = "已截止";
            } else if ("ongoing".equals(activity.getStatus())) {
                statusText = "进行中";
            }
            int progress = activity.getMaxMembers() == 0
                    ? 0
                    : Math.min(100, activity.getCurrentMembers() * 100
                            / Math.max(1, activity.getMaxMembers()));
            String cover = activity.getCoverImage() == null
                    || activity.getCoverImage().isBlank()
                    ? "images/default-activity.png"
                    : activity.getCoverImage();
        %>
        <article class="activity-card card" data-activity-card>
            <a class="activity-card-media"
               href="<%= activityContextPath %>/activity/detail?id=<%=
                        activity.getId()
               %>">
                <img src="<%= activityContextPath %>/<%= HtmlUtils.escape(cover) %>"
                     onerror="this.onerror=null;this.src='<%= activityContextPath %>/images/default-activity.png';"
                     alt="<%= HtmlUtils.escape(activity.getTitle()) %>">
                <span class="activity-card-status <%= activity.getStatus() %>"><%=
                        statusText
                %></span>
            </a>
            <div class="activity-card-body">
                <div>
                    <h2><%= HtmlUtils.escape(activity.getTitle()) %></h2>
                    <p class="activity-publisher">发布者：<%=
                            HtmlUtils.escape(item.creatorNickname() == null
                                    ? "校园用户" : item.creatorNickname())
                    %></p>
                </div>
                <dl class="activity-meta">
                    <div><dt>地点</dt><dd><%=
                            HtmlUtils.escape(activity.getLocation())
                    %></dd></div>
                    <div><dt>开始</dt><dd><%= activity.getStartTime() == null
                            ? "待定"
                            : activity.getStartTime().format(activityDateTime)
                    %></dd></div>
                    <div><dt>结束</dt><dd><%= activity.getEndTime() == null
                            ? "待定"
                            : activity.getEndTime().format(activityDateTime)
                    %></dd></div>
                    <div><dt>截止</dt><dd><%= activity.getDeadline() == null
                            ? "待定"
                            : activity.getDeadline().format(activityDateTime)
                    %></dd></div>
                </dl>
                <div class="activity-progress">
                    <div>
                        <span>报名人数</span>
                        <strong data-activity-members><%=
                                activity.getCurrentMembers()
                        %> / <%= activity.getMaxMembers() == 0
                                ? "不限人数" : activity.getMaxMembers()
                        %></strong>
                    </div>
                    <div class="activity-progress-track">
                        <span style="width:<%= progress %>%"></span>
                    </div>
                </div>
                <div class="activity-actions">
                    <a href="<%= activityContextPath %>/activity/detail?id=<%=
                            activity.getId()
                    %>">查看详情</a>
                    <% if (registered) { %>
                    <button type="button"
                            data-activity-cancel
                            data-id="<%= activity.getId() %>">取消报名</button>
                    <% } else { %>
                    <button type="button"
                            class="primary-btn"
                            data-activity-register
                            data-id="<%= activity.getId() %>"
                            <%= canRegister ? "" : "disabled" %>><%=
                            canRegister ? "立即报名" : "暂不可报名"
                    %></button>
                    <% } %>
                </div>
            </div>
        </article>
        <% } %>
    </section>
    <% } %>
</div>

<div class="activity-modal" data-activity-modal hidden>
    <div class="activity-modal-backdrop" data-activity-close></div>
    <section class="activity-modal-dialog"
             role="dialog"
             aria-modal="true"
             aria-labelledby="activityModalTitle">
        <div class="activity-modal-heading">
            <div><span>CREATE ACTIVITY</span><h2 id="activityModalTitle">发布活动</h2></div>
            <button type="button"
                    data-activity-close
                    aria-label="关闭">×</button>
        </div>
        <form data-activity-form>
            <input type="hidden" name="id">
            <label>活动标题
                <input type="text" name="title" maxlength="150" required>
            </label>
            <label>活动内容
                <textarea name="content" rows="6" required></textarea>
            </label>
            <div class="activity-form-row">
                <label>活动地点
                    <input type="text" name="location" maxlength="150" required>
                </label>
                <label>封面图片路径
                    <input type="text" name="coverImage" maxlength="255">
                </label>
            </div>
            <div class="activity-form-row">
                <label>开始时间
                    <input type="datetime-local" name="startTime" required>
                </label>
                <label>结束时间
                    <input type="datetime-local" name="endTime" required>
                </label>
            </div>
            <div class="activity-form-row">
                <label>报名截止时间
                    <input type="datetime-local" name="deadline" required>
                </label>
                <label>人数上限
                    <input type="number"
                           name="maxMembers"
                           min="0"
                           value="0"
                           required>
                    <small>0 表示不限人数</small>
                </label>
            </div>
            <p class="activity-form-error" data-activity-error hidden></p>
            <div class="activity-modal-actions">
                <button type="button" data-activity-close>取消</button>
                <button type="submit" class="primary-btn">发布活动</button>
            </div>
        </form>
    </section>
</div>

<div data-activity-auth="<%= activityLoggedIn %>" hidden></div>
