/*
 Navicat Premium Data Transfer

 Source Server         : 10.168.136.128
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : 10.168.136.128:3306
 Source Schema         : dcom

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 13/02/2023 17:03:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rc_su
-- ----------------------------
DROP TABLE IF EXISTS `rc_su`;
CREATE TABLE `rc_su`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `su_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '监控主机名称',
  `su_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '监控主机编码',
  `su_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '监控主机IP',
  `su_port` int(8) NULL DEFAULT NULL COMMENT '监控主机端口号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rc_su
-- ----------------------------
INSERT INTO `rc_su` VALUES (1, 'K25', '191103', '192.168.1.168', 9090);
INSERT INTO `rc_su` VALUES (2, 'K35', '191104', '192.168.1.169', 9091);
INSERT INTO `rc_su` VALUES (3, 'K36', '191105', '192.168.1.170', 9092);
INSERT INTO `rc_su` VALUES (4, 'K15', '191106', '192.168.1.171', 9093);
INSERT INTO `rc_su` VALUES (5, 'K26', '191107', '192.168.1.172', 9094);
INSERT INTO `rc_su` VALUES (6, 'K45', '191108', '192.168.1.173', 9095);
INSERT INTO `rc_su` VALUES (7, 'K46', '191109', '192.168.1.174', 9096);
INSERT INTO `rc_su` VALUES (8, 'K47', '191110', '192.168.1.175', 9097);

SET FOREIGN_KEY_CHECKS = 1;
