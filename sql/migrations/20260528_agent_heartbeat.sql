SET NAMES utf8mb4;
USE `ry_cloud`;

UPDATE `sys_job`
SET `job_name` = 'Agent心跳状态检查',
    `invoke_target` = 'serverMonitorTask.checkAgentHeartbeat',
    `cron_expression` = '0 */1 * * * ?',
    `remark` = '每分钟检查Agent心跳，超时后标记离线并生成告警',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `invoke_target` = 'serverMonitorTask.collectMonitorData'
   OR `job_name` = '服务器监控数据采集';
