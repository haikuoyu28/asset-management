-- =============================================
-- Enterprise IT Asset Management Platform
-- Database Schema
-- =============================================

-- Step 1: Create database
CREATE DATABASE IF NOT EXISTS `ry_cloud` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ry_cloud`;

-- =============================================
-- Table 1: asset_info - Asset Information
-- =============================================
DROP TABLE IF EXISTS `asset_info`;
CREATE TABLE `asset_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Asset ID',
  `asset_code` varchar(64) NOT NULL COMMENT 'Asset Code',
  `asset_name` varchar(128) NOT NULL COMMENT 'Asset Name',
  `model` varchar(128) DEFAULT NULL COMMENT 'Model',
  `specification` varchar(256) DEFAULT NULL COMMENT 'Specification',
  `configuration` varchar(512) DEFAULT NULL COMMENT 'Configuration',
  `serial_number` varchar(128) DEFAULT NULL COMMENT 'Serial Number',
  `asset_type` char(1) NOT NULL COMMENT 'Asset Type (1=Server, 2=PC, 3=Switch, 4=Router, 5=Other)',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT 'Status (0=In Use, 1=Idle, 2=Repair, 3=Scrap)',
  `dept_id` bigint DEFAULT NULL COMMENT 'Department ID',
  `dept_name` varchar(64) DEFAULT NULL COMMENT 'Department Name',
  `responsible_person` varchar(64) DEFAULT NULL COMMENT 'Responsible Person',
  `purchase_date` date DEFAULT NULL COMMENT 'Purchase Date',
  `price` decimal(12,2) DEFAULT NULL COMMENT 'Price',
  `image_url` varchar(256) DEFAULT NULL COMMENT 'Image URL',
  `location` varchar(128) DEFAULT NULL COMMENT 'Location',
  `ip_address` varchar(64) DEFAULT NULL COMMENT 'IP Address',
  `mac_address` varchar(64) DEFAULT NULL COMMENT 'MAC Address',
  `remark` varchar(512) DEFAULT NULL COMMENT 'Remark',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT 'Delete Flag (0=Exists, 1=Deleted)',
  `create_by` varchar(64) DEFAULT '' COMMENT 'Creator',
  `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) DEFAULT '' COMMENT 'Updater',
  `update_time` datetime DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_asset_code` (`asset_code`),
  KEY `idx_asset_type` (`asset_type`),
  KEY `idx_status` (`status`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Asset Information';

-- =============================================
-- Table 2: asset_flow - Asset Flow
-- =============================================
DROP TABLE IF EXISTS `asset_flow`;
CREATE TABLE `asset_flow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Flow ID',
  `asset_id` bigint NOT NULL COMMENT 'Asset ID',
  `asset_name` varchar(128) DEFAULT NULL COMMENT 'Asset Name',
  `asset_code` varchar(64) DEFAULT NULL COMMENT 'Asset Code',
  `flow_type` char(1) NOT NULL COMMENT 'Flow Type (1=Apply, 2=Return, 3=Repair, 4=Maint, 5=Inspect, 6=Scrap)',
  `operator` varchar(64) NOT NULL COMMENT 'Operator',
  `operate_time` datetime NOT NULL COMMENT 'Operate Time',
  `before_person` varchar(64) DEFAULT NULL COMMENT 'Before Person',
  `after_person` varchar(64) DEFAULT NULL COMMENT 'After Person',
  `before_status` char(1) DEFAULT NULL COMMENT 'Before Status',
  `after_status` char(1) DEFAULT NULL COMMENT 'After Status',
  `description` varchar(512) DEFAULT NULL COMMENT 'Description',
  `attachment` varchar(256) DEFAULT NULL COMMENT 'Attachment',
  `remark` varchar(512) DEFAULT NULL COMMENT 'Remark',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT 'Delete Flag (0=Exists, 1=Deleted)',
  `create_by` varchar(64) DEFAULT '' COMMENT 'Creator',
  `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) DEFAULT '' COMMENT 'Updater',
  `update_time` datetime DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`id`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_flow_type` (`flow_type`),
  KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Asset Flow';

-- =============================================
-- Table 3: monitor_server - Monitor Server
-- =============================================
DROP TABLE IF EXISTS `monitor_server`;
CREATE TABLE `monitor_server` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Server ID',
  `server_ip` varchar(64) NOT NULL COMMENT 'Server IP',
  `hostname` varchar(128) NOT NULL COMMENT 'Hostname',
  `os_name` varchar(128) DEFAULT NULL COMMENT 'OS Name',
  `cpu_cores` int DEFAULT NULL COMMENT 'CPU Cores',
  `memory_gb` int DEFAULT NULL COMMENT 'Memory GB',
  `disk_gb` int DEFAULT NULL COMMENT 'Disk GB',
  `monitor_status` char(1) NOT NULL DEFAULT '0' COMMENT 'Monitor Status (0=Normal, 1=Abnormal, 2=Unknown)',
  `last_collect_time` datetime DEFAULT NULL COMMENT 'Last Collect Time',
  `connection_status` char(1) NOT NULL DEFAULT '0' COMMENT 'Connection Status (0=Online, 1=Offline)',
  `ssh_port` int DEFAULT 22 COMMENT 'SSH Port',
  `ssh_username` varchar(64) DEFAULT NULL COMMENT 'SSH Username',
  `ssh_password` varchar(256) DEFAULT NULL COMMENT 'SSH Password',
  `agent_path` varchar(256) DEFAULT NULL COMMENT 'Agent Path',
  `remark` varchar(512) DEFAULT NULL COMMENT 'Remark',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT 'Delete Flag (0=Exists, 1=Deleted)',
  `create_by` varchar(64) DEFAULT '' COMMENT 'Creator',
  `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) DEFAULT '' COMMENT 'Updater',
  `update_time` datetime DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_server_ip` (`server_ip`),
  KEY `idx_monitor_status` (`monitor_status`),
  KEY `idx_connection_status` (`connection_status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Monitor Server';

-- =============================================
-- Table 4: monitor_data - Monitor Data
-- =============================================
DROP TABLE IF EXISTS `monitor_data`;
CREATE TABLE `monitor_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data ID',
  `server_id` bigint NOT NULL COMMENT 'Server ID',
  `server_ip` varchar(64) DEFAULT NULL COMMENT 'Server IP',
  `cpu_usage` decimal(5,2) DEFAULT NULL COMMENT 'CPU Usage (%)',
  `memory_usage` decimal(5,2) DEFAULT NULL COMMENT 'Memory Usage (%)',
  `memory_used_gb` decimal(8,2) DEFAULT NULL COMMENT 'Used Memory GB',
  `memory_total_gb` decimal(8,2) DEFAULT NULL COMMENT 'Total Memory GB',
  `disk_usage` decimal(5,2) DEFAULT NULL COMMENT 'Disk Usage (%)',
  `disk_used_gb` decimal(8,2) DEFAULT NULL COMMENT 'Used Disk GB',
  `disk_total_gb` decimal(8,2) DEFAULT NULL COMMENT 'Total Disk GB',
  `network_in` decimal(12,2) DEFAULT NULL COMMENT 'Network In KB/s',
  `network_out` decimal(12,2) DEFAULT NULL COMMENT 'Network Out KB/s',
  `load_average` varchar(64) DEFAULT NULL COMMENT 'Load Average',
  `running_processes` int DEFAULT NULL COMMENT 'Running Processes',
  `collect_time` datetime NOT NULL COMMENT 'Collect Time',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  PRIMARY KEY (`id`),
  KEY `idx_server_id` (`server_id`),
  KEY `idx_collect_time` (`collect_time`),
  KEY `idx_server_collect` (`server_id`, `collect_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Monitor Data';

-- =============================================
-- Table 5: monitor_alarm - Monitor Alarm
-- =============================================
DROP TABLE IF EXISTS `monitor_alarm`;
CREATE TABLE `monitor_alarm` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Alarm ID',
  `server_id` bigint NOT NULL COMMENT 'Server ID',
  `server_ip` varchar(64) DEFAULT NULL COMMENT 'Server IP',
  `hostname` varchar(128) DEFAULT NULL COMMENT 'Hostname',
  `alarm_type` char(1) NOT NULL COMMENT 'Alarm Type (1=CPU, 2=Memory, 3=Disk, 4=Network, 5=Service)',
  `alarm_level` char(1) NOT NULL DEFAULT '1' COMMENT 'Alarm Level (1=Info, 2=Warning, 3=Critical)',
  `threshold_value` decimal(8,2) NOT NULL COMMENT 'Threshold Value',
  `alarm_value` decimal(8,2) NOT NULL COMMENT 'Alarm Value',
  `alarm_status` char(1) NOT NULL DEFAULT '0' COMMENT 'Alarm Status (0=Unprocessed, 1=Processing, 2=Processed, 3=Ignored)',
  `alarm_message` varchar(512) DEFAULT NULL COMMENT 'Alarm Message',
  `alarm_time` datetime NOT NULL COMMENT 'Alarm Time',
  `handle_time` datetime DEFAULT NULL COMMENT 'Handle Time',
  `handle_person` varchar(64) DEFAULT NULL COMMENT 'Handle Person',
  `handle_result` varchar(512) DEFAULT NULL COMMENT 'Handle Result',
  `remark` varchar(512) DEFAULT NULL COMMENT 'Remark',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT 'Delete Flag (0=Exists, 1=Deleted)',
  `create_by` varchar(64) DEFAULT '' COMMENT 'Creator',
  `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) DEFAULT '' COMMENT 'Updater',
  `update_time` datetime DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`id`),
  KEY `idx_server_id` (`server_id`),
  KEY `idx_alarm_type` (`alarm_type`),
  KEY `idx_alarm_status` (`alarm_status`),
  KEY `idx_alarm_time` (`alarm_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Monitor Alarm';

-- =============================================
-- Dictionary Data: asset_type
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('Asset Type', 'asset_type', '0', 'admin', NOW(), 'Asset Type');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, 'Server', '1', 'asset_type', NULL, 'primary', 'N', '0', 'admin', NOW()),
(2, 'PC', '2', 'asset_type', NULL, 'success', 'N', '0', 'admin', NOW()),
(3, 'Switch', '3', 'asset_type', NULL, 'warning', 'N', '0', 'admin', NOW()),
(4, 'Router', '4', 'asset_type', NULL, 'info', 'N', '0', 'admin', NOW()),
(5, 'Other', '5', 'asset_type', NULL, 'default', 'N', '0', 'admin', NOW());

-- =============================================
-- Dictionary Data: asset_status
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('Asset Status', 'asset_status', '0', 'admin', NOW(), 'Asset Status');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, 'In Use', '0', 'asset_status', NULL, 'success', 'Y', '0', 'admin', NOW()),
(2, 'Idle', '1', 'asset_status', NULL, 'warning', 'N', '0', 'admin', NOW()),
(3, 'Repair', '2', 'asset_status', NULL, 'danger', 'N', '0', 'admin', NOW()),
(4, 'Scrap', '3', 'asset_status', NULL, 'info', 'N', '0', 'admin', NOW());

-- =============================================
-- Dictionary Data: asset_flow_type
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('Asset Flow Type', 'asset_flow_type', '0', 'admin', NOW(), 'Asset Flow Type');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, 'Apply', '1', 'asset_flow_type', NULL, 'primary', 'N', '0', 'admin', NOW()),
(2, 'Return', '2', 'asset_flow_type', NULL, 'success', 'N', '0', 'admin', NOW()),
(3, 'Repair Request', '3', 'asset_flow_type', NULL, 'warning', 'N', '0', 'admin', NOW()),
(4, 'Maintenance', '4', 'asset_flow_type', NULL, 'danger', 'N', '0', 'admin', NOW()),
(5, 'Inspection', '5', 'asset_flow_type', NULL, 'info', 'N', '0', 'admin', NOW()),
(6, 'Scrap', '6', 'asset_flow_type', NULL, 'default', 'N', '0', 'admin', NOW());

-- =============================================
-- Dictionary Data: monitor_status
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('Monitor Status', 'monitor_status', '0', 'admin', NOW(), 'Monitor Status');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, 'Normal', '0', 'monitor_status', NULL, 'success', 'Y', '0', 'admin', NOW()),
(2, 'Abnormal', '1', 'monitor_status', NULL, 'danger', 'N', '0', 'admin', NOW()),
(3, 'Unknown', '2', 'monitor_status', NULL, 'warning', 'N', '0', 'admin', NOW());

-- =============================================
-- Dictionary Data: connection_status
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('Connection Status', 'connection_status', '0', 'admin', NOW(), 'Connection Status');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, 'Online', '0', 'connection_status', NULL, 'success', 'Y', '0', 'admin', NOW()),
(2, 'Offline', '1', 'connection_status', NULL, 'danger', 'N', '0', 'admin', NOW());

-- =============================================
-- Dictionary Data: alarm_type
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('Alarm Type', 'alarm_type', '0', 'admin', NOW(), 'Alarm Type');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, 'CPU', '1', 'alarm_type', NULL, 'danger', 'N', '0', 'admin', NOW()),
(2, 'Memory', '2', 'alarm_type', NULL, 'warning', 'N', '0', 'admin', NOW()),
(3, 'Disk', '3', 'alarm_type', NULL, 'info', 'N', '0', 'admin', NOW()),
(4, 'Network', '4', 'alarm_type', NULL, 'primary', 'N', '0', 'admin', NOW()),
(5, 'Service', '5', 'alarm_type', NULL, 'success', 'N', '0', 'admin', NOW());

-- =============================================
-- Dictionary Data: alarm_level
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('Alarm Level', 'alarm_level', '0', 'admin', NOW(), 'Alarm Level');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, 'Info', '1', 'alarm_level', NULL, 'info', 'Y', '0', 'admin', NOW()),
(2, 'Warning', '2', 'alarm_level', NULL, 'warning', 'N', '0', 'admin', NOW()),
(3, 'Critical', '3', 'alarm_level', NULL, 'danger', 'N', '0', 'admin', NOW());

-- =============================================
-- Dictionary Data: alarm_status
-- =============================================
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('Alarm Status', 'alarm_status', '0', 'admin', NOW(), 'Alarm Status');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
(1, 'Unprocessed', '0', 'alarm_status', NULL, 'danger', 'Y', '0', 'admin', NOW()),
(2, 'Processing', '1', 'alarm_status', NULL, 'warning', 'N', '0', 'admin', NOW()),
(3, 'Processed', '2', 'alarm_status', NULL, 'success', 'N', '0', 'admin', NOW()),
(4, 'Ignored', '3', 'alarm_status', NULL, 'info', 'N', '0', 'admin', NOW());

SELECT 'Database tables and dictionary data created successfully!' AS Result;
