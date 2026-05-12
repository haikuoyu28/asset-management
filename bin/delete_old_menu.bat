@echo off
chcp 65001 >nul
echo ================================================
echo  删除冗余菜单相关文件
echo ================================================
echo.

echo 正在删除后端Controller文件...
del /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-admin\src\main\java\com\ruoyi\web\controller\system\SysPostController.java" 2>nul
del /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-admin\src\main\java\com\ruoyi\web\controller\system\SysNoticeController.java" 2>nul
del /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-admin\src\main\java\com\ruoyi\web\controller\monitor\SysUserOnlineController.java" 2>nul
del /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-admin\src\main\java\com\ruoyi\web\controller\tool\TestController.java" 2>nul
echo 完成！

echo.
echo 正在删除前端Vue页面...
rmdir /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-ui\src\views\tool" 2>nul
rmdir /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-ui\src\views\system\post" 2>nul
rmdir /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-ui\src\views\system\notice" 2>nul
rmdir /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-ui\src\views\monitor\online" 2>nul
echo 完成！

echo.
echo ================================================
echo  删除完成！
echo ================================================
echo.
pause