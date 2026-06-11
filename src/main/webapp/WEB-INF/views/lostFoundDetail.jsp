<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.model.ClaimRequest" %>
<%@ page import="cn.campushub.model.LostFound" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%-- 渲染失物招领详情页面，输出服务端数据与前端交互所需标记。 --%>
<%
    String contextPath = request.getContextPath();
    LostFound item = (LostFound) request.getAttribute("lostFound");
    boolean owner = Boolean.TRUE.equals(request.getAttribute("lostFoundOwner"));
    SessionUser lostFoundLoginUser =
            (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
    List<ClaimRequest> claims =
            (List<ClaimRequest>) request.getAttribute("claimRequests");
    DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String statusText = "待认领";
    if ("claiming".equals(item.getStatus())) {
        statusText = "认领中";
    } else if ("completed".equals(item.getStatus())) {
        statusText = "已找回";
    } else if ("closed".equals(item.getStatus())) {
        statusText = "已关闭";
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="<%= contextPath %>">
    <title><%= HtmlUtils.escape(item.getTitle()) %> - CampusHub</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/lostfound.css">
    <link rel="stylesheet" href="<%= contextPath %>/css/report.css">
</head>
<body class="lostfound-detail-body"
      data-report-authenticated="<%= lostFoundLoginUser != null %>">
<header class="simple-topbar">
    <a class="brand" href="<%= contextPath %>/home#lostfound">
        <span class="brand-mark">
            <img src="<%= contextPath %>/images/Logo_noword.png" alt="CampusHub">
        </span>
        <span class="brand-copy">
            <strong>CampusHub</strong><small>校园综合社区</small>
        </span>
    </a>
    <a class="back-link" href="<%= contextPath %>/home#lostfound">返回失物招领</a>
</header>
<main class="lostfound-detail-shell">
    <section class="lostfound-detail card">
        <div class="lostfound-detail-image">
            <img src="<%= contextPath %><%=
                    HtmlUtils.escape(HtmlUtils.resourcePath(
                            item.getFirstImage()
                    ))
            %>"
                 onerror="this.onerror=null;this.src='<%= contextPath %>/images/default-lostfound.png?v=20260611';"
                 alt="<%= HtmlUtils.escape(item.getTitle()) %>">
        </div>
        <div class="lostfound-detail-info">
            <div class="lostfound-detail-badges">
                <span class="lostfound-type-badge <%= item.getType() %>"><%=
                        "lost".equals(item.getType()) ? "失物" : "招领"
                %></span>
                <span class="lostfound-status <%= item.getStatus() %>"><%=
                        statusText
                %></span>
            </div>
            <h1><%= HtmlUtils.escape(item.getTitle()) %></h1>
            <strong><%= HtmlUtils.escape(item.getItemName()) %></strong>
            <dl class="lostfound-detail-meta">
                <div><dt>分类</dt><dd><%= HtmlUtils.escape(
                        item.getCategoryName() == null
                                ? "未分类" : item.getCategoryName()
                ) %></dd></div>
                <div><dt>地点</dt><dd><%= HtmlUtils.escape(
                        item.getPlace() == null ? "未填写" : item.getPlace()
                ) %></dd></div>
                <div><dt>事件时间</dt><dd><%= item.getEventTime() == null
                        ? "未填写" : item.getEventTime().format(dateTime)
                %></dd></div>
                <div><dt>发布时间</dt><dd><%= item.getCreatedAt() == null
                        ? "" : item.getCreatedAt().format(dateTime)
                %></dd></div>
            </dl>
            <div class="lostfound-detail-actions">
                <% if (owner) { %>
                <button type="button"
                        data-lostfound-status-button
                        data-id="<%= item.getId() %>"
                        data-status="completed">标记为已找回</button>
                <button type="button"
                        data-lostfound-status-button
                        data-id="<%= item.getId() %>"
                        data-status="<%= "closed".equals(item.getStatus())
                                ? "pending" : "closed" %>"><%=
                        "closed".equals(item.getStatus()) ? "重新开放" : "关闭信息"
                %></button>
                <% } else if ("pending".equals(item.getStatus())
                        || "claiming".equals(item.getStatus())) { %>
                <button type="button"
                        class="primary-btn"
                        data-claim-open
                        data-id="<%= item.getId() %>"
                        data-title="<%= HtmlUtils.escape(item.getTitle()) %>">
                    申请认领
                </button>
                <a href="<%= contextPath %>/private-messages/thread?receiverId=<%=
                        item.getUserId()
                %>">私信发布者</a>
                <% } %>
                <% if (!owner && !"closed".equals(item.getStatus())) { %>
                <button type="button"
                        class="report-btn"
                        data-target-type="lost_found"
                        data-target-id="<%= item.getId() %>">举报该信息</button>
                <% } %>
            </div>
        </div>
    </section>

    <section class="lostfound-detail-section card">
        <h2>详细描述</h2>
        <p><%= HtmlUtils.escape(item.getDescription()) %></p>
    </section>

    <section class="lostfound-detail-section card">
        <h2>发布人信息</h2>
        <div class="lostfound-publisher">
            <span><%= HtmlUtils.escape(
                    item.getPublisherNickname().substring(0, 1)
            ) %></span>
            <div>
                <strong><%= HtmlUtils.escape(item.getPublisherNickname()) %></strong>
                <p><%= HtmlUtils.escape(item.getPublisherCollege() == null
                        ? "学院未填写" : item.getPublisherCollege()) %></p>
                <small>联系方式：<%= HtmlUtils.escape(item.getContact()) %></small>
            </div>
        </div>
    </section>

    <% if (owner) { %>
    <section class="lostfound-detail-section card">
        <div class="lostfound-request-heading">
            <h2>认领申请</h2>
            <span><%= claims == null ? 0 : claims.size() %> 条</span>
        </div>
        <div class="claim-request-list">
            <% if (claims == null || claims.isEmpty()) { %>
            <p class="lostfound-detail-empty">暂时没有认领申请。</p>
            <% } else {
                for (ClaimRequest claim : claims) {
                    String claimStatus = "待处理";
                    if ("approved".equals(claim.getStatus())) {
                        claimStatus = "已通过";
                    } else if ("rejected".equals(claim.getStatus())) {
                        claimStatus = "已拒绝";
                    }
            %>
            <article class="claim-request-card">
                <div>
                    <strong><%= HtmlUtils.escape(
                            claim.getApplicantNickname()
                    ) %></strong>
                    <span><%= HtmlUtils.escape(
                            claim.getApplicantCollege() == null
                                    ? "学院未填写"
                                    : claim.getApplicantCollege()
                    ) %></span>
                    <p><%= HtmlUtils.escape(claim.getMessage()) %></p>
                    <small>联系方式：<%= HtmlUtils.escape(
                            claim.getContact()
                    ) %> · <%= claim.getCreatedAt() == null
                            ? "" : claim.getCreatedAt().format(dateTime) %></small>
                </div>
                <div class="claim-request-actions">
                    <span class="<%= claim.getStatus() %>"><%=
                            claimStatus
                    %></span>
                    <% if ("pending".equals(claim.getStatus())) { %>
                    <button type="button"
                            data-claim-handle
                            data-claim-id="<%= claim.getId() %>"
                            data-action="approve">通过</button>
                    <button type="button"
                            data-claim-handle
                            data-claim-id="<%= claim.getId() %>"
                            data-action="reject">拒绝</button>
                    <% } %>
                </div>
            </article>
            <%  }
               } %>
        </div>
    </section>
    <% } %>
</main>

<div class="lostfound-modal" data-claim-modal hidden>
    <div class="lostfound-modal-backdrop" data-claim-close></div>
    <section class="lostfound-modal-dialog claim-modal" role="dialog" aria-modal="true">
        <div class="lostfound-modal-heading">
            <div><span>CLAIM REQUEST</span><h2>申请认领</h2></div>
            <button type="button" data-claim-close aria-label="关闭">×</button>
        </div>
        <form data-claim-form>
            <input type="hidden" name="lostFoundId">
            <p data-claim-title></p>
            <label>申请说明
                <textarea name="message" rows="5" required></textarea>
            </label>
            <label>联系方式
                <input name="contact" maxlength="100" required>
            </label>
            <p class="lostfound-form-error" data-claim-error hidden></p>
            <div class="lostfound-modal-actions">
                <button type="button" data-claim-close>取消</button>
                <button type="submit" class="primary-btn">提交申请</button>
            </div>
        </form>
    </section>
</div>
<script src="<%= contextPath %>/js/lostfound-actions.js"></script>
<script src="<%= contextPath %>/js/report.js"></script>
</body>
</html>
