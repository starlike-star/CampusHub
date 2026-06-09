CREATE DATABASE IF NOT EXISTS campushub
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE campushub;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户主键',
    username VARCHAR(20) NOT NULL COMMENT '登录用户名，统一存储为小写',
    email VARCHAR(120) NOT NULL COMMENT '登录邮箱，统一存储为小写',
    password_hash VARCHAR(60) NOT NULL COMMENT 'BCrypt 密码哈希',
    nickname VARCHAR(20) NOT NULL COMMENT '社区昵称',
    avatar_url VARCHAR(255) NULL COMMENT '头像地址',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT 'USER / ADMIN',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE / DISABLED / LOCKED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email),
    KEY idx_users_status (status),
    KEY idx_users_created_at (created_at)
) ENGINE=InnoDB COMMENT='CampusHub 用户表';
