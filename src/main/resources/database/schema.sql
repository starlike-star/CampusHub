-- CampusHub complete MySQL 8 schema for first-time local deployment.
-- This file is aligned with database-schema.md and the current JDBC DAOs.

CREATE DATABASE IF NOT EXISTS campushub
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE campushub;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    avatar VARCHAR(255) DEFAULT 'images/default-user.png',
    student_no VARCHAR(50),
    college VARCHAR(100),
    major VARCHAR(100),
    grade VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(30),
    role ENUM('student', 'admin') DEFAULT 'student',
    status TINYINT DEFAULT 1 COMMENT '1 active, 0 disabled, 2 canceled',
    experience INT DEFAULT 0,
    level INT DEFAULT 1,
    canceled_at DATETIME NULL,
    cancel_reason VARCHAR(255) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_users_status (status),
    KEY idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    type ENUM('post', 'goods', 'lost_found', 'activity') NOT NULL,
    description VARCHAR(255),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_categories_type_name (type, name),
    KEY idx_categories_type_status (type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    images TEXT,
    topic VARCHAR(100),
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1 visible, 0 deleted, 2 pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories(id),
    KEY idx_posts_user_status (user_id, status),
    KEY idx_posts_category_status (category_id, status),
    KEY idx_posts_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    like_count INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1 visible, 0 deleted',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id),
    KEY idx_comments_post_status (post_id, status),
    KEY idx_comments_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    target_type ENUM('post', 'comment') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_like (user_id, target_id, target_type),
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    target_type ENUM('post', 'goods', 'lost_found', 'activity') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_favorite (user_id, target_id, target_type),
    KEY idx_favorites_target (target_type, target_id),
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    condition_level VARCHAR(50),
    images TEXT,
    trade_place VARCHAR(150),
    trade_method ENUM('offline', 'online', 'both') DEFAULT 'offline',
    contact VARCHAR(100),
    status ENUM('on_sale', 'reserved', 'sold', 'off_shelf') DEFAULT 'on_sale',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_goods_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_goods_category FOREIGN KEY (category_id) REFERENCES categories(id),
    KEY idx_goods_user_status (user_id, status),
    KEY idx_goods_category_status (category_id, status),
    KEY idx_goods_trade_method (trade_method),
    KEY idx_goods_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS lost_found (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    type ENUM('lost', 'found') NOT NULL,
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
    CONSTRAINT fk_lost_found_category FOREIGN KEY (category_id) REFERENCES categories(id),
    KEY idx_lost_found_user_status (user_id, status),
    KEY idx_lost_found_category_status (category_id, status),
    KEY idx_lost_found_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS claim_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    lost_found_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    message TEXT,
    contact VARCHAR(100),
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    handled_at DATETIME,
    CONSTRAINT fk_claim_lost_found FOREIGN KEY (lost_found_id) REFERENCES lost_found(id),
    CONSTRAINT fk_claim_user FOREIGN KEY (user_id) REFERENCES users(id),
    KEY idx_claim_lost_found_status (lost_found_id, status),
    KEY idx_claim_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS activities (
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
    CONSTRAINT fk_activities_creator FOREIGN KEY (created_by) REFERENCES users(id),
    KEY idx_activities_status (status),
    KEY idx_activities_start_time (start_time),
    KEY idx_activities_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS activity_registrations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('registered', 'cancelled') DEFAULT 'registered',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    CONSTRAINT fk_registration_activity FOREIGN KEY (activity_id) REFERENCES activities(id),
    CONSTRAINT fk_registration_user FOREIGN KEY (user_id) REFERENCES users(id),
    KEY idx_activity_registrations_user (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    type ENUM('teaching', 'life', 'activity', 'system', 'urgent') DEFAULT 'system',
    is_top TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT '1 visible, 0 hidden',
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_notices_creator FOREIGN KEY (created_by) REFERENCES users(id),
    KEY idx_notices_status_top (status, is_top),
    KEY idx_notices_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS checkins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    checkin_date DATE NOT NULL,
    points INT DEFAULT 5,
    continuous_days INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_checkin_date (user_id, checkin_date),
    CONSTRAINT fk_checkins_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    type ENUM('comment', 'like', 'favorite', 'claim', 'activity', 'report', 'system') DEFAULT 'system',
    is_read TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_messages_user FOREIGN KEY (user_id) REFERENCES users(id),
    KEY idx_messages_user_read (user_id, is_read),
    KEY idx_messages_user_type (user_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reports (
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
    CONSTRAINT fk_reports_handler FOREIGN KEY (handled_by) REFERENCES users(id),
    KEY idx_reports_status (status),
    KEY idx_reports_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS private_conversations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_a_id BIGINT NOT NULL,
    user_b_id BIGINT NOT NULL,
    last_message VARCHAR(500) NULL,
    last_message_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_conversation_pair (user_a_id, user_b_id),
    CONSTRAINT fk_private_conversations_user_a FOREIGN KEY (user_a_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_private_conversations_user_b FOREIGN KEY (user_b_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_private_conversations_user_a (user_a_id),
    KEY idx_private_conversations_user_b (user_b_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS private_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    is_read TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_private_messages_conversation FOREIGN KEY (conversation_id) REFERENCES private_conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_private_messages_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_private_messages_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_private_messages_conversation (conversation_id, created_at),
    KEY idx_private_messages_receiver_read (receiver_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS remember_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    selector VARCHAR(64) NOT NULL UNIQUE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_used_at DATETIME NULL,
    user_agent VARCHAR(255) NULL,
    ip_address VARCHAR(64) NULL,
    CONSTRAINT fk_remember_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_remember_tokens_user_id (user_id),
    KEY idx_remember_tokens_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_experience_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    change_value INT NOT NULL,
    source VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_exp_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_user_exp_logs_user_id (user_id),
    KEY idx_user_exp_logs_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS goods_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    goods_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    pay_method VARCHAR(30) DEFAULT 'mock_wechat',
    status VARCHAR(30) DEFAULT 'pending_payment',
    pay_token VARCHAR(128) NOT NULL UNIQUE,
    expire_at DATETIME NULL,
    paid_at DATETIME NULL,
    cancelled_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_goods_orders_goods FOREIGN KEY (goods_id) REFERENCES goods(id) ON DELETE CASCADE,
    CONSTRAINT fk_goods_orders_buyer FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_goods_orders_seller FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_goods_orders_goods_id (goods_id),
    KEY idx_goods_orders_buyer_id (buyer_id),
    KEY idx_goods_orders_seller_id (seller_id),
    KEY idx_goods_orders_status (status),
    KEY idx_goods_orders_pay_token (pay_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS account_cancel_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    username_snapshot VARCHAR(50) NOT NULL,
    nickname_snapshot VARCHAR(50),
    cancel_reason VARCHAR(255),
    canceled_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(64),
    user_agent VARCHAR(255),
    CONSTRAINT fk_account_cancel_logs_user FOREIGN KEY (user_id) REFERENCES users(id),
    KEY idx_account_cancel_logs_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO categories (name, type, description, sort_order, status)
VALUES
    ('校园生活', 'post', 'Campus life and discussion posts', 10, 1),
    ('学习交流', 'post', 'Study notes and learning discussion', 20, 1),
    ('社团活动', 'post', 'Club and student organization topics', 30, 1),
    ('电子数码', 'goods', 'Electronics and digital devices', 10, 1),
    ('教材资料', 'goods', 'Textbooks, notes, and study materials', 20, 1),
    ('生活用品', 'goods', 'Daily campus supplies', 30, 1),
    ('运动器材', 'goods', 'Sports and fitness items', 40, 1),
    ('免费赠送', 'goods', 'Free giveaway items', 50, 1),
    ('证件卡片', 'lost_found', 'Cards, IDs, and certificates', 10, 1),
    ('电子设备', 'lost_found', 'Phones, computers, and accessories', 20, 1),
    ('生活物品', 'lost_found', 'Daily personal belongings', 30, 1),
    ('讲座培训', 'activity', 'Lectures and training activities', 10, 1),
    ('文体竞赛', 'activity', 'Sports, arts, and competitions', 20, 1),
    ('志愿服务', 'activity', 'Volunteer service activities', 30, 1)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    sort_order = VALUES(sort_order),
    status = VALUES(status);

INSERT INTO posts (
    user_id, category_id, title, content, images, topic, like_count,
    comment_count, favorite_count, view_count, status
)
SELECT
    u.id,
    (SELECT id FROM categories WHERE type = 'post' ORDER BY sort_order LIMIT 1),
    '校园二手集市和失物招领已上线',
    '这里是 CampusHub 演示内容：同学们可以在广场交流校园生活，也可以发布二手商品、失物招领和活动报名信息。',
    'images/demo/demo-campus-post.png',
    'CampusHub公告',
    12,
    3,
    4,
    68,
    1
FROM users u
WHERE u.username = 'XuYiWen'
  AND NOT EXISTS (SELECT 1 FROM posts WHERE title = '校园二手集市和失物招领已上线');

INSERT INTO posts (
    user_id, category_id, title, content, images, topic, like_count,
    comment_count, favorite_count, view_count, status
)
SELECT
    u.id,
    (SELECT id FROM categories WHERE type = 'post' AND name = '学习交流' LIMIT 1),
    'Java Web 实训答辩复盘帖',
    '整理了本次 CampusHub 项目的分层架构、数据库初始化、Tomcat 部署和答辩演示路径，方便同学们复习。',
    'images/demo/demo-activity-web.png',
    '项目答辩',
    18,
    5,
    7,
    96,
    1
FROM users u
WHERE u.username = 'student03'
  AND NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Java Web 实训答辩复盘帖');

INSERT INTO goods (
    user_id, category_id, title, description, price, condition_level,
    images, trade_place, trade_method, contact, status
)
SELECT
    u.id,
    (SELECT id FROM categories WHERE type = 'goods' ORDER BY sort_order LIMIT 1),
    '九成新华为平板，适合课堂记笔记',
    '演示商品：配套保护壳和电容笔，支持当面验机，适合答辩时展示发布商品、收藏和模拟支付流程。',
    899.00,
    '九成新',
    'images/demo/demo-goods-tablet.png',
    '信息楼一楼大厅',
    'both',
    '站内私信联系',
    'on_sale'
FROM users u
WHERE u.username = 'YuWenLong'
  AND NOT EXISTS (SELECT 1 FROM goods WHERE title = '九成新华为平板，适合课堂记笔记');

INSERT INTO goods (
    user_id, category_id, title, description, price, condition_level,
    images, trade_place, trade_method, contact, status
)
SELECT
    u.id,
    (SELECT id FROM categories WHERE type = 'goods' AND name = '教材资料' LIMIT 1),
    'Java Web 课程资料合集',
    '包含 Servlet、JSP、JDBC、MySQL 实训笔记，适合期末复习和答辩前快速回顾。',
    29.90,
    '资料整理',
    'images/demo/demo-campus-post.png',
    '教学楼 A 区',
    'offline',
    '站内私信联系',
    'on_sale'
FROM users u
WHERE u.username = 'student04'
  AND NOT EXISTS (SELECT 1 FROM goods WHERE title = 'Java Web 课程资料合集');

INSERT INTO lost_found (
    user_id, category_id, type, item_name, title, description, place,
    event_time, images, contact, status
)
SELECT
    u.id,
    (SELECT id FROM categories WHERE type = 'lost_found' ORDER BY sort_order LIMIT 1),
    'found',
    '校园一卡通',
    '拾到一张校园一卡通',
    '演示失物招领：在图书馆二楼自习区拾到一卡通，可通过认领流程提交信息。',
    '图书馆二楼自习区',
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    'images/demo/demo-lost-card.png',
    '站内私信联系',
    'pending'
FROM users u
WHERE u.username = 'student01'
  AND NOT EXISTS (SELECT 1 FROM lost_found WHERE title = '拾到一张校园一卡通');

INSERT INTO activities (
    title, content, cover_image, location, start_time, end_time, deadline,
    max_members, current_members, status, created_by
)
SELECT
    'Java Web 项目答辩模拟演练',
    '演示活动：用于展示活动发布、活动详情、报名和后台管理流程。',
    'images/demo/demo-activity-web.png',
    '实训楼 305',
    DATE_ADD(NOW(), INTERVAL 3 DAY),
    DATE_ADD(NOW(), INTERVAL 3 DAY) + INTERVAL 2 HOUR,
    DATE_ADD(NOW(), INTERVAL 2 DAY),
    30,
    6,
    'signup',
    u.id
FROM users u
WHERE u.username = 'student02'
  AND NOT EXISTS (SELECT 1 FROM activities WHERE title = 'Java Web 项目答辩模拟演练');

INSERT INTO activities (
    title, content, cover_image, location, start_time, end_time, deadline,
    max_members, current_members, status, created_by
)
SELECT
    '校园二手集市线下交流会',
    '面向同学开放的二手交易交流活动，可现场交流商品信息、交易安全和平台使用经验。',
    'images/demo/demo-goods-tablet.png',
    '学生活动中心大厅',
    DATE_ADD(NOW(), INTERVAL 5 DAY),
    DATE_ADD(NOW(), INTERVAL 5 DAY) + INTERVAL 3 HOUR,
    DATE_ADD(NOW(), INTERVAL 4 DAY),
    50,
    12,
    'signup',
    u.id
FROM users u
WHERE u.username = 'student05'
  AND NOT EXISTS (SELECT 1 FROM activities WHERE title = '校园二手集市线下交流会');

INSERT INTO notices (title, content, type, is_top, status, created_by)
SELECT
    'CampusHub 演示数据已准备',
    '本公告用于答辩演示：首页、公告详情和后台公告管理均可查看。',
    'system',
    1,
    1,
    u.id
FROM users u
WHERE u.username = 'XuYiWen'
  AND NOT EXISTS (SELECT 1 FROM notices WHERE title = 'CampusHub 演示数据已准备');
-- Keep demo post counters consistent with the comments shown on the detail page.
INSERT INTO comments (post_id, user_id, content, like_count, status, created_at)
SELECT p.id, commenter.id, '这个首页整合后很适合答辩演示，入口比较清楚。', 2, 1, DATE_SUB(NOW(), INTERVAL 6 HOUR)
FROM posts p
JOIN users author ON author.id = p.user_id AND author.username = 'XuYiWen'
JOIN users commenter ON commenter.username = 'student01'
WHERE p.images = 'images/demo/demo-campus-post.png'
  AND NOT EXISTS (
      SELECT 1 FROM comments c
      WHERE c.post_id = p.id
        AND c.user_id = commenter.id
        AND c.content = '这个首页整合后很适合答辩演示，入口比较清楚。'
  );

INSERT INTO comments (post_id, user_id, content, like_count, status, created_at)
SELECT p.id, commenter.id, '二手市场和失物招领都能从这里跳转，测试路径更完整了。', 1, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR)
FROM posts p
JOIN users author ON author.id = p.user_id AND author.username = 'XuYiWen'
JOIN users commenter ON commenter.username = 'student02'
WHERE p.images = 'images/demo/demo-campus-post.png'
  AND NOT EXISTS (
      SELECT 1 FROM comments c
      WHERE c.post_id = p.id
        AND c.user_id = commenter.id
        AND c.content = '二手市场和失物招领都能从这里跳转，测试路径更完整了。'
  );

INSERT INTO comments (post_id, user_id, content, like_count, status, created_at)
SELECT p.id, commenter.id, '建议答辩时先展示这个帖子，再进入各个功能模块。', 3, 1, DATE_SUB(NOW(), INTERVAL 4 HOUR)
FROM posts p
JOIN users author ON author.id = p.user_id AND author.username = 'XuYiWen'
JOIN users commenter ON commenter.username = 'YuWenLong'
WHERE p.images = 'images/demo/demo-campus-post.png'
  AND NOT EXISTS (
      SELECT 1 FROM comments c
      WHERE c.post_id = p.id
        AND c.user_id = commenter.id
        AND c.content = '建议答辩时先展示这个帖子，再进入各个功能模块。'
  );

INSERT INTO comments (post_id, user_id, content, like_count, status, created_at)
SELECT p.id, commenter.id, '这份复盘把数据库初始化和 Tomcat 部署都写清楚了。', 2, 1, DATE_SUB(NOW(), INTERVAL 8 HOUR)
FROM posts p
JOIN users author ON author.id = p.user_id AND author.username = 'student03'
JOIN users commenter ON commenter.username = 'XuYiWen'
WHERE p.images = 'images/demo/demo-activity-web.png'
  AND NOT EXISTS (
      SELECT 1 FROM comments c
      WHERE c.post_id = p.id
        AND c.user_id = commenter.id
        AND c.content = '这份复盘把数据库初始化和 Tomcat 部署都写清楚了。'
  );

INSERT INTO comments (post_id, user_id, content, like_count, status, created_at)
SELECT p.id, commenter.id, '我按清单走了一遍，注册登录和发帖评论都可以演示。', 4, 1, DATE_SUB(NOW(), INTERVAL 7 HOUR)
FROM posts p
JOIN users author ON author.id = p.user_id AND author.username = 'student03'
JOIN users commenter ON commenter.username = 'student04'
WHERE p.images = 'images/demo/demo-activity-web.png'
  AND NOT EXISTS (
      SELECT 1 FROM comments c
      WHERE c.post_id = p.id
        AND c.user_id = commenter.id
        AND c.content = '我按清单走了一遍，注册登录和发帖评论都可以演示。'
  );

INSERT INTO comments (post_id, user_id, content, like_count, status, created_at)
SELECT p.id, commenter.id, '后台审核、通知和搜索也可以放进答辩演示顺序。', 1, 1, DATE_SUB(NOW(), INTERVAL 6 HOUR)
FROM posts p
JOIN users author ON author.id = p.user_id AND author.username = 'student03'
JOIN users commenter ON commenter.username = 'student05'
WHERE p.images = 'images/demo/demo-activity-web.png'
  AND NOT EXISTS (
      SELECT 1 FROM comments c
      WHERE c.post_id = p.id
        AND c.user_id = commenter.id
        AND c.content = '后台审核、通知和搜索也可以放进答辩演示顺序。'
  );

INSERT INTO comments (post_id, user_id, content, like_count, status, created_at)
SELECT p.id, commenter.id, '商品发布和模拟支付的截图可以放到报告功能测试部分。', 2, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR)
FROM posts p
JOIN users author ON author.id = p.user_id AND author.username = 'student03'
JOIN users commenter ON commenter.username = 'YuWenLong'
WHERE p.images = 'images/demo/demo-activity-web.png'
  AND NOT EXISTS (
      SELECT 1 FROM comments c
      WHERE c.post_id = p.id
        AND c.user_id = commenter.id
        AND c.content = '商品发布和模拟支付的截图可以放到报告功能测试部分。'
  );

INSERT INTO comments (post_id, user_id, content, like_count, status, created_at)
SELECT p.id, commenter.id, '这条帖子可以作为项目说明页的补充材料。', 1, 1, DATE_SUB(NOW(), INTERVAL 4 HOUR)
FROM posts p
JOIN users author ON author.id = p.user_id AND author.username = 'student03'
JOIN users commenter ON commenter.username = 'student01'
WHERE p.images = 'images/demo/demo-activity-web.png'
  AND NOT EXISTS (
      SELECT 1 FROM comments c
      WHERE c.post_id = p.id
        AND c.user_id = commenter.id
        AND c.content = '这条帖子可以作为项目说明页的补充材料。'
  );

UPDATE posts p
JOIN users author ON author.id = p.user_id AND author.username = 'XuYiWen'
SET p.comment_count = (
    SELECT COUNT(*) FROM comments c
    WHERE c.post_id = p.id AND c.status = 1
)
WHERE p.images = 'images/demo/demo-campus-post.png';

UPDATE posts p
JOIN users author ON author.id = p.user_id AND author.username = 'student03'
SET p.comment_count = (
    SELECT COUNT(*) FROM comments c
    WHERE c.post_id = p.id AND c.status = 1
)
WHERE p.images = 'images/demo/demo-activity-web.png';
