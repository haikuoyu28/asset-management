-- =============================================
-- 删除冗余菜单SQL脚本
-- 删除：表单构建、代码生成、系统接口、岗位管理、通知公告、在线用户、若依官网
-- =============================================

USE `ry_cloud`;

-- 1. 删除"表单构建"菜单及其子菜单
DELETE FROM `sys_menu` WHERE `menu_name` = '表单构建' OR `menu_name` LIKE '表单构建%';

-- 2. 删除"代码生成"菜单及其子菜单
DELETE FROM `sys_menu` WHERE `menu_name` = '代码生成' OR `menu_name` LIKE '代码生成%';

-- 3. 删除"系统接口"菜单
DELETE FROM `sys_menu` WHERE `menu_name` = '系统接口' OR `menu_name` LIKE '系统接口%';

-- 4. 删除"岗位管理"菜单及其子菜单
DELETE FROM `sys_menu` WHERE `menu_name` = '岗位管理' OR `menu_name` LIKE '岗位管理%';

-- 5. 删除"通知公告"菜单及其子菜单
DELETE FROM `sys_menu` WHERE `menu_name` = '通知公告' OR `menu_name` LIKE '通知公告%';

-- 6. 删除"在线用户"菜单
DELETE FROM `sys_menu` WHERE `menu_name` = '在线用户' OR `menu_name` LIKE '在线用户%';

-- 7. 删除"若依官网"菜单
DELETE FROM `sys_menu` WHERE `menu_name` = '若依官网' OR `perms` LIKE '%ruoyi%';

-- 8. 删除对应的角色菜单关联
DELETE FROM `sys_role_menu`
WHERE `menu_id` NOT IN (SELECT `menu_id` FROM `sys_menu`);

-- 9. 删除对应的按钮权限
DELETE FROM `sys_menu` WHERE `menu_type` = 'F' AND `parent_id` NOT IN (SELECT `menu_id` FROM `sys_menu`);

-- 10. 清理孤儿按钮权限
DELETE FROM `sys_menu`
WHERE `menu_type` = 'F'
AND `parent_id` NOT IN (SELECT `menu_id` FROM (SELECT `menu_id` FROM `sys_menu` WHERE `menu_type` != 'F') AS t);

-- =============================================
-- 提示：执行完此SQL后，需要重启后端服务并清空Redis缓存
-- =============================================