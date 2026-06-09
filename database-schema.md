# CampusHub 数据库结构说明

## 项目说明

CampusHub 是一个基于 JSP + Servlet + JDBC + MySQL 的校园综合社区系统。

当前阶段数据库结构已经确定，后端 DAO、Service、Servlet 必须严格按照本文档字段编写，不允许自行发明字段名。

如果代码中出现本文档未定义的字段，例如 `password_hash`、`avatar_url`，请改代码，而不是要求数据库新增字段。

---

## 数据库名称

```sql
campushub
users 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    avatar VARCHAR(255) DEFAULT 'images/default-avatar.png',
    student_no VARCHAR(50),
    college VARCHAR(100),
    major VARCHAR(100),
    grade VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(30),
    role ENUM('student', 'admin') DEFAULT 'student',
    status TINYINT DEFAULT 1 COMMENT '1正常 0禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

注意：

密码字段叫 password
头像字段叫 avatar
不存在 password_hash
不存在 avatar_url
categories 分类表
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    type ENUM('post', 'goods', 'lost_found', 'activity') NOT NULL,
    description VARCHAR(255),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
posts 帖子表
CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    images TEXT COMMENT '多张图片用逗号分隔',
    topic VARCHAR(100),
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1正常 0删除 2审核中',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
comments 评论表
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    like_count INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1正常 0删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id)
);
likes 点赞表
CREATE TABLE likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    target_type ENUM('post', 'comment') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_like (user_id, target_id, target_type),
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id)
);
favorites 收藏表
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    target_type ENUM('post', 'goods', 'lost_found', 'activity') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_favorite (user_id, target_id, target_type),
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id)
);
goods 二手商品表
CREATE TABLE goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    condition_level VARCHAR(50) COMMENT '全新/九成新/八成新等',
    images TEXT,
    trade_place VARCHAR(150),
    contact VARCHAR(100),
    status ENUM('on_sale', 'reserved', 'sold', 'off_shelf') DEFAULT 'on_sale',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_goods_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_goods_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
lost_found 失物招领表
CREATE TABLE lost_found (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    type ENUM('lost', 'found') NOT NULL COMMENT 'lost失物 found招领',
    item_name VARCHAR(100) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    place VARCHAR(150),
    event_time DATETIME,
    images TEXT,
    contact VARCHAR(100),
    status ENUM('pending', 'claiming', 'completed', 'closed') DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lost_found_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_lost_found_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
claim_requests 认领申请表
CREATE TABLE claim_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    lost_found_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    message TEXT,
    contact VARCHAR(100),
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    handled_at DATETIME,
    CONSTRAINT fk_claim_lost_found FOREIGN KEY (lost_found_id) REFERENCES lost_found(id),
    CONSTRAINT fk_claim_user FOREIGN KEY (user_id) REFERENCES users(id)
);
activities 活动表
CREATE TABLE activities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    cover_image VARCHAR(255),
    location VARCHAR(150),
    start_time DATETIME,
    end_time DATETIME,
    deadline DATETIME,
    max_members INT DEFAULT 0,
    current_members INT DEFAULT 0,
    status ENUM('signup', 'closed', 'ongoing', 'finished') DEFAULT 'signup',
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_activities_creator FOREIGN KEY (created_by) REFERENCES users(id)
);
activity_registrations 活动报名表
CREATE TABLE activity_registrations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('registered', 'cancelled') DEFAULT 'registered',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    CONSTRAINT fk_registration_activity FOREIGN KEY (activity_id) REFERENCES activities(id),
    CONSTRAINT fk_registration_user FOREIGN KEY (user_id) REFERENCES users(id)
);
notices 公告表
CREATE TABLE notices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    type ENUM('teaching', 'life', 'activity', 'system', 'urgent') DEFAULT 'system',
    is_top TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1显示 0隐藏',
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_notices_creator FOREIGN KEY (created_by) REFERENCES users(id)
);
checkins 每日签到表
CREATE TABLE checkins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    checkin_date DATE NOT NULL,
    points INT DEFAULT 5,
    continuous_days INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_checkin_date (user_id, checkin_date),
    CONSTRAINT fk_checkins_user FOREIGN KEY (user_id) REFERENCES users(id)
);
messages 消息通知表
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    type ENUM('comment', 'like', 'favorite', 'claim', 'activity', 'system') DEFAULT 'system',
    is_read TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_messages_user FOREIGN KEY (user_id) REFERENCES users(id)
);
reports 举报表
CREATE TABLE reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    target_type ENUM('post', 'comment', 'goods', 'lost_found') NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status ENUM('pending', 'handled', 'rejected') DEFAULT 'pending',
    handled_by BIGINT,
    handled_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reports_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reports_handler FOREIGN KEY (handled_by) REFERENCES users(id)
);
后端代码修改要求

请检查以下代码：

User 模型类
JdbcUserDao
UserService
LoginServlet
RegisterServlet
首页相关 DAO
首页相关 Servlet

要求：

所有 SQL 字段必须和本文档一致。
注册用户时插入 users 表字段：
username
password
nickname
avatar
student_no
college
major
grade
email
phone
role
status
登录时使用 username 和 password 校验。
不要使用 password_hash 字段。
不要使用 avatar_url 字段。
如果代码中已有密码加密逻辑，可以先暂时保留，但最终存储字段仍然是 password。
如果模型类中存在 passwordHash 或 avatarUrl，请改为 password 和 avatar。
如果 JSP 页面引用头像字段，也统一使用 avatar。
不要新增课程表，不要实现今日课程相关功能。
首页右侧只保留：
每日签到
校园公告
活动推荐
失物速递
当前禁止使用的字段

以下字段不属于当前数据库结构，后端代码不得使用：

password_hash
avatar_url
course_id
course_name
classroom
schedule_time
teacher_name
当前首页模块

首页应该包含：

顶部导航栏
左侧导航栏
校园地图卡片
发帖快捷框
帖子信息流
每日签到
校园公告
活动推荐
失物速递

首页不包含：

天气模块
今日课程模块
今日校园数据统计模块
```
