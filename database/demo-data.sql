-- Optional CampusHub demo content for an existing local database.
-- This script assumes these users already exist:
-- XuYiWen, YuWenLong, student01, student02, student03, student04, student05.

USE campushub;

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
