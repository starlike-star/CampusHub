/*
 Navicat Premium Data Transfer

 Source Server         : test
 Source Server Type    : MySQL
 Source Server Version : 80017 (8.0.17)
 Source Host           : localhost:3306
 Source Schema         : campushub

 Target Server Type    : MySQL
 Target Server Version : 80017 (8.0.17)
 File Encoding         : 65001

 Date: 18/06/2026 21:28:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account_cancel_logs
-- ----------------------------
DROP TABLE IF EXISTS `account_cancel_logs`;
CREATE TABLE `account_cancel_logs`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `username_snapshot` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname_snapshot` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `canceled_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_cancel_logs_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `account_cancel_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of account_cancel_logs
-- ----------------------------

-- ----------------------------
-- Table structure for activities
-- ----------------------------
DROP TABLE IF EXISTS `activities`;
CREATE TABLE `activities`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `location` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `start_time` datetime NULL DEFAULT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `deadline` datetime NULL DEFAULT NULL,
  `max_members` int(11) NULL DEFAULT 0,
  `current_members` int(11) NULL DEFAULT 0,
  `status` enum('signup','closed','ongoing','finished') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'signup',
  `created_by` bigint(20) NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_activities_creator`(`created_by` ASC) USING BTREE,
  CONSTRAINT `fk_activities_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activities
-- ----------------------------
INSERT INTO `activities` VALUES (1, 'Java Web 项目答辩模拟演练', '演示活动：用于展示活动发布、活动详情、报名和后台管理流程。', 'images/demo/demo-activity-web.png', '实训楼 305', '2026-06-21 16:36:16', '2026-06-21 18:36:16', '2026-06-20 16:36:16', 30, 7, 'signup', 11, '2026-06-18 16:36:16', '2026-06-18 20:37:47');
INSERT INTO `activities` VALUES (2, '校园二手集市线下交流会', '面向同学开放的二手交易交流活动，可现场交流商品信息、交易安全和平台使用经验。', 'images/demo/demo-goods-tablet.png', '学生活动中心大厅', '2026-06-23 16:36:16', '2026-06-23 19:36:16', '2026-06-22 16:36:16', 50, 12, 'signup', 14, '2026-06-18 16:36:16', '2026-06-18 16:36:16');

-- ----------------------------
-- Table structure for activity_registrations
-- ----------------------------
DROP TABLE IF EXISTS `activity_registrations`;
CREATE TABLE `activity_registrations`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `status` enum('registered','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'registered',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_user`(`activity_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `fk_registration_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_registration_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_registration_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_registrations
-- ----------------------------
INSERT INTO `activity_registrations` VALUES (1, 1, 8, 'registered', '2026-06-18 20:37:42');

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('post','goods','lost_found','activity') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `sort_order` int(11) NULL DEFAULT 0,
  `status` tinyint(4) NULL DEFAULT 1,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '校园生活', 'post', '校园日常、吐槽、分享', 1, 1, '2026-06-09 15:21:56');
INSERT INTO `categories` VALUES (2, '学习交流', 'post', '课程资料、考试复习、学习求助', 2, 1, '2026-06-09 15:21:56');
INSERT INTO `categories` VALUES (3, '二手交易', 'post', '二手商品相关讨论', 3, 1, '2026-06-09 15:21:56');
INSERT INTO `categories` VALUES (4, '电子数码', 'goods', '耳机、键盘、电脑配件等', 1, 1, '2026-06-09 15:21:56');
INSERT INTO `categories` VALUES (5, '教材资料', 'goods', '教材、资料、考试书籍', 2, 1, '2026-06-09 15:21:56');
INSERT INTO `categories` VALUES (6, '生活用品', 'goods', '宿舍和日常生活用品', 3, 1, '2026-06-09 15:21:56');
INSERT INTO `categories` VALUES (7, '证件卡片', 'lost_found', '校园卡、身份证、银行卡等', 1, 1, '2026-06-09 15:21:56');
INSERT INTO `categories` VALUES (8, '钥匙钱包', 'lost_found', '钥匙、钱包等物品', 2, 1, '2026-06-09 15:21:56');
INSERT INTO `categories` VALUES (9, '校园比赛', 'activity', '比赛竞赛类活动', 1, 1, '2026-06-09 15:21:56');
INSERT INTO `categories` VALUES (10, '社团活动', 'activity', '社团组织的活动', 2, 1, '2026-06-09 15:21:56');

-- ----------------------------
-- Table structure for checkins
-- ----------------------------
DROP TABLE IF EXISTS `checkins`;
CREATE TABLE `checkins`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `checkin_date` date NOT NULL,
  `points` int(11) NULL DEFAULT 5,
  `continuous_days` int(11) NULL DEFAULT 1,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_checkin_date`(`user_id` ASC, `checkin_date` ASC) USING BTREE,
  CONSTRAINT `fk_checkins_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of checkins
-- ----------------------------
INSERT INTO `checkins` VALUES (1, 8, '2026-06-18', 5, 1, '2026-06-18 15:45:18');

-- ----------------------------
-- Table structure for claim_requests
-- ----------------------------
DROP TABLE IF EXISTS `claim_requests`;
CREATE TABLE `claim_requests`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lost_found_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `handled_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_claim_lost_found`(`lost_found_id` ASC) USING BTREE,
  INDEX `fk_claim_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_claim_lost_found` FOREIGN KEY (`lost_found_id`) REFERENCES `lost_found` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_claim_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of claim_requests
-- ----------------------------

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `post_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `like_count` int(11) NULL DEFAULT 0,
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '1正常 0删除',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_comments_post`(`post_id` ASC) USING BTREE,
  INDEX `fk_comments_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_comments_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_comments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (1, 1, 10, '这个首页整合后很适合答辩演示，入口比较清楚。', 2, 1, '2026-06-18 10:55:43');
INSERT INTO `comments` VALUES (2, 1, 11, '二手市场和失物招领都能从这里跳转，测试路径更完整了。', 1, 1, '2026-06-18 11:55:43');
INSERT INTO `comments` VALUES (3, 1, 9, '建议答辩时先展示这个帖子，再进入各个功能模块。', 3, 1, '2026-06-18 12:55:43');
INSERT INTO `comments` VALUES (4, 2, 8, '这份复盘把数据库初始化和 Tomcat 部署都写清楚了。', 2, 1, '2026-06-18 08:55:43');
INSERT INTO `comments` VALUES (5, 2, 13, '我按清单走了一遍，注册登录和发帖评论都可以演示。', 4, 1, '2026-06-18 09:55:43');
INSERT INTO `comments` VALUES (6, 2, 14, '后台审核、通知和搜索也可以放进答辩演示顺序。', 1, 1, '2026-06-18 10:55:43');
INSERT INTO `comments` VALUES (7, 2, 9, '商品发布和模拟支付的截图可以放到报告功能测试部分。', 2, 1, '2026-06-18 11:55:43');
INSERT INTO `comments` VALUES (8, 2, 10, '这条帖子可以作为项目说明页的补充材料。', 1, 1, '2026-06-18 12:55:43');

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `target_id` bigint(20) NOT NULL,
  `target_type` enum('post','goods','lost_found','activity') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_favorite`(`user_id` ASC, `target_id` ASC, `target_type` ASC) USING BTREE,
  CONSTRAINT `fk_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorites
-- ----------------------------

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `category_id` bigint(20) NULL DEFAULT NULL,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `condition_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '全新/九成新/八成新等',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `trade_place` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `trade_method` enum('offline','online','both') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'offline' COMMENT '交易方式：offline线下交易 online线上付款 both均可',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` enum('on_sale','reserved','sold','off_shelf') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'on_sale',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_goods_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_goods_category`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fk_goods_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_goods_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES (1, 9, 4, '九成新华为平板，适合课堂记笔记', '演示商品：配套保护壳和电容笔，支持当面验机，适合答辩时展示发布商品、收藏和模拟支付流程。', 899.00, '九成新', 'images/demo/demo-goods-tablet.png', '信息楼一楼大厅', 'both', '站内私信联系', 'on_sale', '2026-06-18 16:36:16', '2026-06-18 16:36:16');
INSERT INTO `goods` VALUES (2, 13, 5, 'Java Web 课程资料合集', '包含 Servlet、JSP、JDBC、MySQL 实训笔记，适合期末复习和答辩前快速回顾。', 29.90, '资料整理', 'images/demo/demo-campus-post.png', '教学楼 A 区', 'offline', '站内私信联系', 'on_sale', '2026-06-18 16:36:16', '2026-06-18 16:36:16');

-- ----------------------------
-- Table structure for goods_orders
-- ----------------------------
DROP TABLE IF EXISTS `goods_orders`;
CREATE TABLE `goods_orders`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `buyer_id` bigint(20) NOT NULL COMMENT '买家用户ID',
  `seller_id` bigint(20) NOT NULL COMMENT '卖家用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '订单金额',
  `pay_method` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'mock_wechat' COMMENT '支付方式：mock_wechat模拟微信支付',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending_payment' COMMENT '订单状态：pending_payment待支付 paid已支付 cancelled已取消 expired已过期',
  `pay_token` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模拟支付确认token',
  `expire_at` datetime NULL DEFAULT NULL COMMENT '支付过期时间',
  `paid_at` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `cancelled_at` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  UNIQUE INDEX `pay_token`(`pay_token` ASC) USING BTREE,
  INDEX `idx_goods_orders_goods_id`(`goods_id` ASC) USING BTREE,
  INDEX `idx_goods_orders_buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_goods_orders_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_goods_orders_status`(`status` ASC) USING BTREE,
  INDEX `idx_goods_orders_pay_token`(`pay_token` ASC) USING BTREE,
  CONSTRAINT `fk_goods_orders_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_goods_orders_goods` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_goods_orders_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods_orders
-- ----------------------------
INSERT INTO `goods_orders` VALUES (1, 'CH202606182036580001', 1, 8, 9, 899.00, 'mock_wechat', 'pending_payment', 'c83b9f710eb7425b9c0ffbe918660a1e1c2a4077514c12fce2b0fcfcbd312161', '2026-06-18 20:46:59', NULL, NULL, '2026-06-18 20:36:58', '2026-06-18 20:36:58');

-- ----------------------------
-- Table structure for likes
-- ----------------------------
DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `target_id` bigint(20) NOT NULL,
  `target_type` enum('post','comment') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_like`(`user_id` ASC, `target_id` ASC, `target_type` ASC) USING BTREE,
  CONSTRAINT `fk_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of likes
-- ----------------------------
INSERT INTO `likes` VALUES (1, 8, 1, 'post', '2026-06-18 16:36:58');

-- ----------------------------
-- Table structure for lost_found
-- ----------------------------
DROP TABLE IF EXISTS `lost_found`;
CREATE TABLE `lost_found`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `category_id` bigint(20) NULL DEFAULT NULL,
  `type` enum('lost','found') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'lost失物 found招领',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `place` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `event_time` datetime NULL DEFAULT NULL,
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` enum('pending','claiming','completed','closed') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_lost_found_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_lost_found_category`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fk_lost_found_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_lost_found_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lost_found
-- ----------------------------
INSERT INTO `lost_found` VALUES (1, 10, 7, 'found', '校园一卡通', '拾到一张校园一卡通', '演示失物招领：在图书馆二楼自习区拾到一卡通，可通过认领流程提交信息。', '图书馆二楼自习区', '2026-06-16 16:36:16', 'images/demo/demo-lost-card.png', '站内私信联系', 'pending', '2026-06-18 16:36:16', '2026-06-18 16:36:16');

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('comment','like','favorite','claim','activity','system') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'system',
  `is_read` tinyint(4) NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_messages_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_messages_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (1, 8, '活动报名成功', '你已成功报名活动《Java Web 项目答辩模拟演练》。', 'activity', 0, '2026-06-18 20:37:42');
INSERT INTO `messages` VALUES (2, 11, '有人报名了你的活动', '徐翊文 报名了你发布的活动《Java Web 项目答辩模拟演练》。', 'activity', 0, '2026-06-18 20:37:42');
INSERT INTO `messages` VALUES (3, 8, '已取消活动报名', '你已取消报名活动《Java Web 项目答辩模拟演练》。', 'activity', 0, '2026-06-18 20:37:44');
INSERT INTO `messages` VALUES (4, 8, '活动报名成功', '你已成功报名活动《Java Web 项目答辩模拟演练》。', 'activity', 0, '2026-06-18 20:37:47');
INSERT INTO `messages` VALUES (5, 11, '有人报名了你的活动', '徐翊文 报名了你发布的活动《Java Web 项目答辩模拟演练》。', 'activity', 0, '2026-06-18 20:37:47');

-- ----------------------------
-- Table structure for notices
-- ----------------------------
DROP TABLE IF EXISTS `notices`;
CREATE TABLE `notices`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('teaching','life','activity','system','urgent') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'system',
  `is_top` tinyint(4) NULL DEFAULT 0,
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '1显示 0隐藏',
  `created_by` bigint(20) NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_notices_creator`(`created_by` ASC) USING BTREE,
  CONSTRAINT `fk_notices_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notices
-- ----------------------------
INSERT INTO `notices` VALUES (1, 'CampusHub 演示数据已准备', '本公告用于答辩演示：首页、公告详情和后台公告管理均可查看。', 'system', 1, 1, 8, '2026-06-18 16:36:16', '2026-06-18 16:36:16');

-- ----------------------------
-- Table structure for posts
-- ----------------------------
DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `category_id` bigint(20) NULL DEFAULT NULL,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '多张图片用逗号分隔',
  `topic` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `like_count` int(11) NULL DEFAULT 0,
  `comment_count` int(11) NULL DEFAULT 0,
  `favorite_count` int(11) NULL DEFAULT 0,
  `view_count` int(11) NULL DEFAULT 0,
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '1正常 0删除 2审核中',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_posts_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_posts_category`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fk_posts_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_posts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of posts
-- ----------------------------
INSERT INTO `posts` VALUES (1, 8, 1, '校园二手集市和失物招领已上线', '这里是 CampusHub 演示内容：同学们可以在广场交流校园生活，也可以发布二手商品、失物招领和活动报名信息。', 'images/demo/demo-campus-post.png', 'CampusHub公告', 13, 3, 4, 72, 1, '2026-06-18 16:36:16', '2026-06-18 20:36:37');
INSERT INTO `posts` VALUES (2, 12, 2, 'Java Web 实训答辩复盘帖', '整理了本次 CampusHub 项目的分层架构、数据库初始化、Tomcat 部署和答辩演示路径，方便同学们复习。', 'images/demo/demo-activity-web.png', '项目答辩', 18, 5, 7, 98, 1, '2026-06-18 16:36:16', '2026-06-18 16:56:16');

-- ----------------------------
-- Table structure for private_conversations
-- ----------------------------
DROP TABLE IF EXISTS `private_conversations`;
CREATE TABLE `private_conversations`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_a_id` bigint(20) NOT NULL,
  `user_b_id` bigint(20) NOT NULL,
  `last_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `last_message_at` datetime NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_conversation_pair`(`user_a_id` ASC, `user_b_id` ASC) USING BTREE,
  INDEX `idx_private_conversations_user_a`(`user_a_id` ASC) USING BTREE,
  INDEX `idx_private_conversations_user_b`(`user_b_id` ASC) USING BTREE,
  CONSTRAINT `private_conversations_ibfk_1` FOREIGN KEY (`user_a_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `private_conversations_ibfk_2` FOREIGN KEY (`user_b_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of private_conversations
-- ----------------------------

-- ----------------------------
-- Table structure for private_messages
-- ----------------------------
DROP TABLE IF EXISTS `private_messages`;
CREATE TABLE `private_messages`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `conversation_id` bigint(20) NOT NULL,
  `sender_id` bigint(20) NOT NULL,
  `receiver_id` bigint(20) NOT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_read` tinyint(4) NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_private_messages_conversation`(`conversation_id` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_private_messages_receiver_read`(`receiver_id` ASC, `is_read` ASC) USING BTREE,
  CONSTRAINT `private_messages_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `private_conversations` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `private_messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `private_messages_ibfk_3` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of private_messages
-- ----------------------------

-- ----------------------------
-- Table structure for remember_tokens
-- ----------------------------
DROP TABLE IF EXISTS `remember_tokens`;
CREATE TABLE `remember_tokens`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `selector` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` datetime NOT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `last_used_at` datetime NULL DEFAULT NULL,
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ip_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `selector`(`selector` ASC) USING BTREE,
  INDEX `idx_remember_tokens_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_remember_tokens_expires_at`(`expires_at` ASC) USING BTREE,
  CONSTRAINT `remember_tokens_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of remember_tokens
-- ----------------------------

-- ----------------------------
-- Table structure for reports
-- ----------------------------
DROP TABLE IF EXISTS `reports`;
CREATE TABLE `reports`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `target_id` bigint(20) NOT NULL,
  `target_type` enum('post','comment','goods','lost_found') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('pending','handled','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending',
  `handled_by` bigint(20) NULL DEFAULT NULL,
  `handled_at` datetime NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_reports_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_reports_handler`(`handled_by` ASC) USING BTREE,
  CONSTRAINT `fk_reports_handler` FOREIGN KEY (`handled_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_reports_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reports
-- ----------------------------

-- ----------------------------
-- Table structure for user_experience_logs
-- ----------------------------
DROP TABLE IF EXISTS `user_experience_logs`;
CREATE TABLE `user_experience_logs`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `change_value` int(11) NOT NULL,
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_exp_logs_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_exp_logs_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `user_experience_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_experience_logs
-- ----------------------------
INSERT INTO `user_experience_logs` VALUES (1, 8, 5, 'checkin', '每日签到获得经验', '2026-06-18 15:45:18');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'images/default-avatar.png',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `role` enum('student','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'student',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '1正常 0禁用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `experience` int(11) NULL DEFAULT 0 COMMENT '用户经验值',
  `level` int(11) NULL DEFAULT 1 COMMENT '用户等级',
  `canceled_at` datetime NULL DEFAULT NULL COMMENT '账号注销时间',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '账号注销原因',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$12$dUpIH.PD9mPptjAO73SM2.thhjHJNpdj9eFAHU5xIaK3NbruMHR3a', '系统管理员', '/uploads/avatar/aaf45b26-3020-4f94-9fe1-61984c2036e1.png', NULL, '信息中心', '系统管理', NULL, 'admin@campus.com', NULL, 'admin', 1, '2026-06-09 15:21:56', '2026-06-18 16:39:34', 5, 1, NULL, NULL);
INSERT INTO `users` VALUES (8, 'xuyiwen', '$2a$12$eE3yjMcZbPYYwaoDooVOpOD.0ZewsPreVnoyF7mpA5mAdE2auor.m', '徐翊文', '/uploads/avatar/e10bf7c1-4422-4bd1-9ca3-856946b2cae4.png', '230628720242', '信息工程', '大数据技术', '2024级', '123456789@ch.com', '13200000001', 'student', 1, '2026-06-11 16:59:34', '2026-06-18 16:38:29', 15, 1, NULL, NULL);
INSERT INTO `users` VALUES (9, 'yuwenlong', '$2a$12$RrLwy4f6FTn/KrLPwxrBeu9cKXwzxcA02SH5kFKRW8076DFb2frim', '余文龙', 'images/default-user.png', NULL, NULL, NULL, NULL, '987654321@ch.com', NULL, 'student', 1, '2026-06-11 17:00:56', '2026-06-11 17:00:56', 0, 1, NULL, NULL);
INSERT INTO `users` VALUES (10, 'student01', '$2a$12$OAkbBaaS1v6FQAwWhmvW..MW8oI40qXaZgB2J70h2krSqg8nFEMae', '林淑瑶', '/uploads/avatar/3a079551-9306-494f-8d66-dc9fbefc9e37.jpg', '2501001001', '信息工程', '数据科学', '2025级', '472910581@ch.com', '18238383838', 'student', 1, '2026-06-11 17:03:15', '2026-06-18 16:40:44', 5, 1, NULL, NULL);
INSERT INTO `users` VALUES (11, 'student02', '$2a$12$SH1KzAAsB1hFM43O3QtWHOgTlqNfUniRQMb3PqgiI.BiAkZILUk3q', '陈宇轩', '/uploads/avatar/ded7a785-3700-48a4-bc73-3ac6ef677d52.jpg', '2502001003', '电子工程', '集成电路', '2025级', '836502942@ch.com', '17293847382', 'student', 1, '2026-06-11 17:04:09', '2026-06-18 16:41:43', 5, 1, NULL, NULL);
INSERT INTO `users` VALUES (12, 'student03', '$2a$12$k5o.M87DVPTdCdIRJ7t7fuvdDZXhkxtAhWOBokmUwDwTJPf74WHzq', '苏婉晴', '/uploads/avatar/2673f25b-b57c-4874-b015-04e9b0cfe8f2.jpg', '2502002011', '信息工程', '通信工程', '2025级', '194827365@ch.com', '13438463845', 'student', 1, '2026-06-11 17:05:16', '2026-06-18 16:42:27', 0, 1, NULL, NULL);
INSERT INTO `users` VALUES (13, 'student04', '$2a$12$xray4kbYRzREG0rQ07qs8un.S0bGRZzpPk1U2OsUbgqP701N1Uvvy', '赵明泽', '/uploads/avatar/3db56960-31fc-4243-bb96-9931cd2b7296.jpg', '2504001004', '智能制造', '机器人工程', '2025级', '605184723@ch.com', '18428374832', 'student', 1, '2026-06-11 17:06:19', '2026-06-18 16:43:04', 5, 1, NULL, NULL);
INSERT INTO `users` VALUES (14, 'student05', '$2a$12$HpWTKULcgrJ233eZaqNdcOSJMPWRw2wel0WMo6FjIC/YWayHX0CgO', '许安然', '/uploads/avatar/dd2285fc-d3cc-4272-9f0f-19b5a8483b3c.jpg', '2503001007', '新能源', '储能科学', '2025级', '283947156@ch.com', '19438374912', 'student', 1, '2026-06-11 17:07:30', '2026-06-18 16:43:31', 5, 1, NULL, NULL);

-- ----------------------------
-- Table structure for users_backup_before_password_fix
-- ----------------------------
DROP TABLE IF EXISTS `users_backup_before_password_fix`;
CREATE TABLE `users_backup_before_password_fix`  (
  `id` bigint(20) NOT NULL DEFAULT 0,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'images/default-avatar.png',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `role` enum('student','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'student',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '1正常 0禁用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users_backup_before_password_fix
-- ----------------------------
INSERT INTO `users_backup_before_password_fix` VALUES (1, 'admin', '123456', '', '系统管理员', 'images/avatar-admin.png', NULL, '信息中心', '系统管理', NULL, 'admin@campus.com', NULL, 'admin', 1, '2026-06-09 15:21:56', '2026-06-09 15:21:56');
INSERT INTO `users_backup_before_password_fix` VALUES (2, 'student01', '123456', '', '林小夏', 'images/avatar1.png', '20240001', '计算机学院', '软件工程', '2024级', 'student01@campus.com', NULL, 'student', 1, '2026-06-09 15:21:56', '2026-06-09 15:21:56');
INSERT INTO `users_backup_before_password_fix` VALUES (3, 'student02', '123456', '', '周一鸣', 'images/avatar2.png', '20240002', '计算机学院', '计算机科学与技术', '2024级', 'student02@campus.com', NULL, 'student', 1, '2026-06-09 15:21:56', '2026-06-09 15:21:56');
INSERT INTO `users_backup_before_password_fix` VALUES (4, 'student03', '123456', '', '陈可乐', 'images/avatar3.png', '20240003', '外国语学院', '英语', '2023级', 'student03@campus.com', NULL, 'student', 1, '2026-06-09 15:21:56', '2026-06-09 15:21:56');
INSERT INTO `users_backup_before_password_fix` VALUES (5, 'xuyiwen', '$2a$12$qLqroBKniHCJZW9O3LiN2uTN25JPuVNoLykhc9yPhWQnBXio7A866', '', 'Star', 'images/default-avatar.png', NULL, NULL, NULL, NULL, '123456789@ch.com', NULL, 'student', 1, '2026-06-09 16:49:01', '2026-06-09 16:49:01');
INSERT INTO `users_backup_before_password_fix` VALUES (6, 'yuwenlong', '$2a$12$/D3hdAZ3EViz9VrmTzN42ObQfJ4dMMHGxZhtYvxZli8UtrUK3GT/W', '', 'Hengye', 'images/default-avatar.png', NULL, NULL, NULL, NULL, '987654321@ch.com', NULL, 'student', 1, '2026-06-09 16:57:30', '2026-06-09 16:57:30');

SET FOREIGN_KEY_CHECKS = 1;
