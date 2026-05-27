SET NAMES utf8mb4;

-- =============================================
-- 企业IT资产与服务器资产管理平台数据库脚本
-- 适用于 RuoYi-Vue 框架
-- 数据库名: ry-cloud (可根据实际情况修改)
-- =============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `ry_cloud` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ry_cloud`;

-- =============================================
-- 表1: asset_info - 设备资产表
-- =============================================
DROP TABLE IF EXISTS `asset_info`;
CREATE TABLE `asset_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资产ID',
  `asset_code` varchar(64) NOT NULL COMMENT '设备编号',
  `asset_name` varchar(128) NOT NULL COMMENT '设备名称',
  `model` varchar(128) DEFAULT NULL COMMENT '型号',
  `specification` varchar(256) DEFAULT NULL COMMENT '规格',
  `configuration` varchar(512) DEFAULT NULL COMMENT '配置',
  `serial_number` varchar(128) DEFAULT NULL COMMENT '序列号',
  `asset_type` char(1) NOT NULL COMMENT '资产类型（1服务器 2PC 3交换机 4路由器 5其他）',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0在用 1闲置 2维修 3报废）',
  `dept_id` bigint DEFAULT NULL COMMENT '归属部门ID',
  `dept_name` varchar(64) DEFAULT NULL COMMENT '归属部门名称',
  `responsible_person` varchar(64) DEFAULT NULL COMMENT '责任人',
  `purchase_date` date DEFAULT NULL COMMENT '采购日期',
  `price` decimal(12,2) DEFAULT NULL COMMENT '价格',
  `image_url` varchar(256) DEFAULT NULL COMMENT '设备图片',
  `location` varchar(128) DEFAULT NULL COMMENT '存放位置',
  `ip_address` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `mac_address` varchar(64) DEFAULT NULL COMMENT 'MAC地址',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_asset_code` (`asset_code`),
  KEY `idx_asset_type` (`asset_type`),
  KEY `idx_status` (`status`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='设备资产表';

-- =============================================
-- 表2: asset_flow - 资产流程表
-- =============================================
DROP TABLE IF EXISTS `asset_flow`;
CREATE TABLE `asset_flow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流程ID',
  `asset_id` bigint NOT NULL COMMENT '资产ID',
  `asset_name` varchar(128) DEFAULT NULL COMMENT '资产名称',
  `asset_code` varchar(64) DEFAULT NULL COMMENT '资产编号',
  `flow_type` char(1) NOT NULL COMMENT '流程类型（1领用 2归还 3报修 4维修 5巡检 6报废）',
  `operator` varchar(64) NOT NULL COMMENT '操作人',
  `operate_time` datetime NOT NULL COMMENT '操作时间',
  `before_person` varchar(64) DEFAULT NULL COMMENT '变更前责任人',
  `after_person` varchar(64) DEFAULT NULL COMMENT '变更后责任人',
  `before_status` char(1) DEFAULT NULL COMMENT '变更前状态',
  `after_status` char(1) DEFAULT NULL COMMENT '变更后状态',
  `description` varchar(512) DEFAULT NULL COMMENT '描述',
  `attachment` varchar(256) DEFAULT NULL COMMENT '附件',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_flow_type` (`flow_type`),
  KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='资产流程表';

-- =============================================
-- 表3: monitor_server - 监控服务器表
-- =============================================
DROP TABLE IF EXISTS `monitor_server`;
CREATE TABLE `monitor_server` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务器ID',
  `asset_id` bigint DEFAULT NULL COMMENT '关联资产ID',
  `server_ip` varchar(64) NOT NULL COMMENT '服务器IP',
  `hostname` varchar(128) NOT NULL COMMENT '主机名',
  `os_name` varchar(128) DEFAULT NULL COMMENT '操作系统',
  `cpu_cores` int DEFAULT NULL COMMENT 'CPU核心数',
  `memory_gb` int DEFAULT NULL COMMENT '内存GB',
  `disk_gb` int DEFAULT NULL COMMENT '磁盘GB',
  `monitor_status` char(1) NOT NULL DEFAULT '0' COMMENT '监控状态（0正常 1异常 2未知）',
  `last_collect_time` datetime DEFAULT NULL COMMENT '上次采集时间',
  `connection_status` char(1) NOT NULL DEFAULT '0' COMMENT '连接状态（0在线 1离线）',
  `ssh_port` int DEFAULT 22 COMMENT 'SSH端口',
  `ssh_username` varchar(64) DEFAULT NULL COMMENT 'SSH用户名',
  `ssh_password` varchar(256) DEFAULT NULL COMMENT 'SSH密码（保留兼容，当前版本不保存）',
  `agent_path` varchar(256) DEFAULT NULL COMMENT 'Agent路径',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_server_ip` (`server_ip`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_monitor_status` (`monitor_status`),
  KEY `idx_connection_status` (`connection_status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='监控服务器表';

-- =============================================
-- 表4: monitor_data - 监控数据表
-- =============================================
DROP TABLE IF EXISTS `monitor_data`;
CREATE TABLE `monitor_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `server_id` bigint NOT NULL COMMENT '服务器ID',
  `server_ip` varchar(64) DEFAULT NULL COMMENT '服务器IP',
  `cpu_usage` decimal(5,2) DEFAULT NULL COMMENT 'CPU使用率(%)',
  `memory_usage` decimal(5,2) DEFAULT NULL COMMENT '内存使用率(%)',
  `memory_used_gb` decimal(8,2) DEFAULT NULL COMMENT '已用内存GB',
  `memory_total_gb` decimal(8,2) DEFAULT NULL COMMENT '总内存GB',
  `disk_usage` decimal(5,2) DEFAULT NULL COMMENT '磁盘使用率(%)',
  `disk_used_gb` decimal(8,2) DEFAULT NULL COMMENT '已用磁盘GB',
  `disk_total_gb` decimal(8,2) DEFAULT NULL COMMENT '总磁盘GB',
  `network_in` decimal(12,2) DEFAULT NULL COMMENT '网络入流量KB/s',
  `network_out` decimal(12,2) DEFAULT NULL COMMENT '网络出流量KB/s',
  `load_average` varchar(64) DEFAULT NULL COMMENT '系统负载',
  `running_processes` int DEFAULT NULL COMMENT '运行进程数',
  `collect_time` datetime NOT NULL COMMENT '采集时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_server_id` (`server_id`),
  KEY `idx_collect_time` (`collect_time`),
  KEY `idx_server_collect` (`server_id`, `collect_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='监控数据表';

-- =============================================
-- 表5: monitor_alarm - 监控告警表
-- =============================================
DROP TABLE IF EXISTS `monitor_alarm`;
CREATE TABLE `monitor_alarm` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '告警ID',
  `server_id` bigint NOT NULL COMMENT '服务器ID',
  `server_ip` varchar(64) DEFAULT NULL COMMENT '服务器IP',
  `hostname` varchar(128) DEFAULT NULL COMMENT '主机名',
  `alarm_type` char(1) NOT NULL COMMENT '告警类型（1CPU 2内存 3磁盘 4网络 5服务）',
  `alarm_level` char(1) NOT NULL DEFAULT '1' COMMENT '告警级别（1提示 2警告 3严重）',
  `threshold_value` decimal(8,2) NOT NULL COMMENT '告警阈值',
  `alarm_value` decimal(8,2) NOT NULL COMMENT '告警值',
  `alarm_status` char(1) NOT NULL DEFAULT '0' COMMENT '告警状态（0未处理 1处理中 2已处理 3已忽略）',
  `alarm_message` varchar(512) DEFAULT NULL COMMENT '告警消息',
  `alarm_time` datetime NOT NULL COMMENT '告警时间',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_person` varchar(64) DEFAULT NULL COMMENT '处理人',
  `handle_result` varchar(512) DEFAULT NULL COMMENT '处理结果',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_server_id` (`server_id`),
  KEY `idx_alarm_type` (`alarm_type`),
  KEY `idx_alarm_status` (`alarm_status`),
  KEY `idx_alarm_time` (`alarm_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='监控告警表';

-- =============================================
-- 初始化数据：资产类型字典
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('资产类型', 'asset_type', '0', 'admin', NOW(), '设备资产类型');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, '服务器', '1', 'asset_type', NULL, 'primary', 'N', '0', 'admin', NOW()),
(2, 'PC', '2', 'asset_type', NULL, 'success', 'N', '0', 'admin', NOW()),
(3, '交换机', '3', 'asset_type', NULL, 'warning', 'N', '0', 'admin', NOW()),
(4, '路由器', '4', 'asset_type', NULL, 'info', 'N', '0', 'admin', NOW()),
(5, '其他', '5', 'asset_type', NULL, 'default', 'N', '0', 'admin', NOW());

-- =============================================
-- 初始化数据：资产状态字典
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('资产状态', 'asset_status', '0', 'admin', NOW(), '设备资产状态');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, '在用', '0', 'asset_status', NULL, 'success', 'Y', '0', 'admin', NOW()),
(2, '闲置', '1', 'asset_status', NULL, 'warning', 'N', '0', 'admin', NOW()),
(3, '维修', '2', 'asset_status', NULL, 'danger', 'N', '0', 'admin', NOW()),
(4, '报废', '3', 'asset_status', NULL, 'info', 'N', '0', 'admin', NOW());

-- =============================================
-- 初始化数据：资产流程类型字典
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('资产流程类型', 'asset_flow_type', '0', 'admin', NOW(), '资产流程类型');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, '领用', '1', 'asset_flow_type', NULL, 'primary', 'N', '0', 'admin', NOW()),
(2, '归还', '2', 'asset_flow_type', NULL, 'success', 'N', '0', 'admin', NOW()),
(3, '报修', '3', 'asset_flow_type', NULL, 'warning', 'N', '0', 'admin', NOW()),
(4, '维修', '4', 'asset_flow_type', NULL, 'danger', 'N', '0', 'admin', NOW()),
(5, '巡检', '5', 'asset_flow_type', NULL, 'info', 'N', '0', 'admin', NOW()),
(6, '报废', '6', 'asset_flow_type', NULL, 'default', 'N', '0', 'admin', NOW());

-- =============================================
-- 初始化数据：监控状态字典
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('监控状态', 'monitor_status', '0', 'admin', NOW(), '服务器监控状态');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, '正常', '0', 'monitor_status', NULL, 'success', 'Y', '0', 'admin', NOW()),
(2, '异常', '1', 'monitor_status', NULL, 'danger', 'N', '0', 'admin', NOW()),
(3, '未知', '2', 'monitor_status', NULL, 'warning', 'N', '0', 'admin', NOW());

-- =============================================
-- 初始化数据：连接状态字典
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('连接状态', 'connection_status', '0', 'admin', NOW(), '服务器连接状态');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, '在线', '0', 'connection_status', NULL, 'success', 'Y', '0', 'admin', NOW()),
(2, '离线', '1', 'connection_status', NULL, 'danger', 'N', '0', 'admin', NOW());

-- =============================================
-- 初始化数据：告警类型字典
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('告警类型', 'alarm_type', '0', 'admin', NOW(), '监控告警类型');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, 'CPU', '1', 'alarm_type', NULL, 'danger', 'N', '0', 'admin', NOW()),
(2, '内存', '2', 'alarm_type', NULL, 'warning', 'N', '0', 'admin', NOW()),
(3, '磁盘', '3', 'alarm_type', NULL, 'info', 'N', '0', 'admin', NOW()),
(4, '网络', '4', 'alarm_type', NULL, 'primary', 'N', '0', 'admin', NOW()),
(5, '服务', '5', 'alarm_type', NULL, 'success', 'N', '0', 'admin', NOW());

-- =============================================
-- 初始化数据：告警级别字典
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('告警级别', 'alarm_level', '0', 'admin', NOW(), '监控告警级别');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, '提示', '1', 'alarm_level', NULL, 'info', 'Y', '0', 'admin', NOW()),
(2, '警告', '2', 'alarm_level', NULL, 'warning', 'N', '0', 'admin', NOW()),
(3, '严重', '3', 'alarm_level', NULL, 'danger', 'N', '0', 'admin', NOW());

-- =============================================
-- 初始化数据：告警状态字典
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('告警状态', 'alarm_status', '0', 'admin', NOW(), '监控告警状态');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, '未处理', '0', 'alarm_status', NULL, 'danger', 'Y', '0', 'admin', NOW()),
(2, '处理中', '1', 'alarm_status', NULL, 'warning', 'N', '0', 'admin', NOW()),
(3, '已处理', '2', 'alarm_status', NULL, 'success', 'N', '0', 'admin', NOW()),
(4, '已忽略', '3', 'alarm_status', NULL, 'info', 'N', '0', 'admin', NOW());

-- =============================================
-- 创建索引以提高查询性能
-- =============================================
CREATE INDEX idx_asset_create_time ON asset_info(create_time);
CREATE INDEX idx_asset_dept_status ON asset_info(dept_id, status);
CREATE INDEX idx_monitor_data_server_time ON monitor_data(server_id, collect_time);
CREATE INDEX idx_alarm_server_status ON monitor_alarm(server_id, alarm_status);

-- =============================================
-- Navicat操作说明：
-- 1. 打开Navicat，连接到MySQL数据库
-- 2. 选择或创建目标数据库（如 ry_cloud）
-- 3. 右键点击数据库 -> "运行SQL文件"
-- 4. 选择本SQL文件，点击开始
-- 5. 执行完成后，刷新数据库查看表结构
-- =============================================
