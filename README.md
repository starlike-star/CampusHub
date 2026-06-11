# CampusHub 校园综合社区平台
CampusHub 是一个面向在校学生与平台管理员的校园综合社区系统。项目以校园广场为核心，整合帖子互动、二手交易、失物招领、校园活动、公告通知、消息私信、全局搜索、签到等级和后台治理等功能，为校园信息交流与生活服务提供统一入口。

## 功能模块

### 普通用户端

- 用户注册、验证码登录、退出登录、Remember Me 免密登录
- 首页动态、校园地图、校园公告、活动推荐、失物速递
- 校园广场、帖子发布、详情查看、评论、点赞、收藏与举报
- 二手商品发布、筛选、编辑、收藏、状态管理和卖家私信
- 线下交易与模拟线上扫码支付，包含订单和商品状态流转
- 失物与招领信息发布、认领申请及申请处理
- 校园活动发布、报名、取消报名和人数限制
- 每日签到、连续签到、经验值和用户等级
- 个人资料、个人内容、购买记录和公开用户主页
- 系统消息、未读消息、用户一对一私信
- 帖子、商品、失物招领、活动和公告的全局搜索
- 密码验证、资料匿名化和关联数据清理的账号软注销

### 管理员端

- 平台数据总览
- 用户启用、禁用和密码重置
- 帖子、商品、失物招领和活动状态管理
- 公告发布、编辑、显示、隐藏和置顶
- 举报查询、处理、驳回及违规内容治理
- 后台路径权限校验与管理员消息提醒

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 后端 | Java 21、Servlet 4.0、JSP、JDBC |
| 前端 | HTML、CSS、JavaScript、Fetch API |
| 数据库 | MySQL 8 |
| Web 容器 | Apache Tomcat 9 |
| 构建工具 | Maven 3.9+ |
| 安全与工具 | BCrypt、Session、Cookie、验证码、Remember Me Token |
| 二维码 | ZXing 3.5.3 |
| 测试 | JUnit 5 |

项目未使用 Spring、Vue 或 React，采用传统 JavaWeb 技术路线。页面使用 SPA-Lite 结构，由 `ContentServlet` 动态加载 JSP Fragment，并通过 Fetch API 完成局部异步交互。

## 系统架构

```text
浏览器
  │
  ├── JSP / CSS / JavaScript
  │
Filter（编码、登录恢复、用户鉴权、管理员鉴权）
  │
Servlet（请求处理与页面调度）
  │
Service（参数校验与业务规则）
  │
DAO / JDBC（SQL 与事务）
  │
MySQL
```

后端按 `Servlet -> Service -> DAO -> Model` 分层组织。数据库操作使用 `PreparedStatement`，模拟支付、活动报名、签到经验和账号注销等多表业务通过 JDBC 事务保证一致性。

## 项目结构

```text
CampusHub/
├─ database/
│  └─ migrations/             # 数据库增量脚本
├─ src/
│  ├─ main/
│  │  ├─ java/cn/campushub/
│  │  │  ├─ config/           # 数据库配置
│  │  │  ├─ constant/         # 常量
│  │  │  ├─ dao/              # 数据访问接口与 JDBC 实现
│  │  │  ├─ filter/           # 编码、认证和权限过滤器
│  │  │  ├─ model/            # 实体、VO 和业务结果模型
│  │  │  ├─ service/          # 业务服务
│  │  │  ├─ servlet/          # Web 请求控制器
│  │  │  └─ util/             # 校验、密码、JSON、二维码等工具
│  │  ├─ resources/
│  │  │  ├─ database.properties
│  │  │  └─ database/schema.sql
│  │  └─ webapp/
│  │     ├─ WEB-INF/views/    # JSP 页面与 Fragment
│  │     ├─ css/
│  │     ├─ js/
│  │     └─ images/
│  └─ test/java/cn/campushub/ # Service 与工具类单元测试
├─ database-schema.md         # 当前完整数据库字段设计
├─ pom.xml
└─ README.md
```

## 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8.0+
- Tomcat 9.0+

本项目使用 `javax.servlet`，应部署到 Tomcat 9。Tomcat 10 及以上默认使用 `jakarta.servlet`，不能直接兼容。

## 数据库准备

数据库名为 `campushub`，主要包含以下业务表：

```text
users、categories、posts、comments、likes、favorites、goods、
lost_found、claim_requests、activities、activity_registrations、
notices、checkins、messages、reports、remember_tokens、
private_conversations、private_messages、user_experience_logs、
goods_orders、account_cancel_logs
```

1. 在 MySQL 中创建 `campushub` 数据库。
2. 按 [database-schema.md](database-schema.md) 中的定义创建业务表。
3. 如数据库中尚无商品交易方式字段，执行：

   ```sql
   SOURCE database/migrations/20260610_add_goods_trade_method.sql;
   ```

> 注意：`src/main/resources/database/schema.sql` 是早期基础结构，未包含当前项目使用的全部表和字段。复现当前版本时，应以 `database-schema.md` 和 `database/migrations/` 为准。

项目未内置固定管理员账号。可先通过注册页面创建普通用户，再在数据库中将其角色改为管理员：

```sql
UPDATE users
SET role = 'admin'
WHERE username = '你的用户名';
```

## 数据库连接配置

默认配置文件位于：

```text
src/main/resources/database.properties
```

建议在本地修改配置，或使用环境变量覆盖，避免提交真实数据库密码：

| 环境变量 | 说明 |
| --- | --- |
| `CAMPUSHUB_DB_DRIVER` | JDBC 驱动，通常为 `com.mysql.cj.jdbc.Driver` |
| `CAMPUSHUB_DB_URL` | MySQL JDBC 地址 |
| `CAMPUSHUB_DB_USERNAME` | 数据库用户名 |
| `CAMPUSHUB_DB_PASSWORD` | 数据库密码 |
| `CAMPUSHUB_PUBLIC_BASE_URL` | 手机扫码访问模拟支付页时使用的公网或局域网基础地址 |

PowerShell 配置示例：

```powershell
$env:CAMPUSHUB_DB_URL="jdbc:mysql://localhost:3306/campushub?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
$env:CAMPUSHUB_DB_USERNAME="root"
$env:CAMPUSHUB_DB_PASSWORD="your_password"
```

## 构建与测试

运行单元测试：

```shell
mvn test
```

打包 WAR：

```shell
mvn clean package
```

构建成功后生成：

```text
target/CampusHub.war
```

## 部署与访问

1. 将 `target/CampusHub.war` 复制到 Tomcat 9 的 `webapps` 目录。
2. 启动 Tomcat。
3. 浏览器访问：

   ```text
   http://localhost:8080/CampusHub/
   ```

也可以在 IntelliJ IDEA 中配置 Tomcat 9，并部署 `CampusHub:war exploded` Artifact。

常用入口：

| 页面 | 地址 |
| --- | --- |
| 系统首页 | `/CampusHub/` |
| 登录 | `/CampusHub/login` |
| 注册 | `/CampusHub/register` |
| 后台管理 | `/CampusHub/admin` |
| 私信列表 | `/CampusHub/private-messages` |

## 模拟扫码交易说明

模拟交易仅用于课程项目演示，不接入微信支付 API，不涉及真实资金。

如需使用手机扫描二维码：

1. 确保手机与部署服务器处于同一局域网。
2. 将 `CAMPUSHUB_PUBLIC_BASE_URL` 设置为手机可访问的地址，例如：

   ```powershell
   $env:CAMPUSHUB_PUBLIC_BASE_URL="http://192.168.1.100:8080"
   ```

3. 重启 Tomcat 后重新创建订单二维码。

二维码不能使用手机无法访问的 `localhost` 地址。

## 图片上传说明

- 单文件最大 5 MB，请求最大 6 MB。
- 支持头像、帖子、商品、失物招领和活动封面等图片。
- 图片默认保存到 Tomcat 展开应用下的 `uploads` 目录。
- 重新部署或清理 Tomcat 应用目录可能导致上传文件丢失。

当前上传方案适合课程演示；生产部署应改为 WAR 外部持久化目录或对象存储。

## 安全与数据处理

- 密码使用 BCrypt 哈希校验。
- 登录状态由 Session 管理。
- Remember Me Cookie 不保存明文密码。
- SQL 使用参数化查询，降低注入风险。
- 普通用户与管理员路径分别通过过滤器鉴权。
- 内容删除和账号注销以状态变更、匿名化和软删除为主。
- 已完成订单和举报记录在账号注销后保留，便于数据追溯。

## 项目文档
- [数据库结构说明](database-schema.md)
- [代码文件说明](CODE_FILES.md)

## 说明

本项目用于 JavaWeb 综合实训与课程设计展示。模拟支付、应用内上传目录和单机 Session 等实现均以教学演示为目标，不应直接用于生产环境。
