USE `ry_cloud`;

DELETE FROM sys_role_menu
WHERE menu_id = 4
   OR menu_id IN (
      SELECT menu_id FROM sys_menu WHERE path = 'http://ruoyi.vip'
   );

DELETE FROM sys_menu
WHERE menu_id = 4
   OR path = 'http://ruoyi.vip';
