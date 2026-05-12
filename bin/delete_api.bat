@echo off
chcp 65001 >nul
echo ================================================
echo  删除冗余API接口文件
echo ================================================
echo.

echo 正在删除前端API文件...
del /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-ui\src\api\tool" 2>nul
del /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-ui\src\api\system\post.js" 2>nul
del /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-ui\src\api\system\notice.js" 2>nul
del /s /q "d:\Ruoyiproject\RuoYi-Vue-master\ruoyi-ui\src\api\monitor\online.js" 2>nul
echo 完成！

echo.
echo ================================================
echo  删除完成！
echo ================================================
echo.
pause