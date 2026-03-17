@echo off
echo 正在启动成绩管理系统...
echo.
cd /d "%~dp0"
.\mvnw.cmd spring-boot:run -DskipTests
pause
