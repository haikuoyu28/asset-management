SET NAMES utf8mb4;

-- =============================================
-- 定时任务配置SQL
-- 用于在系统管理→定时任务中配置监控采集任务
-- =============================================

USE `ry_cloud`;

-- =============================================
-- 任务1：SSH 主动采集服务器监控数据（每分钟）
-- =============================================
INSERT INTO `sys_job` (
    `job_name`,
    `job_group`,
    `invoke_target`,
    `cron_expression`,
    `misfire_policy`,
    `status`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    'SSH监控数据采集',
    'MONITOR',
    'serverMonitorTask.collectSshMonitorData',
    '0 */1 * * * ?',
    '3',
    '0',
    'admin',
    NOW(),
    '每分钟通过SSH主动采集服务器基础运行数据'
);

-- =============================================
-- 任务2：清理过期监控数据（每天凌晨2点）
-- =============================================
INSERT INTO `sys_job` (
    `job_name`,
    `job_group`,
    `invoke_target`,
    `cron_expression`,
    `misfire_policy`,
    `status`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    '清理过期监控数据',
    'MONITOR',
    'serverMonitorTask.cleanExpiredData',
    '0 0 2 * * ?',
    '3',
    '0',
    'admin',
    NOW(),
    '每天凌晨2点清理7天前的监控数据'
);

-- =============================================
-- 菜单权限配置SQL
-- 资产管理模块
-- =============================================

-- 资产管理目录
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `route_name`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    '资产管理',
    '0',
    '10',
    'asset',
    NULL,
    'M',
    '0',
    '0',
    NULL,
    'computer',
    'admin',
    NOW(),
    '资产管理目录'
);

-- 获取刚插入的资产管理目录ID，假设为100（实际需要根据表中数据调整）
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '资产管理' LIMIT 1);

-- 设备管理菜单
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    '设备管理',
    @parent_id,
    '1',
    'info',
    'asset/info/index',
    'C',
    '0',
    '0',
    'asset:info:list',
    'list',
    'admin',
    NOW(),
    '设备管理菜单'
);

-- 获取设备管理菜单ID
SET @info_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '设备管理' LIMIT 1);

-- 设备管理按钮权限
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES
('设备管理查询', @info_id, '1', '', '', 'F', '0', '0', 'asset:info:query', '#', 'admin', NOW(), ''),
('设备管理新增', @info_id, '2', '', '', 'F', '0', '0', 'asset:info:add', '#', 'admin', NOW(), ''),
('设备管理修改', @info_id, '3', '', '', 'F', '0', '0', 'asset:info:edit', '#', 'admin', NOW(), ''),
('设备管理删除', @info_id, '4', '', '', 'F', '0', '0', 'asset:info:remove', '#', 'admin', NOW(), ''),
('设备管理导出', @info_id, '5', '', '', 'F', '0', '0', 'asset:info:export', '#', 'admin', NOW(), '');

-- 资产流程菜单
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    '资产流程',
    @parent_id,
    '2',
    'flow',
    'asset/flow/index',
    'C',
    '0',
    '0',
    'asset:flow:list',
    'history',
    'admin',
    NOW(),
    '资产流程菜单'
);

-- 获取资产流程菜单ID
SET @flow_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '资产流程' LIMIT 1);

-- 资产流程按钮权限
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES
('资产流程查询', @flow_id, '1', '', '', 'F', '0', '0', 'asset:flow:query', '#', 'admin', NOW(), ''),
('资产流程新增', @flow_id, '2', '', '', 'F', '0', '0', 'asset:flow:add', '#', 'admin', NOW(), ''),
('资产流程修改', @flow_id, '3', '', '', 'F', '0', '0', 'asset:flow:edit', '#', 'admin', NOW(), ''),
('资产流程删除', @flow_id, '4', '', '', 'F', '0', '0', 'asset:flow:remove', '#', 'admin', NOW(), '');

-- =============================================
-- 运维监控模块
-- =============================================

-- 运维监控目录
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    '运维监控',
    '0',
    '20',
    'ops-monitor',
    'OpsMonitor',
    NULL,
    'M',
    '0',
    '0',
    NULL,
    'monitor',
    'admin',
    NOW(),
    '运维监控目录'
);

-- 获取刚插入的运维监控目录ID
SET @monitor_parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '运维监控' LIMIT 1);

-- 服务器管理菜单
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `route_name`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    '服务器管理',
    @monitor_parent_id,
    '1',
    'server',
    'OpsServer',
    'monitor/server/index',
    'C',
    '0',
    '0',
    'monitor:server:list',
    'server',
    'admin',
    NOW(),
    '服务器管理菜单'
);

-- 获取服务器管理菜单ID
SET @server_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '服务器管理' LIMIT 1);

-- 服务器管理按钮权限
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `route_name`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES
('服务器查询', @server_id, '1', '', '', '', 'F', '0', '0', 'monitor:server:query', '#', 'admin', NOW(), ''),
('服务器新增', @server_id, '2', '', '', '', 'F', '0', '0', 'monitor:server:add', '#', 'admin', NOW(), ''),
('服务器修改', @server_id, '3', '', '', '', 'F', '0', '0', 'monitor:server:edit', '#', 'admin', NOW(), ''),
('服务器删除', @server_id, '4', '', '', '', 'F', '0', '0', 'monitor:server:remove', '#', 'admin', NOW(), ''),
('服务器导出', @server_id, '5', '', '', '', 'F', '0', '0', 'monitor:server:export', '#', 'admin', NOW(), '');

-- 监控数据菜单
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `route_name`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    '监控数据',
    @monitor_parent_id,
    '2',
    'data',
    'OpsMonitorData',
    'monitor/data/index',
    'C',
    '0',
    '0',
    'monitor:data:list',
    'chart',
    'admin',
    NOW(),
    '监控数据菜单'
);

-- 获取监控数据菜单ID
SET @data_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '监控数据' LIMIT 1);

-- 监控数据按钮权限
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `route_name`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES
('监控数据查询', @data_id, '1', '', '', '', 'F', '0', '0', 'monitor:data:query', '#', 'admin', NOW(), ''),
('监控数据上报', @data_id, '2', '', '', '', 'F', '0', '0', 'monitor:data:add', '#', 'admin', NOW(), ''),
('监控数据删除', @data_id, '3', '', '', '', 'F', '0', '0', 'monitor:data:remove', '#', 'admin', NOW(), '');

-- 告警管理菜单
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `route_name`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    '告警管理',
    @monitor_parent_id,
    '3',
    'alarm',
    'OpsAlarm',
    'monitor/alarm/index',
    'C',
    '0',
    '0',
    'monitor:alarm:list',
    'bug',
    'admin',
    NOW(),
    '告警管理菜单'
);

-- 获取告警管理菜单ID
SET @alarm_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '告警管理' LIMIT 1);

-- 告警管理按钮权限
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES
('告警查询', @alarm_id, '1', '', '', '', 'F', '0', '0', 'monitor:alarm:query', '#', 'admin', NOW(), ''),
('告警新增', @alarm_id, '2', '', '', '', 'F', '0', '0', 'monitor:alarm:add', '#', 'admin', NOW(), ''),
('告警修改', @alarm_id, '3', '', '', '', 'F', '0', '0', 'monitor:alarm:edit', '#', 'admin', NOW(), ''),
('告警删除', @alarm_id, '4', '', '', '', 'F', '0', '0', 'monitor:alarm:remove', '#', 'admin', NOW(), '');

-- 告警规则菜单
INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `route_name`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES (
    '告警规则',
    @monitor_parent_id,
    '4',
    'rule',
    'OpsAlarmRule',
    'monitor/rule/index',
    'C',
    '0',
    '0',
    'monitor:rule:list',
    'education',
    'admin',
    NOW(),
    '告警规则菜单'
);

SET @rule_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '告警规则' LIMIT 1);

INSERT INTO `sys_menu` (
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `route_name`,
    `component`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_time`,
    `remark`
) VALUES
('告警规则查询', @rule_id, '1', '', '', '', 'F', '0', '0', 'monitor:rule:query', '#', 'admin', NOW(), ''),
('告警规则新增', @rule_id, '2', '', '', '', 'F', '0', '0', 'monitor:rule:add', '#', 'admin', NOW(), ''),
('告警规则修改', @rule_id, '3', '', '', '', 'F', '0', '0', 'monitor:rule:edit', '#', 'admin', NOW(), ''),
('告警规则删除', @rule_id, '4', '', '', '', 'F', '0', '0', 'monitor:rule:remove', '#', 'admin', NOW(), '');

-- =============================================
-- 产品菜单收敛：从 RuoYi 模板后台调整为企业 IT 运维平台
-- =============================================
UPDATE `sys_menu` SET `menu_name` = '资产中心', `order_num` = 10, `visible` = '0', `status` = '0', `icon` = 'list'
WHERE `parent_id` = 0 AND `path` = 'asset';

UPDATE `sys_menu` SET `menu_name` = '设备资产', `order_num` = 1, `visible` = '0', `status` = '0'
WHERE `parent_id` = @parent_id AND `path` = 'info';

UPDATE `sys_menu` SET `menu_name` = '资产变更', `order_num` = 2, `visible` = '0', `status` = '0'
WHERE `parent_id` = @parent_id AND `path` = 'flow';

UPDATE `sys_menu` SET `menu_name` = '监控中心', `order_num` = 20, `visible` = '0', `status` = '0', `icon` = 'monitor'
WHERE `menu_id` = @monitor_parent_id;

UPDATE `sys_menu` SET `menu_name` = '告警事件', `order_num` = 3, `visible` = '0', `status` = '0'
WHERE `parent_id` = @monitor_parent_id AND `path` = 'alarm';

UPDATE `sys_menu` SET `menu_name` = '平台管理', `order_num` = 90, `visible` = '0', `status` = '0', `icon` = 'system'
WHERE `menu_id` = 1;

UPDATE `sys_menu` SET `menu_name` = '用户管理', `order_num` = 1, `visible` = '0', `status` = '0'
WHERE `menu_id` = 100;

UPDATE `sys_menu` SET `menu_name` = '角色权限', `order_num` = 2, `visible` = '0', `status` = '0'
WHERE `menu_id` = 101;

UPDATE `sys_menu` SET `menu_name` = '菜单配置', `order_num` = 3, `visible` = '0', `status` = '0'
WHERE `menu_id` = 102;

UPDATE `sys_menu` SET `menu_name` = '日志审计', `order_num` = 4, `visible` = '0', `status` = '0'
WHERE `menu_id` = 108;

UPDATE `sys_menu` SET `visible` = '1'
WHERE `menu_id` IN (2, 3, 103, 104, 105, 106, 107, 109, 110, 111, 112, 113, 114, 115, 116, 117);

-- =============================================
-- 为管理员角色分配所有权限
-- =============================================

-- 获取管理员角色ID
SET @admin_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1);

-- 分配资产管理权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT @admin_role_id, menu_id FROM sys_menu WHERE perms LIKE 'asset:%';

-- 分配运维监控权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT @admin_role_id, menu_id FROM sys_menu WHERE perms LIKE 'monitor:%';

-- =============================================
-- 说明：
-- 1. 执行完SQL后，需要在系统管理→定时任务中启用定时任务
-- 2. 确保Redis服务已启动
-- 3. 确保MySQL数据库配置正确
-- =============================================
