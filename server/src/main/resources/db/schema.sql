-- 婚纱摄影管理系统数据库
CREATE DATABASE IF NOT EXISTS hunsha DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hunsha;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户 1-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 套餐表
CREATE TABLE IF NOT EXISTS `package` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
    `name` VARCHAR(100) NOT NULL COMMENT '套餐名称',
    `description` TEXT DEFAULT NULL COMMENT '套餐描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图',
    `images` JSON DEFAULT NULL COMMENT '套餐图片列表',
    `includes` JSON DEFAULT NULL COMMENT '套餐包含内容',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `is_hot` TINYINT NOT NULL DEFAULT 0 COMMENT '是否热门: 0-否 1-是',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `package_id` BIGINT DEFAULT NULL COMMENT '套餐ID',
    `package_name` VARCHAR(100) DEFAULT NULL COMMENT '套餐名称',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `status` VARCHAR(20) NOT NULL DEFAULT '待确认' COMMENT '状态: 待确认/已确认/拍摄中/已完成/已取消',
    `shoot_date` DATE DEFAULT NULL COMMENT '拍摄日期',
    `shoot_time` VARCHAR(50) DEFAULT NULL COMMENT '拍摄时间段',
    `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_shoot_date` (`shoot_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 作品表
CREATE TABLE IF NOT EXISTS `portfolio` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作品ID',
    `title` VARCHAR(100) NOT NULL COMMENT '作品标题',
    `description` TEXT DEFAULT NULL COMMENT '作品描述',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图',
    `images` JSON DEFAULT NULL COMMENT '作品图片列表',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类: 婚纱照/写真/旅拍等',
    `is_recommended` TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐: 0-否 1-是',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-隐藏 1-显示',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品表';

-- 评价表
CREATE TABLE IF NOT EXISTS `review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `rating` TINYINT NOT NULL COMMENT '评分: 1-5',
    `content` TEXT DEFAULT NULL COMMENT '评价内容',
    `images` JSON DEFAULT NULL COMMENT '评价图片列表',
    `is_featured` TINYINT NOT NULL DEFAULT 0 COMMENT '是否精选: 0-否 1-是',
    `admin_reply` TEXT DEFAULT NULL COMMENT '商家回复',
    `reply_at` DATETIME DEFAULT NULL COMMENT '回复时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-隐藏 1-显示',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- 档期表
CREATE TABLE IF NOT EXISTS `schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '档期ID',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
    `title` VARCHAR(100) NOT NULL COMMENT '档期标题',
    `shoot_date` DATE NOT NULL COMMENT '拍摄日期',
    `time_slot` VARCHAR(50) DEFAULT NULL COMMENT '时间段',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '拍摄地点',
    `photographer` VARCHAR(50) DEFAULT NULL COMMENT '摄影师',
    `status` VARCHAR(20) NOT NULL DEFAULT '待确认' COMMENT '状态: 待确认/已确认/进行中/已完成/已取消',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_shoot_date` (`shoot_date`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档期表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS `chat_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
    `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `type` VARCHAR(20) NOT NULL DEFAULT 'text' COMMENT '消息类型: text/image/order',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_sender` (`sender_id`),
    KEY `idx_receiver` (`receiver_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';
