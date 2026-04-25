/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80400
 Source Host           : localhost:3306
 Source Schema         : clothes_recycle

 Target Server Type    : MySQL
 Target Server Version : 80400
 File Encoding         : 65001

 Date: 25/04/2026 17:37:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员手机号',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员密码',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员姓名',
  `role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员角色：SUPER_ADMIN/CAMPUS_ADMIN',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '所属校区ID，超级管理员可为空',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_admin_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, '13203071697', '123456', '系统管理员', 'SUPER_ADMIN', NULL, 1, '2026-03-22 14:01:53', '2026-03-22 14:52:32', 0);
INSERT INTO `admin` VALUES (2, '13203071698', '123456', '陶潇', 'CAMPUS_ADMIN', 1, 1, '2026-03-23 15:49:21', '2026-03-23 15:49:21', 0);

-- ----------------------------
-- Table structure for campus
-- ----------------------------
DROP TABLE IF EXISTS `campus`;
CREATE TABLE `campus`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '校区名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '校区地址',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_campus_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '校区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of campus
-- ----------------------------
INSERT INTO `campus` VALUES (1, '主校区', '湖南省长沙市韶山南路498号', 1, '主校区', '2026-03-22 14:01:53', '2026-03-22 14:01:53', 0);
INSERT INTO `campus` VALUES (2, '测试校区', '测试校区', 1, '测试校区', '2026-03-22 14:58:53', '2026-03-22 14:58:53', 0);
INSERT INTO `campus` VALUES (3, '东校区', '湖南省长沙市韶山南路498号', 1, '东校区', '2026-03-24 12:40:36', '2026-03-24 12:40:36', 0);
INSERT INTO `campus` VALUES (4, '北校区', '湖南省长沙市韶山南路498号', 1, '北校区', '2026-03-24 12:40:46', '2026-03-24 12:40:46', 0);

-- ----------------------------
-- Table structure for drop_point
-- ----------------------------
DROP TABLE IF EXISTS `drop_point`;
CREATE TABLE `drop_point`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `campus_id` bigint NOT NULL COMMENT '所属校区ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回收点名称',
  `location_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '位置描述',
  `open_time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '开放时间',
  `manager_note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '负责人说明',
  `stock_count` int NOT NULL DEFAULT 0 COMMENT '当前存量',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_drop_point_campus_id`(`campus_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '回收点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of drop_point
-- ----------------------------
INSERT INTO `drop_point` VALUES (1, 1, '主校区东门回收点', '东门快递站旁', '09:00-18:00', '工作日值守', 0, 1, '2026-03-22 14:01:53', '2026-03-22 14:01:53', 0);
INSERT INTO `drop_point` VALUES (2, 1, '测试回收点', '测试回收点', '不开放', '测试回收点', 0, 0, '2026-03-22 14:58:39', '2026-03-22 14:58:39', 0);
INSERT INTO `drop_point` VALUES (3, 2, '测试001', '测试001', '测试001', '测试001', 0, 1, '2026-03-23 14:57:30', '2026-03-23 14:57:30', 0);
INSERT INTO `drop_point` VALUES (4, 2, '测试002', '测试002', '测试002', '测试002', 0, 1, '2026-03-23 14:57:43', '2026-03-23 14:57:43', 0);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `item_id` bigint NOT NULL COMMENT '物品ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_favorite_user_item`(`user_id` ASC, `item_id` ASC) USING BTREE,
  INDEX `idx_favorite_item_id`(`item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------

-- ----------------------------
-- Table structure for gift
-- ----------------------------
DROP TABLE IF EXISTS `gift`;
CREATE TABLE `gift`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '礼品名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '礼品描述',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '礼品图片URL',
  `point_cost` int NOT NULL COMMENT '所需积分',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `exchanged_count` int NOT NULL DEFAULT 0 COMMENT '已兑换数量',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1上架，0下架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分礼品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gift
-- ----------------------------
INSERT INTO `gift` VALUES (1, '环保帆布袋', '校园联名环保帆布袋', 'http://192.168.30.129:9000/clothes-recycle/item/708d9923-8f6b-4f2f-8b49-bdadb3901f86.png', 120, 99, 1, 1, '2026-03-22 14:01:53', '2026-03-22 14:01:53', 0);
INSERT INTO `gift` VALUES (2, '不锈钢保温杯', '保温杯 480ml', 'http://192.168.30.129:9000/clothes-recycle/item/f2410a31-672b-4d7b-849f-26f356d5662a.jpg', 260, 79, 1, 1, '2026-03-22 14:01:53', '2026-03-22 14:01:53', 0);
INSERT INTO `gift` VALUES (3, '电风扇', '电风扇', 'http://192.168.30.129:9000/clothes-recycle/item/9ae836bd-159b-4aaa-a543-9af97d7bff21.avif', 10, 9, 1, 1, '2026-03-24 12:29:02', '2026-03-24 12:29:02', 0);

-- ----------------------------
-- Table structure for gift_exchange
-- ----------------------------
DROP TABLE IF EXISTS `gift_exchange`;
CREATE TABLE `gift_exchange`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gift_id` bigint NOT NULL COMMENT '礼品ID',
  `user_id` bigint NOT NULL COMMENT '兑换用户ID',
  `point_cost` int NOT NULL COMMENT '兑换时积分',
  `exchange_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '兑换码',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/VERIFIED/CANCELLED',
  `verify_time` datetime NULL DEFAULT NULL COMMENT '核销时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gift_exchange_code`(`exchange_code` ASC) USING BTREE,
  INDEX `idx_gift_exchange_user_time`(`user_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_gift_exchange_gift_id`(`gift_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '礼品兑换记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gift_exchange
-- ----------------------------
INSERT INTO `gift_exchange` VALUES (1, 1, 1, 120, 'EX26032411472715025', 'VERIFIED', '2026-03-24 11:50:18', '2026-03-24 11:47:27', '2026-03-24 11:47:27', 0);
INSERT INTO `gift_exchange` VALUES (2, 3, 1, 10, 'EX26032412292111039', 'VERIFIED', '2026-03-24 12:29:44', '2026-03-24 12:29:22', '2026-03-24 12:29:22', 0);
INSERT INTO `gift_exchange` VALUES (3, 2, 1, 260, 'EX26032412292412264', 'VERIFIED', '2026-03-24 12:29:47', '2026-03-24 12:29:24', '2026-03-24 12:29:24', 0);

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '发布用户ID',
  `campus_id` bigint NOT NULL COMMENT '所属校区ID',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物品标题',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物品品类',
  `gender_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '性别类型：男/女/通用',
  `size_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '尺码类型',
  `condition_level` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '新旧程度',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物品描述',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '封面图URL',
  `acquire_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '获取方式：FREE/POINT',
  `point_price` int NOT NULL DEFAULT 0 COMMENT '积分价格',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物品状态',
  `audit_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '审核原因或下架原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_item_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_item_campus_status`(`campus_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of item
-- ----------------------------
INSERT INTO `item` VALUES (1, 1, 1, '白色纯棉圆领T恤', '上衣', '通用', 'M', '九成新', '穿过两次，洗涤干净，无破损无污渍，适合日常穿着', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400', 'FREE', 0, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:44:08', 0);
INSERT INTO `item` VALUES (2, 1, 1, '黑色修身牛仔裤', '裤子', '男', '30', '八成新', '版型好看，轻微水洗痕迹，正常穿着无影响', 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400', 'POINT', 50, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:44:10', 0);
INSERT INTO `item` VALUES (3, 1, 1, '女士碎花连衣裙', '裙子', '女', 'S', '全新', '买来没穿过，吊牌还在，尺码偏小转让', 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=400', 'POINT', 80, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (4, 1, 2, '米色羊毛大衣', '外套', '女', 'M', '九成新', '韩版宽松版型，保暖性好，只穿过一季', 'https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?w=400', 'POINT', 120, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (5, 1, 2, '灰色连帽运动卫衣', '上衣', '通用', 'L', '七成新', '日常运动穿着，有轻微起球，价格实惠', 'https://img.alicdn.com/imgextra/i1/39600061/O1CN01kB3yf11CJzBajGpqq_!!39600061.gif', 'FREE', 0, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:56:04', 0);
INSERT INTO `item` VALUES (6, 1, 1, '白色正装衬衫', '上衣', '男', 'L', '九成新', '面试用过一次，熨烫整齐，适合正式场合', 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=400', 'POINT', 30, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (7, 1, 3, '蓝色牛仔短裤', '裤子', '女', 'M', '八成新', '夏季款，版型显腿长，洗涤干净无异味', 'https://images.unsplash.com/photo-1604176354204-9268737828e4?w=400', 'FREE', 0, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (8, 1, 2, '黑色机车皮夹克', '外套', '男', 'XL', '八成新', '机车风格，质感好，适合秋冬季节', 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400', 'POINT', 150, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (9, 1, 1, '藏青色西装外套', '外套', '男', 'M', '九成新', '正式场合穿着，版型挺括，只穿过两次', 'https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=400', 'POINT', 100, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (10, 1, 3, '粉色针织毛衣', '上衣', '女', 'S', '全新', '网购尺码不合适，全新未穿，柔软舒适', 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=400', 'POINT', 60, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (11, 1, 2, '卡其色工装裤', '裤子', '男', '32', '八成新', '多口袋设计，宽松版型，适合户外活动', 'https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=400', 'FREE', 0, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (12, 1, 1, '白色蕾丝上衣', '上衣', '女', 'M', '九成新', '法式风格，精致蕾丝花纹，穿过一次', 'https://images.unsplash.com/photo-1485462537746-965f33f7f6a7?w=400', 'POINT', 45, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (13, 1, 3, '深蓝色羽绒服', '外套', '通用', 'L', '八成新', '保暖性极好，填充量足，适合冬季', 'https://images.unsplash.com/photo-1544923246-77307dd654cb?w=400', 'POINT', 180, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (14, 1, 1, '格纹休闲衬衫', '上衣', '男', 'M', '九成新', '经典格纹图案，棉质面料透气舒适', 'https://images.unsplash.com/photo-1589310243389-96a5483213a8?w=400', 'FREE', 0, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (15, 1, 2, '黑色紧身瑜伽裤', '裤子', '女', 'S', '九成新', '高腰设计，弹力好，适合运动健身', 'https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=400', 'POINT', 40, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (16, 1, 1, '条纹海魂衫', '上衣', '通用', 'M', '八成新', '经典海军条纹，棉质面料，日常百搭', 'https://images.unsplash.com/photo-1503341504253-dff4815485f1?w=400', 'FREE', 0, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (17, 1, 3, '卡其色风衣', '外套', '女', 'M', '九成新', '经典双排扣设计，防风保暖，气质优雅', 'https://images.unsplash.com/photo-1548624313-0396c75e4b1a?w=400', 'POINT', 130, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (18, 1, 2, '运动短裤', '裤子', '男', 'L', '七成新', '速干面料，轻盈透气，适合跑步健身', 'https://images.unsplash.com/photo-1544923246-77307dd654cb?w=800', 'FREE', 0, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:56:24', 0);
INSERT INTO `item` VALUES (19, 1, 1, '米白色针织开衫', '上衣', '女', 'M', '全新', '慵懒风格，宽松版型，秋季必备单品', 'https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=400', 'POINT', 70, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:45:24', 0);
INSERT INTO `item` VALUES (20, 1, 3, '黑色休闲西裤', '裤子', '男', '31', '九成新', '商务休闲两用，版型笔挺，面料垂感好', 'https://img.alicdn.com/i3/2298236993/O1CN0142Qzd121WqQOU1i8C_!!2298236993.jpg', 'POINT', 55, 'ON_SHELF', '审核通过', '2026-03-24 12:41:12', '2026-03-24 12:55:44', 0);
INSERT INTO `item` VALUES (21, 1, 1, '帆布袋', '配饰', '通用', 'M', '九成新', '帆布袋', 'http://192.168.30.129:9000/clothes-recycle/item/25cc2182-2920-4cb8-a31e-2a1381349bb1.png', 'POINT', 18, 'ON_SHELF', '审核通过', '2026-03-24 12:43:55', '2026-04-07 16:32:08', 0);

-- ----------------------------
-- Table structure for item_image
-- ----------------------------
DROP TABLE IF EXISTS `item_image`;
CREATE TABLE `item_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_id` bigint NOT NULL COMMENT '物品ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort_no` int NOT NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_item_image_item_id`(`item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物品图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of item_image
-- ----------------------------
INSERT INTO `item_image` VALUES (1, 1, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (2, 1, 'https://images.unsplash.com/photo-1503341504253-dff4815485f1?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (3, 1, 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=800', 2, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (4, 2, 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (5, 2, 'https://images.unsplash.com/photo-1555689502-c4b22d76c56f?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (6, 3, 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (7, 3, 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (8, 3, 'https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=800', 2, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (9, 4, 'https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (10, 4, 'https://images.unsplash.com/photo-1548624313-0396c75e4b1a?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (11, 5, 'https://img.alicdn.com/imgextra/i1/39600061/O1CN01kB3yf11CJzBajGpqq_!!39600061.gif', 0, '2026-03-24 12:41:12', '2026-03-24 12:46:35', 0);
INSERT INTO `item_image` VALUES (12, 5, 'https://img.alicdn.com/imgextra/i1/39600061/O1CN01kB3yf11CJzBajGpqq_!!39600061.gif', 1, '2026-03-24 12:41:12', '2026-03-24 12:46:54', 0);
INSERT INTO `item_image` VALUES (13, 6, 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (14, 6, 'https://images.unsplash.com/photo-1607345366928-199ea26cfe3e?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (15, 7, 'https://images.unsplash.com/photo-1604176354204-9268737828e4?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (16, 7, 'https://images.unsplash.com/photo-1591195853828-11db59a44f43?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (17, 8, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (18, 8, 'https://images.unsplash.com/photo-1520975954732-35dd22299614?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (19, 9, 'https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (20, 9, 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (21, 10, 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (22, 10, 'https://images.unsplash.com/photo-1485462537746-965f33f7f6a7?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (23, 11, 'https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (24, 11, 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (25, 12, 'https://images.unsplash.com/photo-1485462537746-965f33f7f6a7?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (26, 12, 'https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (27, 13, 'https://images.unsplash.com/photo-1544923246-77307dd654cb?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (28, 13, 'https://images.unsplash.com/photo-1520975954732-35dd22299614?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (29, 13, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800', 2, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (30, 14, 'https://images.unsplash.com/photo-1589310243389-96a5483213a8?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (31, 14, 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (32, 15, 'https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (33, 15, 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (34, 16, 'https://images.unsplash.com/photo-1503341504253-dff4815485f1?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (35, 16, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (36, 17, 'https://images.unsplash.com/photo-1548624313-0396c75e4b1a?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (37, 17, 'https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (38, 17, 'https://images.unsplash.com/photo-1544923246-77307dd654cb?w=800', 2, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (39, 18, 'https://images.unsplash.com/photo-1544923246-77307dd654cb?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:48:52', 0);
INSERT INTO `item_image` VALUES (40, 18, 'https://images.unsplash.com/photo-1544923246-77307dd654cb?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:48:54', 0);
INSERT INTO `item_image` VALUES (41, 19, 'https://images.unsplash.com/photo-1544923246-77307dd654cb?w=800', 0, '2026-03-24 12:41:12', '2026-03-24 12:48:55', 0);
INSERT INTO `item_image` VALUES (42, 19, 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=800', 1, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (43, 19, 'https://images.unsplash.com/photo-1485462537746-965f33f7f6a7?w=800', 2, '2026-03-24 12:41:12', '2026-03-24 12:41:12', 0);
INSERT INTO `item_image` VALUES (44, 20, 'https://img.alicdn.com/i3/2298236993/O1CN0142Qzd121WqQOU1i8C_!!2298236993.jpg', 0, '2026-03-24 12:41:12', '2026-03-24 12:50:59', 0);
INSERT INTO `item_image` VALUES (45, 20, 'https://img.alicdn.com/i3/2298236993/O1CN0142Qzd121WqQOU1i8C_!!2298236993.jpg', 1, '2026-03-24 12:41:12', '2026-03-24 12:50:58', 0);
INSERT INTO `item_image` VALUES (46, 21, 'http://192.168.30.129:9000/clothes-recycle/item/25cc2182-2920-4cb8-a31e-2a1381349bb1.png', 0, '2026-03-24 12:43:55', '2026-03-24 12:43:55', 0);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息标题',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `msg_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息类型',
  `read_status` tinyint NOT NULL DEFAULT 0 COMMENT '已读状态：0未读，1已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_message_user_read`(`user_id` ASC, `read_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '站内消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (42, 1, '物品审核结果', '审核已通过', 'AUDIT', 1, '2026-03-24 12:44:25', '2026-03-24 12:44:25', 0);
INSERT INTO `message` VALUES (43, 1, '物品审核结果', '审核已通过', 'AUDIT', 1, '2026-03-24 12:44:26', '2026-03-24 12:44:26', 0);
INSERT INTO `message` VALUES (44, 1, '物品审核结果', '审核已通过', 'AUDIT', 1, '2026-03-24 12:44:28', '2026-03-24 12:44:28', 0);
INSERT INTO `message` VALUES (45, 1, '新的认领申请', '你的物品有新的认领申请', 'ORDER', 0, '2026-04-07 16:32:09', '2026-04-07 16:32:09', 0);
INSERT INTO `message` VALUES (46, 2, '订单已超时取消', '订单超时未处理，系统已自动取消', 'ORDER', 0, '2026-04-25 16:45:01', '2026-04-25 16:45:01', 0);
INSERT INTO `message` VALUES (47, 1, '订单已超时取消', '订单超时未处理，系统已自动取消', 'ORDER', 0, '2026-04-25 16:45:01', '2026-04-25 16:45:01', 0);

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_id` bigint NOT NULL COMMENT '物品ID',
  `buyer_id` bigint NOT NULL COMMENT '领取方用户ID',
  `seller_id` bigint NOT NULL COMMENT '发布方用户ID',
  `campus_id` bigint NOT NULL COMMENT '所属校区ID',
  `acquire_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '获取方式：FREE/POINT',
  `point_amount` int NOT NULL DEFAULT 0 COMMENT '本单积分数量',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单状态',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '订单备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_item_id`(`item_id` ASC) USING BTREE,
  INDEX `idx_order_buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_order_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_order_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '认领订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (16, 21, 2, 1, 1, 'POINT', 18, 'CANCELLED', '我想认领这件衣物', '2026-04-07 16:32:09', '2026-04-07 16:32:09', 0);

-- ----------------------------
-- Table structure for point_record
-- ----------------------------
DROP TABLE IF EXISTS `point_record`;
CREATE TABLE `point_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `change_amount` int NOT NULL COMMENT '积分变动值，正数增加，负数减少',
  `biz_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `biz_id` bigint NULL DEFAULT NULL COMMENT '业务ID',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_point_record_user_time`(`user_id` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of point_record
-- ----------------------------
INSERT INTO `point_record` VALUES (1, 1, 50, 'REGISTER', 1, '注册奖励', '2026-03-23 10:03:35', '2026-03-23 10:03:35', 0);
INSERT INTO `point_record` VALUES (2, 1, 2, 'SIGN', 82, '每日签到奖励', '2026-03-23 10:28:55', '2026-03-23 10:28:55', 0);
INSERT INTO `point_record` VALUES (3, 1, 30, 'ITEM_AUDIT_PASS', 3, '物品审核通过奖励', '2026-03-23 14:19:38', '2026-03-23 14:19:38', 0);
INSERT INTO `point_record` VALUES (4, 1, 30, 'ITEM_AUDIT_PASS', 2, '物品审核通过奖励', '2026-03-23 14:20:48', '2026-03-23 14:20:48', 0);
INSERT INTO `point_record` VALUES (5, 2, 50, 'REGISTER', 2, '注册奖励', '2026-03-23 14:35:15', '2026-03-23 14:35:15', 0);
INSERT INTO `point_record` VALUES (6, 2, -10, 'ORDER_CREATE', 1, '积分兑换下单冻结(冻结)', '2026-03-23 14:35:24', '2026-03-23 14:35:24', 0);
INSERT INTO `point_record` VALUES (7, 2, 10, 'ORDER_CANCEL', 1, '订单取消解冻(解冻)', '2026-03-23 14:50:03', '2026-03-23 14:50:03', 0);
INSERT INTO `point_record` VALUES (8, 2, -10, 'ORDER_CREATE', 2, '积分兑换下单冻结(冻结)', '2026-03-23 14:50:15', '2026-03-23 14:50:15', 0);
INSERT INTO `point_record` VALUES (9, 2, 10, 'ORDER_CANCEL', 2, '订单取消解冻(解冻)', '2026-03-23 14:55:03', '2026-03-23 14:55:03', 0);
INSERT INTO `point_record` VALUES (10, 2, 30, 'ITEM_AUDIT_PASS', 4, '物品审核通过奖励', '2026-03-23 14:56:17', '2026-03-23 14:56:17', 0);
INSERT INTO `point_record` VALUES (11, 2, 30, 'ITEM_AUDIT_PASS', 5, '物品审核通过奖励', '2026-03-23 14:58:35', '2026-03-23 14:58:35', 0);
INSERT INTO `point_record` VALUES (12, 2, -10, 'ORDER_CREATE', 3, '积分兑换下单冻结(冻结)', '2026-03-23 14:59:31', '2026-03-23 14:59:31', 0);
INSERT INTO `point_record` VALUES (13, 2, 10, 'ORDER_CANCEL', 3, '订单取消解冻(解冻)', '2026-03-23 15:15:24', '2026-03-23 15:15:24', 0);
INSERT INTO `point_record` VALUES (14, 2, 30, 'ITEM_AUDIT_PASS', 6, '物品审核通过奖励', '2026-03-23 15:17:10', '2026-03-23 15:17:10', 0);
INSERT INTO `point_record` VALUES (15, 2, 30, 'ITEM_AUDIT_PASS', 7, '物品审核通过奖励', '2026-03-23 15:19:05', '2026-03-23 15:19:05', 0);
INSERT INTO `point_record` VALUES (16, 1, -10, 'ORDER_CREATE', 4, '积分兑换下单冻结(冻结)', '2026-03-23 15:43:08', '2026-03-23 15:43:08', 0);
INSERT INTO `point_record` VALUES (17, 1, 10, 'ORDER_CANCEL', 4, '订单取消解冻(解冻)', '2026-03-23 15:43:39', '2026-03-23 15:43:39', 0);
INSERT INTO `point_record` VALUES (18, 2, 30, 'ITEM_AUDIT_PASS', 8, '物品审核通过奖励', '2026-03-23 15:45:49', '2026-03-23 15:45:49', 0);
INSERT INTO `point_record` VALUES (19, 1, -10, 'ORDER_CREATE', 5, '积分兑换下单冻结(冻结)', '2026-03-23 15:46:04', '2026-03-23 15:46:04', 0);
INSERT INTO `point_record` VALUES (20, 1, -10, 'ORDER_DONE', 5, '订单完成积分结算(冻结扣减)', '2026-03-23 15:47:08', '2026-03-23 15:47:08', 0);
INSERT INTO `point_record` VALUES (21, 2, 10, 'ORDER_DONE', 5, '订单完成积分结算(对方获得)', '2026-03-23 15:47:08', '2026-03-23 15:47:08', 0);
INSERT INTO `point_record` VALUES (22, 2, 30, 'DONATE_DONE', 5, '捐赠完成奖励', '2026-03-23 15:47:08', '2026-03-23 15:47:08', 0);
INSERT INTO `point_record` VALUES (23, 1, 10, 'CLAIM_DONE', 5, '领取完成奖励', '2026-03-23 15:47:08', '2026-03-23 15:47:08', 0);
INSERT INTO `point_record` VALUES (24, 2, 30, 'ITEM_AUDIT_PASS', 9, '物品审核通过奖励', '2026-03-23 16:00:08', '2026-03-23 16:00:08', 0);
INSERT INTO `point_record` VALUES (25, 2, 30, 'DONATE_DONE', 7, '捐赠完成奖励', '2026-03-23 16:06:00', '2026-03-23 16:06:00', 0);
INSERT INTO `point_record` VALUES (26, 1, 10, 'CLAIM_DONE', 7, '领取完成奖励', '2026-03-23 16:06:00', '2026-03-23 16:06:00', 0);
INSERT INTO `point_record` VALUES (27, 2, 30, 'ITEM_AUDIT_PASS', 10, '物品审核通过奖励', '2026-03-23 16:08:42', '2026-03-23 16:08:42', 0);
INSERT INTO `point_record` VALUES (28, 1, -110, 'ORDER_CREATE', 8, '积分兑换下单冻结(冻结)', '2026-03-23 16:08:55', '2026-03-23 16:08:55', 0);
INSERT INTO `point_record` VALUES (29, 1, 110, 'ORDER_CANCEL', 8, '订单取消解冻(解冻)', '2026-03-23 16:09:20', '2026-03-23 16:09:20', 0);
INSERT INTO `point_record` VALUES (30, 1, -110, 'ORDER_CREATE', 9, '积分兑换下单冻结(冻结)', '2026-03-23 16:09:40', '2026-03-23 16:09:40', 0);
INSERT INTO `point_record` VALUES (31, 1, -110, 'ORDER_DONE', 9, '订单完成积分结算(冻结扣减)', '2026-03-23 16:09:51', '2026-03-23 16:09:51', 0);
INSERT INTO `point_record` VALUES (32, 2, 110, 'ORDER_DONE', 9, '订单完成积分结算(对方获得)', '2026-03-23 16:09:51', '2026-03-23 16:09:51', 0);
INSERT INTO `point_record` VALUES (33, 2, 30, 'DONATE_DONE', 9, '捐赠完成奖励', '2026-03-23 16:09:51', '2026-03-23 16:09:51', 0);
INSERT INTO `point_record` VALUES (34, 1, 10, 'CLAIM_DONE', 9, '领取完成奖励', '2026-03-23 16:09:51', '2026-03-23 16:09:51', 0);
INSERT INTO `point_record` VALUES (35, 2, 30, 'ITEM_AUDIT_PASS', 11, '物品审核通过奖励', '2026-03-23 16:11:03', '2026-03-23 16:11:03', 0);
INSERT INTO `point_record` VALUES (36, 1, -101, 'ORDER_CREATE', 13, '积分兑换下单冻结(冻结)', '2026-03-23 16:21:18', '2026-03-23 16:21:18', 0);
INSERT INTO `point_record` VALUES (37, 1, 30, 'ITEM_AUDIT_PASS', 12, '物品审核通过奖励', '2026-03-23 16:22:20', '2026-03-23 16:22:20', 0);
INSERT INTO `point_record` VALUES (38, 1, -101, 'ORDER_DONE', 13, '订单完成积分结算(冻结扣减)', '2026-03-23 16:22:57', '2026-03-23 16:22:57', 0);
INSERT INTO `point_record` VALUES (39, 2, 101, 'ORDER_DONE', 13, '订单完成积分结算(对方获得)', '2026-03-23 16:22:57', '2026-03-23 16:22:57', 0);
INSERT INTO `point_record` VALUES (40, 2, 30, 'DONATE_DONE', 13, '捐赠完成奖励', '2026-03-23 16:22:57', '2026-03-23 16:22:57', 0);
INSERT INTO `point_record` VALUES (41, 1, 10, 'CLAIM_DONE', 13, '领取完成奖励', '2026-03-23 16:22:57', '2026-03-23 16:22:57', 0);
INSERT INTO `point_record` VALUES (42, 1, 30, 'DONATE_DONE', 15, '捐赠完成奖励', '2026-03-23 16:23:34', '2026-03-23 16:23:34', 0);
INSERT INTO `point_record` VALUES (43, 2, 10, 'CLAIM_DONE', 15, '领取完成奖励', '2026-03-23 16:23:34', '2026-03-23 16:23:34', 0);
INSERT INTO `point_record` VALUES (44, 1, -120, 'GIFT_EXCHANGE', 1, '积分兑换礼品', '2026-03-24 11:47:27', '2026-03-24 11:47:27', 0);
INSERT INTO `point_record` VALUES (45, 1, -10, 'GIFT_EXCHANGE', 3, '积分兑换礼品', '2026-03-24 12:29:22', '2026-03-24 12:29:22', 0);
INSERT INTO `point_record` VALUES (46, 1, -260, 'GIFT_EXCHANGE', 2, '积分兑换礼品', '2026-03-24 12:29:24', '2026-03-24 12:29:24', 0);
INSERT INTO `point_record` VALUES (47, 1, 30, 'ITEM_AUDIT_PASS', 2, '物品审核通过奖励', '2026-03-24 12:44:25', '2026-03-24 12:44:25', 0);
INSERT INTO `point_record` VALUES (48, 1, 30, 'ITEM_AUDIT_PASS', 1, '物品审核通过奖励', '2026-03-24 12:44:26', '2026-03-24 12:44:26', 0);
INSERT INTO `point_record` VALUES (49, 1, 30, 'ITEM_AUDIT_PASS', 21, '物品审核通过奖励', '2026-03-24 12:44:28', '2026-03-24 12:44:28', 0);
INSERT INTO `point_record` VALUES (50, 1, 2, 'SIGN', 83, '每日签到奖励', '2026-03-24 12:57:35', '2026-03-24 12:57:35', 0);
INSERT INTO `point_record` VALUES (51, 2, -18, 'ORDER_CREATE', 16, '积分兑换下单冻结(冻结)', '2026-04-07 16:32:09', '2026-04-07 16:32:09', 0);
INSERT INTO `point_record` VALUES (52, 2, 18, 'ORDER_TIMEOUT', 16, '订单超时自动取消解冻(解冻)', '2026-04-25 16:45:01', '2026-04-25 16:45:01', 0);

-- ----------------------------
-- Table structure for sign_record
-- ----------------------------
DROP TABLE IF EXISTS `sign_record`;
CREATE TABLE `sign_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `sign_date` date NOT NULL COMMENT '签到日期',
  `reward_point` int NOT NULL DEFAULT 0 COMMENT '签到奖励积分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sign_user_date`(`user_id` ASC, `sign_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '签到记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sign_record
-- ----------------------------
INSERT INTO `sign_record` VALUES (1, 1, '2026-03-23', 2, '2026-03-23 10:28:55', '2026-03-23 10:28:55', 0);
INSERT INTO `sign_record` VALUES (2, 1, '2026-03-24', 2, '2026-03-24 12:57:35', '2026-03-24 12:57:35', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户手机号',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码',
  `student_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `campus_id` bigint NOT NULL COMMENT '所属校区ID',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像URL',
  `point_balance` int NOT NULL DEFAULT 0 COMMENT '可用积分余额',
  `frozen_point` int NOT NULL DEFAULT 0 COMMENT '冻结积分',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1正常，0禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_user_campus_id`(`campus_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '13203071697', '123456', '2024021575', 'hello', 1, 'http://192.168.30.129:9000/clothes-recycle/item/1115aa25-d8ad-4a9b-9e4c-4c15986575e7.jpg', 1871, 0, 1, '2026-03-23 10:03:35', '2026-03-23 16:21:08', 0);
INSERT INTO `user` VALUES (2, '13203071698', '123456', '2024021576', '测试', 2, '', 641, 0, 1, '2026-03-23 14:35:15', '2026-03-23 14:35:15', 0);

SET FOREIGN_KEY_CHECKS = 1;
