# CampusHub

基于 JSP、Javax Servlet、JDBC 和 MySQL 的校园综合社区基础项目。

## 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8
- Tomcat 9.0+

## 初始化数据库

在 MySQL 中执行：

```sql
SOURCE src/main/resources/database/schema.sql;
```

默认连接配置位于 `src/main/resources/database.properties`。部署时推荐使用环境变量：

```text
CAMPUSHUB_DB_URL
CAMPUSHUB_DB_USERNAME
CAMPUSHUB_DB_PASSWORD
```

## 构建与部署

```shell
mvn clean package
```

将 `target/CampusHub.war` 部署到 Tomcat 9.0+。

## 已提供路由

- `GET /`：首页
- `GET|POST /login`：登录
- `GET|POST /register`：注册
- `POST /logout`：退出
- `GET /api/home`：首页数据占位接口
- `/profile`、`/favorites`、`/publish/*`、`/api/private/*`：登录保护路径

登录用户保存在 Session 的 `loginUser` 属性中，类型为 `SessionUser`。
