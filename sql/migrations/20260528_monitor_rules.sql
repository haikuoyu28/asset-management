SET NAMES utf8mb4;
USE `ry_cloud`;

CREATE TABLE IF NOT EXISTS `monitor_alarm_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name` varchar(128) NOT NULL COMMENT '规则名称',
  `alarm_type` char(1) NOT NULL COMMENT '告警类型（1CPU 2内存 3磁盘）',
  `alarm_level` char(1) NOT NULL DEFAULT '2' COMMENT '告警级别（1提示 2警告 3严重）',
  `compare_operator` varchar(8) NOT NULL DEFAULT '>=' COMMENT '比较符',
  `threshold_value` decimal(8,2) NOT NULL COMMENT '阈值',
  `scope_type` char(1) NOT NULL DEFAULT '0' COMMENT '作用范围（0全部服务器 1指定服务器）',
  `server_id` bigint DEFAULT NULL COMMENT '指定服务器ID',
  `silent_minutes` int NOT NULL DEFAULT 30 COMMENT '静默分钟数',
  `enabled` char(1) NOT NULL DEFAULT '0' COMMENT '启用状态（0启用 1停用）',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_alarm_type` (`alarm_type`),
  KEY `idx_rule_enabled` (`enabled`),
  KEY `idx_rule_scope` (`scope_type`, `server_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='告警规则表';

INSERT INTO `monitor_alarm_rule` (`rule_name`, `alarm_type`, `alarm_level`, `compare_operator`, `threshold_value`, `scope_type`, `silent_minutes`, `enabled`, `create_by`, `create_time`, `remark`)
SELECT 'CPU 使用率过高', '1', '2', '>=', 85.00, '0', 30, '0', 'admin', NOW(), '默认 CPU 阈值规则'
WHERE NOT EXISTS (SELECT 1 FROM `monitor_alarm_rule` WHERE `rule_name` = 'CPU 使用率过高' AND `del_flag` = '0');

INSERT INTO `monitor_alarm_rule` (`rule_name`, `alarm_type`, `alarm_level`, `compare_operator`, `threshold_value`, `scope_type`, `silent_minutes`, `enabled`, `create_by`, `create_time`, `remark`)
SELECT '内存使用率过高', '2', '2', '>=', 90.00, '0', 30, '0', 'admin', NOW(), '默认内存阈值规则'
WHERE NOT EXISTS (SELECT 1 FROM `monitor_alarm_rule` WHERE `rule_name` = '内存使用率过高' AND `del_flag` = '0');

INSERT INTO `monitor_alarm_rule` (`rule_name`, `alarm_type`, `alarm_level`, `compare_operator`, `threshold_value`, `scope_type`, `silent_minutes`, `enabled`, `create_by`, `create_time`, `remark`)
SELECT '磁盘使用率过高', '3', '3', '>=', 90.00, '0', 60, '0', 'admin', NOW(), '默认磁盘阈值规则'
WHERE NOT EXISTS (SELECT 1 FROM `monitor_alarm_rule` WHERE `rule_name` = '磁盘使用率过高' AND `del_flag` = '0');

SET @monitor_parent_id = (SELECT `menu_id` FROM `sys_menu` WHERE `path` = 'ops-monitor' AND `parent_id` = 0 LIMIT 1);

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `route_name`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT '告警规则', @monitor_parent_id, '4', 'rule', 'OpsAlarmRule', 'monitor/rule/index', 'C', '0', '0', 'monitor:rule:list', 'education', 'admin', NOW(), '告警规则菜单'
WHERE @monitor_parent_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `parent_id` = @monitor_parent_id AND `path` = 'rule');

SET @rule_id = (SELECT `menu_id` FROM `sys_menu` WHERE `parent_id` = @monitor_parent_id AND `path` = 'rule' LIMIT 1);

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `route_name`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT '告警规则查询', @rule_id, '1', '', '', '', 'F', '0', '0', 'monitor:rule:query', '#', 'admin', NOW(), ''
WHERE @rule_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `parent_id` = @rule_id AND `perms` = 'monitor:rule:query');

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `route_name`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT '告警规则新增', @rule_id, '2', '', '', '', 'F', '0', '0', 'monitor:rule:add', '#', 'admin', NOW(), ''
WHERE @rule_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `parent_id` = @rule_id AND `perms` = 'monitor:rule:add');

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `route_name`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT '告警规则修改', @rule_id, '3', '', '', '', 'F', '0', '0', 'monitor:rule:edit', '#', 'admin', NOW(), ''
WHERE @rule_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `parent_id` = @rule_id AND `perms` = 'monitor:rule:edit');

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `route_name`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT '告警规则删除', @rule_id, '4', '', '', '', 'F', '0', '0', 'monitor:rule:remove', '#', 'admin', NOW(), ''
WHERE @rule_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `parent_id` = @rule_id AND `perms` = 'monitor:rule:remove');

SET @data_id = (SELECT `menu_id` FROM `sys_menu` WHERE `parent_id` = @monitor_parent_id AND `path` = 'data' LIMIT 1);

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `route_name`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT '监控数据上报', @data_id, '3', '', '', '', 'F', '0', '0', 'monitor:data:add', '#', 'admin', NOW(), ''
WHERE @data_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `parent_id` = @data_id AND `perms` = 'monitor:data:add');

UPDATE `sys_menu`
SET `route_name` = 'OpsMonitorData', `component` = 'monitor/data/index'
WHERE `parent_id` = @monitor_parent_id AND `path` = 'data';

UPDATE `sys_menu`
SET `menu_name` = '告警事件', `route_name` = 'OpsAlarm', `component` = 'monitor/alarm/index'
WHERE `parent_id` = @monitor_parent_id AND `path` = 'alarm';

SET @admin_role_id = (SELECT `role_id` FROM `sys_role` WHERE `role_key` = 'admin' LIMIT 1);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT @admin_role_id, m.`menu_id`
FROM `sys_menu` m
WHERE @admin_role_id IS NOT NULL
  AND m.`perms` IN ('monitor:data:add', 'monitor:rule:list', 'monitor:rule:query', 'monitor:rule:add', 'monitor:rule:edit', 'monitor:rule:remove')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_menu` rm WHERE rm.`role_id` = @admin_role_id AND rm.`menu_id` = m.`menu_id`
  );
