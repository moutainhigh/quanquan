/*
Navicat MySQL Data Transfer

Source Server         : 测试环境
Source Server Version : 50173
Source Host           : 172.16.1.53:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2016-03-15 14:27:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user_count_data`
-- ----------------------------
DROP TABLE IF EXISTS `user_count_data`;
CREATE TABLE `user_count_data` (
  `userId` varchar(255) NOT NULL,
  `ques_num` int(11) DEFAULT NULL,
  `ans_num` int(11) DEFAULT NULL,
  `acheck` double DEFAULT NULL,
  `qcheck` double DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_count_data
-- ----------------------------
