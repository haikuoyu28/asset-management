SET NAMES utf8mb4;
USE `ry_cloud`;

SET @has_agent_token = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'monitor_server'
    AND column_name = 'agent_token'
);

SET @sql = IF(@has_agent_token = 0,
  'ALTER TABLE `monitor_server` ADD COLUMN `agent_token` varchar(128) DEFAULT NULL COMMENT ''Agent上报Token'' AFTER `agent_path`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_agent_enabled = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'monitor_server'
    AND column_name = 'agent_enabled'
);

SET @sql = IF(@has_agent_enabled = 0,
  'ALTER TABLE `monitor_server` ADD COLUMN `agent_enabled` char(1) NOT NULL DEFAULT ''1'' COMMENT ''Agent状态（0启用 1停用）'' AFTER `agent_token`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
