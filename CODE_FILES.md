# 代码文件职责表

本文档列出项目中的源码、视图、样式、数据库脚本和运行配置文件。
图片、Markdown 说明、IDE 配置、构建产物不属于代码文件，因此不在表内。

| 文件 | 类型 | 作用 |
| --- | --- | --- |
| `database/migrations/20260610_add_goods_trade_method.sql` | SQL | 为商品交易方式功能补充数据库字段及相关数据迁移。 |
| `pom.xml` | XML 配置 | 定义 Maven 项目的依赖、Java 版本、测试与打包配置。 |
| `src/main/java/cn/campushub/config/DatabaseConfig.java` | Java 配置 | 集中读取并校验数据库连接配置，为 JDBC 访问提供统一配置来源。 |
| `src/main/java/cn/campushub/constant/SessionConstants.java` | Java 常量 | 集中定义会话属性名等跨模块共享常量，避免散落的字符串字面量。 |
| `src/main/java/cn/campushub/dao/AccountDao.java` | Java DAO | 定义账号数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/ActivityDao.java` | Java DAO | 定义活动数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/ActivityRegistrationDao.java` | Java DAO | 定义活动报名数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/AdminDao.java` | Java DAO | 定义后台管理数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/ClaimRequestDao.java` | Java DAO | 定义认领申请数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/ExperienceDao.java` | Java DAO | 定义经验值数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/GoodsDao.java` | Java DAO | 定义商品数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/GoodsOrderDao.java` | Java DAO | 定义商品订单数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/HomeDao.java` | Java DAO | 定义首页数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/JdbcAccountDao.java` | Java DAO | 使用 JDBC 实现账号数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcActivityDao.java` | Java DAO | 使用 JDBC 实现活动数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcActivityRegistrationDao.java` | Java DAO | 使用 JDBC 实现活动报名数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcAdminDao.java` | Java DAO | 使用 JDBC 实现后台管理数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcClaimRequestDao.java` | Java DAO | 使用 JDBC 实现认领申请数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcExperienceDao.java` | Java DAO | 使用 JDBC 实现经验值数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcGoodsDao.java` | Java DAO | 使用 JDBC 实现商品数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcGoodsOrderDao.java` | Java DAO | 使用 JDBC 实现商品订单数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcHomeDao.java` | Java DAO | 使用 JDBC 实现首页数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcLostFoundDao.java` | Java DAO | 使用 JDBC 实现失物招领数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcMessageDao.java` | Java DAO | 使用 JDBC 实现站内通知数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcNoticeDao.java` | Java DAO | 使用 JDBC 实现公告数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcPostDao.java` | Java DAO | 使用 JDBC 实现帖子数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcPrivateConversationDao.java` | Java DAO | 使用 JDBC 实现私信会话数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcPrivateMessageDao.java` | Java DAO | 使用 JDBC 实现私信消息数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcProfileDao.java` | Java DAO | 使用 JDBC 实现个人主页数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcPublicUserProfileDao.java` | Java DAO | 使用 JDBC 实现公开用户主页数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcRememberTokenDao.java` | Java DAO | 使用 JDBC 实现记住登录令牌数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcReportDao.java` | Java DAO | 使用 JDBC 实现举报数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcSearchDao.java` | Java DAO | 使用 JDBC 实现全站搜索数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcSquareDao.java` | Java DAO | 使用 JDBC 实现校园广场数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/JdbcUserDao.java` | Java DAO | 使用 JDBC 实现用户数据的查询与持久化操作。 |
| `src/main/java/cn/campushub/dao/LostFoundDao.java` | Java DAO | 定义失物招领数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/MessageDao.java` | Java DAO | 定义站内通知数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/NoticeDao.java` | Java DAO | 定义公告数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/PostDao.java` | Java DAO | 定义帖子数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/PrivateConversationDao.java` | Java DAO | 定义私信会话数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/PrivateMessageDao.java` | Java DAO | 定义私信消息数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/ProfileDao.java` | Java DAO | 定义个人主页数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/PublicUserProfileDao.java` | Java DAO | 定义公开用户主页数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/RememberTokenDao.java` | Java DAO | 定义记住登录令牌数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/ReportDao.java` | Java DAO | 定义举报数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/SearchDao.java` | Java DAO | 定义全站搜索数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/SquareDao.java` | Java DAO | 定义校园广场数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/dao/UserDao.java` | Java DAO | 定义用户数据访问能力及业务层依赖的数据契约。 |
| `src/main/java/cn/campushub/filter/AdminAuthFilter.java` | Java 过滤器 | 拦截后台请求并校验当前用户是否具有管理员权限。 |
| `src/main/java/cn/campushub/filter/AuthFilter.java` | Java 过滤器 | 拦截受保护请求，确保用户登录后才能继续访问。 |
| `src/main/java/cn/campushub/filter/EncodingFilter.java` | Java 过滤器 | 统一请求与响应字符编码，避免中文参数和页面内容乱码。 |
| `src/main/java/cn/campushub/filter/RememberMeFilter.java` | Java 过滤器 | 在会话缺少登录用户时尝试通过持久令牌恢复登录状态。 |
| `src/main/java/cn/campushub/model/AccountCancelResult.java` | Java 模型 | 封装账号操作的处理结果与返回数据。 |
| `src/main/java/cn/campushub/model/Activity.java` | Java 模型 | 表示系统中的活动领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/ActivityRegistration.java` | Java 模型 | 表示系统中的活动报名领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/ActivityRegistrationResult.java` | Java 模型 | 封装活动报名操作的处理结果与返回数据。 |
| `src/main/java/cn/campushub/model/ActivityRegistrationVO.java` | Java 模型 | 聚合活动报名页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/ActivityVO.java` | Java 模型 | 聚合活动页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/Category.java` | Java 模型 | 表示系统中的Category领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/CheckinResult.java` | Java 模型 | 封装Checkin操作的处理结果与返回数据。 |
| `src/main/java/cn/campushub/model/ClaimCreateResult.java` | Java 模型 | 封装ClaimCreate操作的处理结果与返回数据。 |
| `src/main/java/cn/campushub/model/ClaimHandleResult.java` | Java 模型 | 封装ClaimHandle操作的处理结果与返回数据。 |
| `src/main/java/cn/campushub/model/ClaimRequest.java` | Java 模型 | 表示系统中的认领申请领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/Comment.java` | Java 模型 | 表示系统中的评论领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/CommentCreateResult.java` | Java 模型 | 封装评论操作的处理结果与返回数据。 |
| `src/main/java/cn/campushub/model/ExperienceInfo.java` | Java 模型 | 承载经验值相关的只读信息。 |
| `src/main/java/cn/campushub/model/ExperienceLog.java` | Java 模型 | 记录经验值变更明细及审计信息。 |
| `src/main/java/cn/campushub/model/FavoriteItemVO.java` | Java 模型 | 聚合FavoriteItem页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/Goods.java` | Java 模型 | 表示系统中的商品领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/GoodsOrder.java` | Java 模型 | 表示系统中的商品订单领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/HomeSidebarVO.java` | Java 模型 | 聚合首页页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/LostFound.java` | Java 模型 | 表示系统中的失物招领领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/Message.java` | Java 模型 | 表示系统中的站内通知领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/Notice.java` | Java 模型 | 表示系统中的公告领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/NotificationTarget.java` | Java 模型 | 描述Notification消息或跳转的目标信息。 |
| `src/main/java/cn/campushub/model/Post.java` | Java 模型 | 表示系统中的帖子领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/PostToggleResult.java` | Java 模型 | 封装帖子操作的处理结果与返回数据。 |
| `src/main/java/cn/campushub/model/PrivateConversation.java` | Java 模型 | 表示系统中的私信会话领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/PrivateMessage.java` | Java 模型 | 表示系统中的私信消息领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/ProfileActivityVO.java` | Java 模型 | 聚合个人主页页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/ProfileOverviewVO.java` | Java 模型 | 聚合个人主页页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/PublicUserProfile.java` | Java 模型 | 表示系统中的公开用户主页领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/PurchasedGoodsVO.java` | Java 模型 | 聚合PurchasedGoods页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/RememberToken.java` | Java 模型 | 表示系统中的记住登录令牌领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/ReportNotificationTarget.java` | Java 模型 | 描述举报消息或跳转的目标信息。 |
| `src/main/java/cn/campushub/model/SearchPageVO.java` | Java 模型 | 聚合全站搜索页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/SearchResultVO.java` | Java 模型 | 聚合全站搜索页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/SessionUser.java` | Java 模型 | 表示系统中的SessionUser领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/TradeOrderResult.java` | Java 模型 | 封装交易订单操作的处理结果与返回数据。 |
| `src/main/java/cn/campushub/model/User.java` | Java 模型 | 表示系统中的用户领域数据，并提供对应属性访问。 |
| `src/main/java/cn/campushub/model/UserCheckinStatsVO.java` | Java 模型 | 聚合用户页面展示所需的数据。 |
| `src/main/java/cn/campushub/model/UserCommentVO.java` | Java 模型 | 聚合用户页面展示所需的数据。 |
| `src/main/java/cn/campushub/service/AccountService.java` | Java 服务 | 编排账号业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/ActivityRegistrationService.java` | Java 服务 | 编排活动报名业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/ActivityService.java` | Java 服务 | 编排活动业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/AdminService.java` | Java 服务 | 编排后台管理业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/ClaimRequestService.java` | Java 服务 | 编排认领申请业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/GoodsService.java` | Java 服务 | 编排商品业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/HomeService.java` | Java 服务 | 编排首页业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/LostFoundService.java` | Java 服务 | 编排失物招领业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/MessageService.java` | Java 服务 | 编排站内通知业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/MockPayService.java` | Java 服务 | 编排MockPay业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/NoticeService.java` | Java 服务 | 编排公告业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/PostService.java` | Java 服务 | 编排帖子业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/PrivateMessageService.java` | Java 服务 | 编排私信消息业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/ProfileService.java` | Java 服务 | 编排个人主页业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/PublicUserProfileService.java` | Java 服务 | 编排公开用户主页业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/RememberMeService.java` | Java 服务 | 编排RememberMe业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/ReportService.java` | Java 服务 | 编排举报业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/SearchService.java` | Java 服务 | 编排全站搜索业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/ServiceResult.java` | Java 服务 | 统一封装业务操作的成功状态、提示消息和可选返回数据。 |
| `src/main/java/cn/campushub/service/SquareService.java` | Java 服务 | 编排校园广场业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/TradeOrderService.java` | Java 服务 | 编排交易订单业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/service/UserService.java` | Java 服务 | 编排用户业务规则、参数校验与数据访问操作。 |
| `src/main/java/cn/campushub/servlet/AccountCancelServlet.java` | Java Servlet | 接收账号的请求处理请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ActivityCancelRegisterServlet.java` | Java Servlet | 接收活动的取消报名请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ActivityCreateServlet.java` | Java Servlet | 接收活动的创建请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ActivityDeleteServlet.java` | Java Servlet | 接收活动的删除请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ActivityDetailServlet.java` | Java Servlet | 接收活动的详情查询请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ActivityJsonSupport.java` | Java Servlet | 为活动接口提供统一的 JSON 响应和参数处理辅助能力。 |
| `src/main/java/cn/campushub/servlet/ActivityRegisterServlet.java` | Java Servlet | 接收活动的报名请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ActivityStatusServlet.java` | Java Servlet | 接收活动的状态变更请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ActivityUpdateServlet.java` | Java Servlet | 接收活动的更新请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/AdminServlet.java` | Java Servlet | 处理后台管理页面请求并聚合管理端所需数据。 |
| `src/main/java/cn/campushub/servlet/CaptchaServlet.java` | Java Servlet | 生成图形验证码并将校验值保存到当前会话。 |
| `src/main/java/cn/campushub/servlet/CheckinServlet.java` | Java Servlet | 接收Checkin的请求处理请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ClaimRequestCreateServlet.java` | Java Servlet | 接收认领申请的创建请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ClaimRequestHandleServlet.java` | Java Servlet | 接收认领申请的审核处理请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/CommentLikeServlet.java` | Java Servlet | 接收评论的点赞切换请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/CommentServlet.java` | Java Servlet | 接收评论的请求处理请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ContentServlet.java` | Java Servlet | 根据前端路由参数分发并渲染各业务页面片段。 |
| `src/main/java/cn/campushub/servlet/GoodsCreateServlet.java` | Java Servlet | 接收商品的创建请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/GoodsDeleteServlet.java` | Java Servlet | 接收商品的删除请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/GoodsDetailServlet.java` | Java Servlet | 接收商品的详情查询请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/GoodsFavoriteServlet.java` | Java Servlet | 接收商品的收藏切换请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/GoodsJsonSupport.java` | Java Servlet | 为商品接口提供统一的 JSON 响应和参数处理辅助能力。 |
| `src/main/java/cn/campushub/servlet/GoodsStatusServlet.java` | Java Servlet | 接收商品的状态变更请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/GoodsUpdateServlet.java` | Java Servlet | 接收商品的更新请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/HomeDataServlet.java` | Java Servlet | 为首页异步请求聚合动态列表与侧栏统计数据。 |
| `src/main/java/cn/campushub/servlet/HomeServlet.java` | Java Servlet | 渲染应用主页面并准备当前登录用户等基础数据。 |
| `src/main/java/cn/campushub/servlet/ImageUploadServlet.java` | Java Servlet | 校验并保存用户上传的图片，返回可访问的图片地址。 |
| `src/main/java/cn/campushub/servlet/LoginServlet.java` | Java Servlet | 处理登录页面展示、凭据校验、会话建立与记住登录选项。 |
| `src/main/java/cn/campushub/servlet/LogoutServlet.java` | Java Servlet | 清理登录会话与持久登录令牌并完成退出跳转。 |
| `src/main/java/cn/campushub/servlet/LostFoundCreateServlet.java` | Java Servlet | 接收失物招领的创建请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/LostFoundDeleteServlet.java` | Java Servlet | 接收失物招领的删除请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/LostFoundDetailServlet.java` | Java Servlet | 接收失物招领的详情查询请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/LostFoundJsonSupport.java` | Java Servlet | 为失物招领接口提供统一的 JSON 响应和参数处理辅助能力。 |
| `src/main/java/cn/campushub/servlet/LostFoundStatusServlet.java` | Java Servlet | 接收失物招领的状态变更请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/LostFoundUpdateServlet.java` | Java Servlet | 接收失物招领的更新请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/MessageJsonSupport.java` | Java Servlet | 为站内通知接口提供统一的 JSON 响应和参数处理辅助能力。 |
| `src/main/java/cn/campushub/servlet/MessageReadAllServlet.java` | Java Servlet | 接收站内通知的全部标记已读请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/MessageReadServlet.java` | Java Servlet | 接收站内通知的标记已读请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/MessageUnreadCountServlet.java` | Java Servlet | 接收站内通知的未读数量查询请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/MockPayConfirmServlet.java` | Java Servlet | 接收MockPay的确认请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/MockPayPageServlet.java` | Java Servlet | 接收MockPay的页面展示请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/NoticeDetailServlet.java` | Java Servlet | 接收公告的详情查询请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PostCommentServlet.java` | Java Servlet | 接收帖子的请求处理请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PostDeleteServlet.java` | Java Servlet | 接收帖子的删除请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PostDetailServlet.java` | Java Servlet | 接收帖子的详情查询请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PostFavoriteServlet.java` | Java Servlet | 接收帖子的收藏切换请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PostJsonSupport.java` | Java Servlet | 为帖子接口提供统一的 JSON 响应和参数处理辅助能力。 |
| `src/main/java/cn/campushub/servlet/PostLikeServlet.java` | Java Servlet | 接收帖子的点赞切换请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PostServlet.java` | Java Servlet | 接收帖子的请求处理请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PostUpdateServlet.java` | Java Servlet | 接收帖子的更新请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PrivateMessageJsonSupport.java` | Java Servlet | 为私信消息接口提供统一的 JSON 响应和参数处理辅助能力。 |
| `src/main/java/cn/campushub/servlet/PrivateMessageSendServlet.java` | Java Servlet | 接收私信消息的发送请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PrivateMessageServlet.java` | Java Servlet | 接收私信消息的请求处理请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PrivateMessageThreadServlet.java` | Java Servlet | 接收私信消息的会话详情请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/ProfileUpdateServlet.java` | Java Servlet | 接收个人主页的更新请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/PublicUserProfileServlet.java` | Java Servlet | 接收公开用户主页的请求处理请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/RegisterServlet.java` | Java Servlet | 处理注册页面展示、验证码校验和新用户创建。 |
| `src/main/java/cn/campushub/servlet/ReportCreateServlet.java` | Java Servlet | 接收举报的创建请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/TradeOrderCreateServlet.java` | Java Servlet | 接收交易订单的创建请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/TradeOrderStatusServlet.java` | Java Servlet | 接收交易订单的状态变更请求，调用业务层并生成 HTTP 响应。 |
| `src/main/java/cn/campushub/servlet/TradeQrCodeServlet.java` | Java Servlet | 根据交易地址生成用于扫码支付或确认的二维码图片。 |
| `src/main/java/cn/campushub/util/HtmlUtils.java` | Java 工具 | 提供 HTML 特殊字符转义，降低页面输出中的注入风险。 |
| `src/main/java/cn/campushub/util/JdbcUtils.java` | Java 工具 | 统一创建数据库连接并处理 JDBC 资源相关基础操作。 |
| `src/main/java/cn/campushub/util/JsonUtils.java` | Java 工具 | 提供 JSON 字符串转义、响应输出和简单数据序列化能力。 |
| `src/main/java/cn/campushub/util/LevelUtils.java` | Java 工具 | 根据经验值计算用户等级、进度和升级阈值。 |
| `src/main/java/cn/campushub/util/PasswordUtils.java` | Java 工具 | 提供密码哈希生成与安全比对能力。 |
| `src/main/java/cn/campushub/util/SessionUtils.java` | Java 工具 | 统一读取和维护当前会话中的登录用户信息。 |
| `src/main/java/cn/campushub/util/TradeUrlUtils.java` | Java 工具 | 构造并校验站内交易流程使用的安全跳转地址。 |
| `src/main/java/cn/campushub/util/ValidationUtils.java` | Java 工具 | 集中提供常用文本、数字和业务参数校验方法。 |
| `src/main/resources/database/schema.sql` | SQL | 定义 CampusHub 数据库的基础表结构、约束、索引与初始化数据。 |
| `src/main/resources/database.properties` | Properties 配置 | 配置数据库驱动、连接地址、用户名和密码等运行参数。 |
| `src/main/webapp/css/activity.css` | CSS 样式 | 定义活动页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/admin.css` | CSS 样式 | 定义后台管理页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/auth.css` | CSS 样式 | 定义登录注册页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/image-upload.css` | CSS 样式 | 定义图片上传页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/index.css` | CSS 样式 | 定义应用主框架页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/lostfound.css` | CSS 样式 | 定义失物招领页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/messages.css` | CSS 样式 | 定义站内通知页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/notice-detail.css` | CSS 样式 | 定义公告详情页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/post.css` | CSS 样式 | 定义帖子页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/private-messages.css` | CSS 样式 | 定义私信页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/profile.css` | CSS 样式 | 定义个人主页页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/public-user-profile.css` | CSS 样式 | 定义公开用户主页页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/report.css` | CSS 样式 | 定义内容举报页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/css/trade.css` | CSS 样式 | 定义商品交易页面或组件的布局、配色与响应式样式。 |
| `src/main/webapp/index.jsp` | JSP 视图 | 渲染应用主框架，提供侧边导航、全局弹窗和前端资源入口。 |
| `src/main/webapp/js/activity-actions.js` | JavaScript | 处理活动发布、编辑、报名、取消报名、状态变更和删除等前端交互。 |
| `src/main/webapp/js/app-router.js` | JavaScript | 实现基于 URL Hash 的页面路由、异步内容加载和主界面初始化。 |
| `src/main/webapp/js/image-upload.js` | JavaScript | 封装图片选择、上传、预览、数量限制与失败提示交互。 |
| `src/main/webapp/js/index.js` | JavaScript | 初始化首页通用交互、导航状态和全局组件行为。 |
| `src/main/webapp/js/lostfound-actions.js` | JavaScript | 处理失物招领信息的发布、编辑、认领、状态变更和删除交互。 |
| `src/main/webapp/js/market.js` | JavaScript | 处理二手市场筛选、商品发布编辑、收藏和状态操作。 |
| `src/main/webapp/js/message-actions.js` | JavaScript | 处理站内通知的已读、全部已读和未读数量刷新。 |
| `src/main/webapp/js/post-detail.js` | JavaScript | 处理帖子详情页的评论、点赞、收藏、编辑与删除交互。 |
| `src/main/webapp/js/private-messages.js` | JavaScript | 处理私信会话列表、消息发送、轮询刷新与滚动定位。 |
| `src/main/webapp/js/profile-actions.js` | JavaScript | 处理个人资料编辑、头像上传、签到及个人内容操作。 |
| `src/main/webapp/js/register.js` | JavaScript | 处理注册表单校验、验证码刷新和密码可见性切换。 |
| `src/main/webapp/js/report.js` | JavaScript | 提供内容举报弹窗、原因校验和举报请求提交。 |
| `src/main/webapp/js/textarea-autosize.js` | JavaScript | 根据输入内容自动调整多行文本框高度。 |
| `src/main/webapp/js/trade.js` | JavaScript | 处理商品交易下单、模拟支付、二维码展示和订单状态轮询。 |
| `src/main/webapp/WEB-INF/views/activityDetail.jsp` | JSP 视图 | 渲染活动详情页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/admin/admin.jsp` | JSP 视图 | 渲染后台管理页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/admin/forbidden.jsp` | JSP 视图 | 渲染无权限提示页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/activity.jsp` | JSP 视图 | 渲染活动页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/development.jsp` | JSP 视图 | 渲染功能开发提示页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/favorites.jsp` | JSP 视图 | 渲染我的收藏页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/home-feed.jsp` | JSP 视图 | 渲染首页动态流页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/lostfound.jsp` | JSP 视图 | 渲染失物招领页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/market.jsp` | JSP 视图 | 渲染二手市场页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/messages.jsp` | JSP 视图 | 渲染站内通知页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/my-goods.jsp` | JSP 视图 | 渲染我的商品页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/post-list.jsp` | JSP 视图 | 渲染帖子列表页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/profile.jsp` | JSP 视图 | 渲染个人主页页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/purchased-goods.jsp` | JSP 视图 | 渲染已购商品页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/search.jsp` | JSP 视图 | 渲染全站搜索页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/fragments/square.jsp` | JSP 视图 | 渲染校园广场页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/goodsDetail.jsp` | JSP 视图 | 渲染商品详情页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/home.jsp` | JSP 视图 | 渲染应用首页页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/login.jsp` | JSP 视图 | 渲染登录页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/lostFoundDetail.jsp` | JSP 视图 | 渲染失物招领详情页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/noticeDetail.jsp` | JSP 视图 | 渲染公告详情页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/postDetail.jsp` | JSP 视图 | 渲染帖子详情页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/privateMessages.jsp` | JSP 视图 | 渲染私信列表页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/privateMessageThread.jsp` | JSP 视图 | 渲染私信会话页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/publicUserProfile.jsp` | JSP 视图 | 渲染公开用户主页页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/publishPost.jsp` | JSP 视图 | 渲染帖子发布页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/register.jsp` | JSP 视图 | 渲染用户注册页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/views/trade/mock-pay.jsp` | JSP 视图 | 渲染模拟支付页面，输出服务端数据与前端交互所需标记。 |
| `src/main/webapp/WEB-INF/web.xml` | XML 配置 | 配置 Web 应用的欢迎页、会话与容器级部署参数。 |
| `src/test/java/cn/campushub/service/ActivityServiceTest.java` | Java 测试 | 验证 活动相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/AdminServiceTest.java` | Java 测试 | 验证 后台管理相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/ClaimRequestServiceTest.java` | Java 测试 | 验证 认领申请相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/GoodsServiceTest.java` | Java 测试 | 验证 商品相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/HomeServiceTest.java` | Java 测试 | 验证 首页相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/LostFoundServiceTest.java` | Java 测试 | 验证 失物招领相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/MessageServiceTest.java` | Java 测试 | 验证 站内通知相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/PostServiceTest.java` | Java 测试 | 验证 帖子相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/PrivateMessageServiceTest.java` | Java 测试 | 验证 私信消息相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/ProfileServiceTest.java` | Java 测试 | 验证 个人主页相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/ReportServiceTest.java` | Java 测试 | 验证 举报相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/SearchServiceTest.java` | Java 测试 | 验证 全站搜索相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/SquareServiceTest.java` | Java 测试 | 验证 校园广场相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/service/UserServiceTest.java` | Java 测试 | 验证 用户相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/util/LevelUtilsTest.java` | Java 测试 | 验证 LevelUtils相关逻辑的正常路径、边界条件和失败场景。 |
| `src/test/java/cn/campushub/util/TradeUrlUtilsTest.java` | Java 测试 | 验证 TradeUrlUtils相关逻辑的正常路径、边界条件和失败场景。 |
