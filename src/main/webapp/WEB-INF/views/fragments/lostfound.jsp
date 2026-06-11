<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cn.campushub.constant.SessionConstants" %>
<%@ page import="cn.campushub.model.Category" %>
<%@ page import="cn.campushub.model.LostFound" %>
<%@ page import="cn.campushub.model.SessionUser" %>
<%@ page import="cn.campushub.util.HtmlUtils" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String lfContextPath = request.getContextPath();
    SessionUser lfUser =
            (SessionUser) session.getAttribute(SessionConstants.LOGIN_USER);
    List<LostFound> lfItems =
            (List<LostFound>) request.getAttribute("lostFoundItems");
    List<Category> lfCategories =
            (List<Category>) request.getAttribute("lostFoundCategories");
    String lfType = (String) request.getAttribute("selectedType");
    String lfStatus = (String) request.getAttribute("selectedStatus");
    String lfKeyword = (String) request.getAttribute("keyword");
    String lfSort = (String) request.getAttribute("selectedSort");
    Long lfCategoryId = (Long) request.getAttribute("selectedCategoryId");
    DateTimeFormatter lfDateTime =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter lfInputDateTime =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
%>
<div class="lostfound-page">
    <section class="lostfound-header card">
        <div>
            <span>LOST &amp; FOUND</span>
            <h1>失物招领</h1>
            <p>发布失物信息，寻找遗失物品，也帮助同学找回重要物品</p>
        </div>
        <button type="button" class="primary-btn" data-lostfound-publish>
            + 发布失物 / 招领
        </button>
    </section>

    <form class="lostfound-toolbar card" data-lostfound-filter>
        <label class="lostfound-search">
            <input type="search"
                   name="keyword"
                   value="<%= HtmlUtils.escape(lfKeyword) %>"
                   placeholder="搜索物品名称、地点、描述...">
            <button type="submit">搜索</button>
        </label>
        <div class="lostfound-filter-row">
            <label>类型
                <select name="type">
                    <option value="all">全部</option>
                    <option value="lost" <%= "lost".equals(lfType)
                            ? "selected" : "" %>>我丢了东西</option>
                    <option value="found" <%= "found".equals(lfType)
                            ? "selected" : "" %>>我捡到东西</option>
                </select>
            </label>
            <label>状态
                <select name="status">
                    <option value="all">全部状态</option>
                    <option value="pending" <%= "pending".equals(lfStatus)
                            ? "selected" : "" %>>待认领</option>
                    <option value="claiming" <%= "claiming".equals(lfStatus)
                            ? "selected" : "" %>>认领中</option>
                    <option value="completed" <%= "completed".equals(lfStatus)
                            ? "selected" : "" %>>已找回</option>
                    <option value="closed" <%= "closed".equals(lfStatus)
                            ? "selected" : "" %>>已关闭</option>
                </select>
            </label>
            <label>分类
                <select name="categoryId">
                    <option value="">全部分类</option>
                    <% if (lfCategories != null) {
                        for (Category category : lfCategories) { %>
                    <option value="<%= category.getId() %>" <%=
                            category.getId().equals(lfCategoryId)
                                    ? "selected" : ""
                    %>><%= HtmlUtils.escape(category.getName()) %></option>
                    <%  }
                       } %>
                </select>
            </label>
            <label>排序
                <select name="sort">
                    <option value="latest">最新发布</option>
                    <option value="oldest" <%= "oldest".equals(lfSort)
                            ? "selected" : "" %>>时间从早到晚</option>
                </select>
            </label>
        </div>
    </form>

    <section class="lostfound-grid">
        <% if (lfItems == null || lfItems.isEmpty()) { %>
        <div class="lostfound-empty card">
            <h2>暂无失物招领信息</h2>
            <p>还没有相关信息，发布一条帮助自己或同学找回物品吧。</p>
            <button type="button" class="primary-btn" data-lostfound-publish>
                发布失物 / 招领
            </button>
        </div>
        <% } else {
            for (LostFound item : lfItems) {
                boolean owner = lfUser != null
                        && (lfUser.id() == item.getUserId()
                        || "admin".equalsIgnoreCase(lfUser.role()));
                String statusText = "待认领";
                if ("claiming".equals(item.getStatus())) {
                    statusText = "认领中";
                } else if ("completed".equals(item.getStatus())) {
                    statusText = "已找回";
                } else if ("closed".equals(item.getStatus())) {
                    statusText = "已关闭";
                }
        %>
        <article class="lostfound-card card" data-lostfound-card>
            <a class="lostfound-image"
               href="<%= lfContextPath %>/lostfound/detail?id=<%= item.getId() %>">
                <img src="<%= lfContextPath %><%=
                        item.getFirstImage().startsWith("/") ? "" : "/"
                %><%= HtmlUtils.escape(item.getFirstImage()) %>"
                     onerror="this.onerror=null;this.src='<%= lfContextPath %>/images/default-lostfound.png';"
                     alt="<%= HtmlUtils.escape(item.getTitle()) %>">
                <span class="lostfound-type-badge <%= item.getType() %>"><%=
                        "lost".equals(item.getType()) ? "失物" : "招领"
                %></span>
                <span class="lostfound-status <%= item.getStatus() %>"><%=
                        statusText
                %></span>
            </a>
            <div class="lostfound-card-body">
                <div class="lostfound-card-heading">
                    <div>
                        <small><%= HtmlUtils.escape(
                                item.getCategoryName() == null
                                        ? "未分类"
                                        : item.getCategoryName()
                        ) %></small>
                        <h2><%= HtmlUtils.escape(item.getTitle()) %></h2>
                    </div>
                    <strong><%= HtmlUtils.escape(item.getItemName()) %></strong>
                </div>
                <p><%= HtmlUtils.escape(item.getDescription()) %></p>
                <div class="lostfound-meta">
                    <span>地点：<%= HtmlUtils.escape(
                            item.getPlace() == null ? "未填写" : item.getPlace()
                    ) %></span>
                    <span>时间：<%= item.getEventTime() == null
                            ? "未填写"
                            : item.getEventTime().format(lfDateTime) %></span>
                    <span>发布人：<%= HtmlUtils.escape(
                            item.getPublisherNickname()
                    ) %></span>
                    <span>发布于：<%= item.getCreatedAt() == null
                            ? ""
                            : item.getCreatedAt().format(lfDateTime) %></span>
                </div>
                <div class="lostfound-actions">
                    <a href="<%= lfContextPath %>/lostfound/detail?id=<%=
                            item.getId()
                    %>">查看详情</a>
                    <% if (!owner
                            && ("pending".equals(item.getStatus())
                            || "claiming".equals(item.getStatus()))) { %>
                    <button type="button"
                            data-claim-open
                            data-id="<%= item.getId() %>"
                            data-title="<%= HtmlUtils.escape(item.getTitle()) %>">
                        申请认领
                    </button>
                    <% } else if (owner) { %>
                    <button type="button"
                            data-lostfound-edit
                            data-id="<%= item.getId() %>"
                            data-type="<%= item.getType() %>"
                            data-item-name="<%= HtmlUtils.escape(item.getItemName()) %>"
                            data-title="<%= HtmlUtils.escape(item.getTitle()) %>"
                            data-category-id="<%= item.getCategoryId() == null
                                    ? "" : item.getCategoryId() %>"
                            data-description="<%= HtmlUtils.escape(item.getDescription()) %>"
                            data-place="<%= HtmlUtils.escape(item.getPlace()) %>"
                            data-event-time="<%= item.getEventTime() == null
                                    ? ""
                                    : item.getEventTime().format(lfInputDateTime) %>"
                            data-images="<%= HtmlUtils.escape(item.getImages()) %>"
                            data-contact="<%= HtmlUtils.escape(item.getContact()) %>">
                        编辑
                    </button>
                    <select data-lostfound-status data-id="<%= item.getId() %>">
                        <option value="pending" <%= "pending".equals(item.getStatus())
                                ? "selected" : "" %>>待认领</option>
                        <option value="claiming" <%= "claiming".equals(item.getStatus())
                                ? "selected" : "" %>>认领中</option>
                        <option value="completed" <%= "completed".equals(item.getStatus())
                                ? "selected" : "" %>>已找回</option>
                        <option value="closed" <%= "closed".equals(item.getStatus())
                                ? "selected" : "" %>>已关闭</option>
                    </select>
                    <% } %>
                </div>
            </div>
        </article>
        <%  }
           } %>
    </section>
</div>

<div class="lostfound-modal" data-lostfound-modal hidden>
    <div class="lostfound-modal-backdrop" data-lostfound-close></div>
    <section class="lostfound-modal-dialog" role="dialog" aria-modal="true">
        <div class="lostfound-modal-heading">
            <div><span>LOST &amp; FOUND</span><h2 data-lostfound-modal-title>发布失物 / 招领</h2></div>
            <button type="button" data-lostfound-close aria-label="关闭">×</button>
        </div>
        <form data-lostfound-form>
            <input type="hidden" name="id">
            <div class="lostfound-form-row">
                <label>类型
                    <select name="type" required>
                        <option value="lost">我丢了东西</option>
                        <option value="found">我捡到东西</option>
                    </select>
                </label>
                <label>物品分类
                    <select name="categoryId" required>
                        <% if (lfCategories != null) {
                            for (Category category : lfCategories) { %>
                        <option value="<%= category.getId() %>"><%=
                                HtmlUtils.escape(category.getName())
                        %></option>
                        <%  }
                           } %>
                    </select>
                </label>
            </div>
            <div class="lostfound-form-row">
                <label>物品名称
                    <input name="itemName" maxlength="100" required>
                </label>
                <label>标题
                    <input name="title" maxlength="150" required>
                </label>
            </div>
            <label>详细描述
                <textarea name="description" rows="5" required></textarea>
            </label>
            <div class="lostfound-form-row">
                <label>丢失 / 拾取地点
                    <input name="place" maxlength="150">
                </label>
                <label>丢失 / 拾取时间
                    <input type="datetime-local" name="eventTime">
                </label>
            </div>
            <div class="lostfound-form-row">
                <div class="image-upload" data-image-upload="lost_found">
                    <span>物品图片</span>
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
                <label>联系方式
                    <input name="contact" maxlength="100" required>
                </label>
            </div>
            <p class="lostfound-form-error" data-lostfound-error hidden></p>
            <div class="lostfound-modal-actions">
                <button type="button" data-lostfound-close>取消</button>
                <button type="submit" class="primary-btn">保存</button>
            </div>
        </form>
    </section>
</div>

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
                <textarea name="message" rows="5"
                          placeholder="请描述物品特征或认领依据" required></textarea>
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
