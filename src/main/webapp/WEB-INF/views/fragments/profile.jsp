<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.FavoriteItemVO" %>
<%@ page import="cn.campushub.model.ExperienceInfo" %>
<%@ page import="cn.campushub.model.LostFound" %>
<%@ page import="cn.campushub.model.ProfileOverviewVO" %>
<%@ page import="cn.campushub.model.ProfileActivityVO" %>
<%@ page import="cn.campushub.model.User" %>
<%@ page import="cn.campushub.model.UserCheckinStatsVO" %>
<%@ page import="cn.campushub.model.UserCommentVO" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="cn.campushub.util.LevelUtils" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String profileContextPath = request.getContextPath();
    boolean profileLoginRequired =
            Boolean.TRUE.equals(request.getAttribute("loginRequired"));
    String activeTab = (String) request.getAttribute("activeTab");
    if (activeTab == null) {
        activeTab = "overview";
    }
    ProfileOverviewVO overview =
            (ProfileOverviewVO) request.getAttribute("profileOverview");
    DateTimeFormatter profileDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter profileDateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DecimalFormat profilePriceFormatter = new DecimalFormat("0.00");
%>
<% if (profileLoginRequired || overview == null) { %>
<section class="profile-empty card">
    <span>PERSONAL CENTER</span>
    <h1>登录后进入个人中心</h1>
    <p>登录后可以管理资料、帖子、评论、收藏、商品和签到记录。</p>
    <a class="primary-btn" href="<%= profileContextPath %>/login">去登录</a>
</section>
<% } else {
    User profileUser = overview.user();
    ProfileOverviewVO.Stats profileStats = overview.stats();
    String avatar = profileUser.getAvatar();
    ExperienceInfo profileExperience = LevelUtils.experienceInfo(
            profileUser.getExperience() == null ? 0 : profileUser.getExperience()
    );
%>
<div class="profile-dashboard">
    <section class="profile-header card">
        <div class="profile-avatar">
            <% if (avatar != null && !avatar.isBlank()) { %>
            <img src="<%= profileContextPath %><%=
                    avatar.startsWith("/") ? "" : "/"
            %><%= HtmlUtils.escape(avatar) %>"
                 alt="<%= HtmlUtils.escape(profileUser.getNickname()) %>">
            <% } else { %>
            <span><%= HtmlUtils.escape(
                    profileUser.getNickname().substring(0, 1)
            ) %></span>
            <% } %>
        </div>
        <div class="profile-info">
            <span class="profile-eyebrow">PERSONAL CENTER</span>
            <h1>
                <%= HtmlUtils.escape(profileUser.getNickname()) %>
                <span class="profile-level-badge">Lv.<%=
                        profileExperience.level()
                %></span>
            </h1>
            <p>@<%= HtmlUtils.escape(profileUser.getUsername()) %></p>
            <div class="profile-meta">
                <span><%= HtmlUtils.escape(
                        profileUser.getCollege() == null
                                ? "学院未填写"
                                : profileUser.getCollege()
                ) %></span>
                <span><%= HtmlUtils.escape(
                        profileUser.getMajor() == null
                                ? "专业未填写"
                                : profileUser.getMajor()
                ) %></span>
                <span><%= HtmlUtils.escape(
                        profileUser.getGrade() == null
                                ? "年级未填写"
                                : profileUser.getGrade()
                ) %></span>
                <span class="profile-role"><%=
                        "admin".equalsIgnoreCase(profileUser.getRole())
                                ? "管理员"
                                : "学生"
                %></span>
            </div>
        </div>
        <button type="button"
                class="primary-btn profile-edit-button"
                data-profile-edit>编辑资料</button>
    </section>

    <section class="profile-stats" aria-label="个人数据统计">
        <article class="profile-stat-card card">
            <span>帖子</span><strong><%= profileStats.postCount() %></strong>
        </article>
        <article class="profile-stat-card card">
            <span>评论</span><strong><%= profileStats.commentCount() %></strong>
        </article>
        <article class="profile-stat-card card">
            <span>收藏</span><strong><%= profileStats.favoriteCount() %></strong>
        </article>
        <article class="profile-stat-card card">
            <span>商品</span><strong><%= profileStats.goodsCount() %></strong>
        </article>
        <article class="profile-stat-card card">
            <span>签到天数</span><strong><%= profileStats.checkinDays() %></strong>
        </article>
        <article class="profile-stat-card card">
            <span>连续签到</span><strong><%= profileStats.continuousDays() %></strong>
        </article>
    </section>

    <nav class="profile-tabs card" aria-label="个人中心导航">
        <a href="#profile?tab=overview"
           data-route="profile"
           data-tab="overview"
           class="<%= "overview".equals(activeTab) ? "active" : "" %>">资料概览</a>
        <a href="#profile?tab=posts"
           data-route="profile"
           data-tab="posts"
           class="<%= "posts".equals(activeTab) ? "active" : "" %>">我的帖子</a>
        <a href="#profile?tab=comments"
           data-route="profile"
           data-tab="comments"
           class="<%= "comments".equals(activeTab) ? "active" : "" %>">我的评论</a>
        <a href="#profile?tab=favorites"
           data-route="profile"
           data-tab="favorites"
           class="<%= "favorites".equals(activeTab) ? "active" : "" %>">我的收藏</a>
        <a href="#profile?tab=goods"
           data-route="profile"
           data-tab="goods"
           class="<%= "goods".equals(activeTab) ? "active" : "" %>">我发布的商品</a>
        <a href="#profile?tab=purchasedGoods"
           data-route="profile"
           data-tab="purchasedGoods"
           class="<%= "purchasedGoods".equals(activeTab)
                   ? "active"
                   : "" %>">我买到的商品</a>
        <a href="#profile?tab=lostfound"
           data-route="profile"
           data-tab="lostfound"
           class="<%= "lostfound".equals(activeTab) ? "active" : "" %>">我的失物招领</a>
        <a href="#profile?tab=activities"
           data-route="profile"
           data-tab="activities"
           class="<%= "activities".equals(activeTab) ? "active" : "" %>">我的活动</a>
        <a href="#profile?tab=checkins"
           data-route="profile"
           data-tab="checkins"
           class="<%= "checkins".equals(activeTab) ? "active" : "" %>">我的签到</a>
    </nav>

    <% if ("overview".equals(activeTab)) { %>
    <section class="profile-level-card card">
        <div class="profile-level-heading">
            <div>
                <span>EXPERIENCE LEVEL</span>
                <strong>Lv.<%= profileExperience.level() %></strong>
            </div>
            <p>总经验 <b><%= profileExperience.experience() %></b></p>
        </div>
        <div class="profile-level-progress">
            <span style="width: <%=
                    profileExperience.progressPercent()
            %>%"></span>
        </div>
        <div class="profile-level-meta">
            <span>下一等级需要 <%=
                    profileExperience.nextLevelRequiredExp()
            %> 总经验</span>
            <span>还差 <strong><%=
                    profileExperience.remainingExp()
            %></strong> 经验</span>
        </div>
    </section>
    <section class="profile-section card">
        <div class="profile-section-heading">
            <div>
                <span>PROFILE OVERVIEW</span>
                <h2>基本资料</h2>
            </div>
            <p>注册于 <%= profileUser.getCreatedAt() == null
                    ? ""
                    : profileUser.getCreatedAt().format(profileDateFormatter) %></p>
        </div>
        <dl class="profile-detail-grid">
            <div><dt>用户名</dt><dd><%= HtmlUtils.escape(
                    profileUser.getUsername()
            ) %></dd></div>
            <div><dt>学号</dt><dd><%= HtmlUtils.escape(
                    profileUser.getStudentNo() == null
                            ? "未填写"
                            : profileUser.getStudentNo()
            ) %></dd></div>
            <div><dt>学院</dt><dd><%= HtmlUtils.escape(
                    profileUser.getCollege() == null
                            ? "未填写"
                            : profileUser.getCollege()
            ) %></dd></div>
            <div><dt>专业</dt><dd><%= HtmlUtils.escape(
                    profileUser.getMajor() == null
                            ? "未填写"
                            : profileUser.getMajor()
            ) %></dd></div>
            <div><dt>年级</dt><dd><%= HtmlUtils.escape(
                    profileUser.getGrade() == null
                            ? "未填写"
                            : profileUser.getGrade()
            ) %></dd></div>
            <div><dt>邮箱</dt><dd><%= HtmlUtils.escape(
                    profileUser.getEmail() == null
                            ? "未填写"
                            : profileUser.getEmail()
            ) %></dd></div>
            <div><dt>手机</dt><dd><%= HtmlUtils.escape(
                    profileUser.getPhone() == null
                            ? "未填写"
                            : profileUser.getPhone()
            ) %></dd></div>
            <div><dt>角色</dt><dd><%=
                    "admin".equalsIgnoreCase(profileUser.getRole())
                            ? "管理员"
                            : "学生"
            %></dd></div>
        </dl>
    </section>
    <section class="profile-danger-zone card">
        <div>
            <span>ACCOUNT SECURITY</span>
            <h2>注销账号</h2>
            <p>注销后账号无法再次登录，相关内容将被隐藏，行为和隐私数据将被清理。</p>
        </div>
        <% if ("admin".equalsIgnoreCase(profileUser.getRole())) { %>
        <p class="profile-admin-cancel-note">
            管理员账号不能在前台注销，请联系系统管理员。
        </p>
        <% } else { %>
        <button type="button" data-account-cancel-open>注销账号</button>
        <% } %>
    </section>
    <% } else if ("posts".equals(activeTab)) { %>
    <section class="profile-section profile-list-section">
        <div class="profile-section-title card">
            <div><span>MY POSTS</span><h2>我的帖子</h2></div>
            <p>按发布时间倒序，仅显示正常状态帖子</p>
        </div>
        <jsp:include page="post-list.jsp"/>
    </section>
    <% } else if ("comments".equals(activeTab)) {
        List<UserCommentVO> comments =
                (List<UserCommentVO>) request.getAttribute("profileComments");
    %>
    <section class="profile-section card">
        <div class="profile-section-heading">
            <div><span>MY COMMENTS</span><h2>我的评论</h2></div>
            <p><%= comments == null ? 0 : comments.size() %> 条</p>
        </div>
        <div class="profile-list">
            <% if (comments == null || comments.isEmpty()) { %>
            <div class="profile-inline-empty">还没有发表过评论。</div>
            <% } else {
                for (UserCommentVO comment : comments) { %>
            <article class="profile-card">
                <div class="profile-card-main">
                    <a href="<%= profileContextPath %>/post/detail?id=<%=
                            comment.postId()
                    %>" class="profile-card-title"><%=
                            HtmlUtils.escape(comment.postTitle())
                    %></a>
                    <p><%= HtmlUtils.escape(comment.content()) %></p>
                    <div class="profile-card-meta">
                        <span><%= comment.createdAt() == null
                                ? ""
                                : comment.createdAt().format(
                                        profileDateTimeFormatter
                                ) %></span>
                        <span>点赞 <%= comment.likeCount() %></span>
                    </div>
                </div>
                <a class="profile-action-link"
                   href="<%= profileContextPath %>/post/detail?id=<%=
                            comment.postId()
                   %>">查看原帖</a>
            </article>
            <%  }
               } %>
        </div>
    </section>
    <% } else if ("favorites".equals(activeTab)) {
        List<FavoriteItemVO> favorites =
                (List<FavoriteItemVO>) request.getAttribute("profileFavorites");
    %>
    <section class="profile-section card">
        <div class="profile-section-heading">
            <div><span>MY FAVORITES</span><h2>我的收藏</h2></div>
            <p>帖子与商品混合展示</p>
        </div>
        <div class="profile-list">
            <% if (favorites == null || favorites.isEmpty()) { %>
            <div class="profile-inline-empty">还没有收藏内容。</div>
            <% } else {
                for (FavoriteItemVO item : favorites) {
                    String detailUrl = item.isPost()
                            ? profileContextPath + "/post/detail?id=" + item.targetId()
                            : profileContextPath + "/goods/detail?id=" + item.targetId();
            %>
            <article class="profile-card favorite-profile-card"
                     data-profile-favorite-item>
                <% if (!item.isPost()) { %>
                <a class="profile-favorite-image" href="<%= detailUrl %>">
                    <img src="<%= profileContextPath %>/<%=
                            HtmlUtils.escape(item.firstImage())
                    %>"
                         onerror="this.onerror=null;this.src='<%= profileContextPath %>/images/default-goods.png';"
                         alt="<%= HtmlUtils.escape(item.title()) %>">
                </a>
                <% } %>
                <div class="profile-card-main">
                    <div class="profile-card-labels">
                        <span class="favorite-type-badge <%= item.targetType() %>"><%=
                                item.isPost() ? "帖子" : "商品"
                        %></span>
                        <% if (item.categoryName() != null) { %>
                        <span><%= HtmlUtils.escape(item.categoryName()) %></span>
                        <% } %>
                    </div>
                    <a class="profile-card-title" href="<%= detailUrl %>"><%=
                            HtmlUtils.escape(item.title())
                    %></a>
                    <% if (item.isPost()) { %>
                    <p><%= HtmlUtils.escape(item.summary()) %></p>
                    <div class="profile-card-meta">
                        <span>作者 <%= HtmlUtils.escape(
                                item.authorNickname()
                        ) %></span>
                        <span>点赞 <%= item.likeCount() %></span>
                        <span>评论 <%= item.commentCount() %></span>
                        <span>浏览 <%= item.viewCount() %></span>
                    </div>
                    <% } else { %>
                    <strong class="profile-favorite-price">¥<%=
                            profilePriceFormatter.format(item.price())
                    %></strong>
                    <div class="profile-card-meta">
                        <span><%= HtmlUtils.escape(
                                item.conditionLevel() == null
                                        ? "成色未填写"
                                        : item.conditionLevel()
                        ) %></span>
                        <span><%= HtmlUtils.escape(
                                item.tradePlace() == null
                                        ? "地点待定"
                                        : item.tradePlace()
                        ) %></span>
                    </div>
                    <% } %>
                    <small>收藏于 <%= item.favoriteTime() == null
                            ? ""
                            : item.favoriteTime().format(
                                    profileDateTimeFormatter
                            ) %></small>
                </div>
                <div class="profile-card-actions">
                    <a class="profile-action-link" href="<%= detailUrl %>">查看详情</a>
                    <button type="button"
                            data-profile-unfavorite
                            data-target-type="<%= item.targetType() %>"
                            data-target-id="<%= item.targetId() %>">取消收藏</button>
                </div>
            </article>
            <%  }
               } %>
        </div>
    </section>
    <% } else if ("goods".equals(activeTab)) { %>
    <section class="profile-section profile-embedded-module">
        <jsp:include page="my-goods.jsp"/>
    </section>
    <% } else if ("purchasedGoods".equals(activeTab)) { %>
    <jsp:include page="purchased-goods.jsp"/>
    <% } else if ("lostfound".equals(activeTab)) {
        List<LostFound> profileLostFound =
                (List<LostFound>) request.getAttribute("profileLostFound");
    %>
    <section class="profile-section card">
        <div class="profile-section-heading">
            <div><span>MY LOST &amp; FOUND</span><h2>我的失物招领</h2></div>
            <p><%= profileLostFound == null ? 0 : profileLostFound.size() %> 条</p>
        </div>
        <div class="profile-list">
            <% if (profileLostFound == null || profileLostFound.isEmpty()) { %>
            <div class="profile-inline-empty">还没有发布失物招领信息。</div>
            <% } else {
                for (LostFound item : profileLostFound) {
                    String profileLfStatus = "待认领";
                    if ("claiming".equals(item.getStatus())) {
                        profileLfStatus = "认领中";
                    } else if ("completed".equals(item.getStatus())) {
                        profileLfStatus = "已找回";
                    } else if ("closed".equals(item.getStatus())) {
                        profileLfStatus = "已关闭";
                    }
            %>
            <article class="profile-card">
                <div class="profile-card-main">
                    <div class="profile-card-labels">
                        <span class="favorite-type-badge"><%=
                                "lost".equals(item.getType()) ? "失物" : "招领"
                        %></span>
                        <span><%= HtmlUtils.escape(item.getCategoryName()) %></span>
                        <span><%= profileLfStatus %></span>
                    </div>
                    <a class="profile-card-title"
                       href="<%= profileContextPath %>/lostfound/detail?id=<%=
                                item.getId()
                       %>"><%= HtmlUtils.escape(item.getTitle()) %></a>
                    <p><%= HtmlUtils.escape(item.getItemName()) %> · <%=
                            HtmlUtils.escape(item.getPlace() == null
                                    ? "地点未填写" : item.getPlace())
                    %></p>
                    <small><%= item.getCreatedAt() == null
                            ? ""
                            : item.getCreatedAt().format(
                                    profileDateTimeFormatter
                            ) %></small>
                </div>
                <div class="profile-card-actions">
                    <a class="profile-action-link"
                       href="<%= profileContextPath %>/lostfound/detail?id=<%=
                                item.getId()
                       %>">查看详情</a>
                    <button type="button"
                            data-lostfound-status
                            data-id="<%= item.getId() %>"
                            data-status="closed">关闭信息</button>
                </div>
            </article>
            <%  }
               } %>
        </div>
    </section>
    <% } else if ("activities".equals(activeTab)) {
        List<ProfileActivityVO> profileActivities =
                (List<ProfileActivityVO>) request.getAttribute(
                        "profileActivities"
                );
    %>
    <section class="profile-section card">
        <div class="profile-section-heading">
            <div><span>MY ACTIVITIES</span><h2>我的活动</h2></div>
            <p><%= profileActivities == null ? 0 : profileActivities.size() %> 条</p>
        </div>
        <div class="profile-list">
            <% if (profileActivities == null || profileActivities.isEmpty()) { %>
            <div class="profile-inline-empty">还没有报名过校园活动。</div>
            <% } else {
                for (ProfileActivityVO item : profileActivities) {
                    String activityStatus = "已结束";
                    if ("signup".equals(item.status())) {
                        activityStatus = "报名中";
                    } else if ("closed".equals(item.status())) {
                        activityStatus = "已截止";
                    } else if ("ongoing".equals(item.status())) {
                        activityStatus = "进行中";
                    }
            %>
            <article class="profile-card">
                <div class="profile-card-main">
                    <div class="profile-card-labels">
                        <span class="favorite-type-badge"><%= activityStatus %></span>
                        <span><%= "registered".equals(item.registrationStatus())
                                ? "已报名" : "已取消"
                        %></span>
                    </div>
                    <a class="profile-card-title"
                       href="<%= profileContextPath %>/activity/detail?id=<%=
                                item.id()
                       %>"><%= HtmlUtils.escape(item.title()) %></a>
                    <p><%= HtmlUtils.escape(item.location()) %> · <%=
                            item.startTime() == null
                                    ? "时间待定"
                                    : item.startTime().format(
                                            profileDateTimeFormatter
                                    )
                    %></p>
                    <small>报名于 <%= item.registeredAt() == null
                            ? ""
                            : item.registeredAt().format(
                                    profileDateTimeFormatter
                            ) %></small>
                </div>
                <div class="profile-card-actions">
                    <a class="profile-action-link"
                       href="<%= profileContextPath %>/activity/detail?id=<%=
                                item.id()
                       %>">查看详情</a>
                    <% if ("registered".equals(item.registrationStatus())
                            && "signup".equals(item.status())) { %>
                    <button type="button"
                            data-activity-cancel
                            data-id="<%= item.id() %>">取消报名</button>
                    <% } %>
                </div>
            </article>
            <%  }
               } %>
        </div>
    </section>
    <% } else if ("checkins".equals(activeTab)) {
        UserCheckinStatsVO checkins =
                (UserCheckinStatsVO) request.getAttribute("profileCheckins");
    %>
    <section class="profile-section card">
        <div class="profile-section-heading">
            <div><span>MY CHECK-INS</span><h2>我的签到</h2></div>
            <p>最近 30 条签到记录</p>
        </div>
        <div class="checkin-stats">
            <article><span>当前连续</span><strong><%=
                    checkins == null ? 0 : checkins.continuousDays()
            %> 天</strong></article>
            <article><span>累计签到</span><strong><%=
                    checkins == null ? 0 : checkins.totalDays()
            %> 天</strong></article>
            <article><span>累计积分</span><strong><%=
                    checkins == null ? 0 : checkins.totalPoints()
            %></strong></article>
        </div>
        <div class="checkin-record-list">
            <% if (checkins == null || checkins.records().isEmpty()) { %>
            <div class="profile-inline-empty">还没有签到记录。</div>
            <% } else {
                for (UserCheckinStatsVO.Record record : checkins.records()) { %>
            <article>
                <time><%= record.checkinDate() == null
                        ? ""
                        : record.checkinDate().format(profileDateFormatter) %></time>
                <span>积分 +<%= record.points() %></span>
                <strong>连续 <%= record.continuousDays() %> 天</strong>
            </article>
            <%  }
               } %>
        </div>
    </section>
    <% } %>
</div>

<% if (!"admin".equalsIgnoreCase(profileUser.getRole())) { %>
<div class="profile-modal account-cancel-modal"
     data-account-cancel-modal
     hidden>
    <div class="profile-modal-backdrop" data-account-cancel-close></div>
    <section class="profile-modal-dialog account-cancel-dialog"
             role="dialog"
             aria-modal="true"
             aria-labelledby="accountCancelTitle">
        <div class="profile-modal-heading account-cancel-heading">
            <div>
                <span>DANGER ZONE</span>
                <h2 id="accountCancelTitle">注销账号</h2>
            </div>
            <button type="button"
                    class="modal-close-btn"
                    data-account-cancel-close
                    aria-label="关闭">×</button>
        </div>
        <p class="account-cancel-warning">
            注销后，你将无法再登录该账号。你发布的帖子、商品、失物招领、
            活动报名、收藏、点赞、私信等数据将被隐藏或清理。该操作不可恢复。
        </p>
        <form data-account-cancel-form>
            <label>当前密码
                <input type="password"
                       name="password"
                       minlength="8"
                       maxlength="72"
                       autocomplete="current-password"
                       required>
            </label>
            <label>注销原因（可选）
                <textarea name="reason"
                          maxlength="255"
                          rows="3"
                          placeholder="可填写注销原因"></textarea>
            </label>
            <label class="account-cancel-confirm">
                <input type="checkbox" name="confirm" value="true" required>
                <span>我确认注销该账号，并理解该操作不可恢复。</span>
            </label>
            <p class="profile-form-error"
               data-account-cancel-error
               hidden></p>
            <div class="profile-modal-actions">
                <button type="button" data-account-cancel-close>取消</button>
                <button type="submit" class="account-cancel-submit">
                    确认注销
                </button>
            </div>
        </form>
    </section>
</div>
<% } %>

<div class="profile-modal" data-profile-modal hidden>
    <div class="profile-modal-backdrop" data-profile-modal-close></div>
    <section class="profile-modal-dialog"
             role="dialog"
             aria-modal="true"
             aria-labelledby="profileEditTitle">
        <div class="profile-modal-heading">
            <div><span>EDIT PROFILE</span><h2 id="profileEditTitle">编辑资料</h2></div>
            <button type="button"
                    class="modal-close-btn"
                    data-profile-modal-close
                    aria-label="关闭">×</button>
        </div>
        <form data-profile-form>
            <div class="profile-form-row">
                <label>昵称
                    <input type="text"
                           name="nickname"
                           maxlength="50"
                           value="<%= HtmlUtils.escape(profileUser.getNickname()) %>"
                           required>
                </label>
                <div class="image-upload" data-image-upload="avatar">
                    <span>头像</span>
                    <input type="hidden"
                           name="avatar"
                           data-image-upload-value
                           value="<%= HtmlUtils.escape(profileUser.getAvatar()) %>">
                    <div class="image-upload-controls">
                        <input class="image-upload-file"
                               type="file"
                               accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp"
                               data-image-upload-input>
                        <button class="image-upload-button"
                                type="button"
                                data-image-upload-button>选择图片</button>
                        <small class="image-upload-status"
                               data-image-upload-status>支持 JPG、PNG、WebP，最大 5MB</small>
                    </div>
                    <div class="image-upload-preview"
                         data-image-upload-preview
                         hidden></div>
                </div>
            </div>
            <div class="profile-form-row">
                <label>学号
                    <input type="text"
                           name="studentNo"
                           maxlength="50"
                           value="<%= HtmlUtils.escape(profileUser.getStudentNo()) %>">
                </label>
                <label>年级
                    <input type="text"
                           name="grade"
                           maxlength="50"
                           value="<%= HtmlUtils.escape(profileUser.getGrade()) %>">
                </label>
            </div>
            <div class="profile-form-row">
                <label>学院
                    <input type="text"
                           name="college"
                           maxlength="100"
                           value="<%= HtmlUtils.escape(profileUser.getCollege()) %>">
                </label>
                <label>专业
                    <input type="text"
                           name="major"
                           maxlength="100"
                           value="<%= HtmlUtils.escape(profileUser.getMajor()) %>">
                </label>
            </div>
            <div class="profile-form-row">
                <label>邮箱
                    <input type="email"
                           name="email"
                           maxlength="100"
                           value="<%= HtmlUtils.escape(profileUser.getEmail()) %>">
                </label>
                <label>手机
                    <input type="text"
                           name="phone"
                           maxlength="30"
                           value="<%= HtmlUtils.escape(profileUser.getPhone()) %>">
                </label>
            </div>
            <p class="profile-form-error" data-profile-form-error hidden></p>
            <div class="profile-modal-actions">
                <button type="button" data-profile-modal-close>取消</button>
                <button type="submit" class="primary-btn">保存资料</button>
            </div>
        </form>
    </section>
</div>
<% } %>
