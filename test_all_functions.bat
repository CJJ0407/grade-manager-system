@echo off
chcp 65001
echo 测试所有功能模块...

REM 切换到脚本所在目录
cd /d "%~dp0"

echo 1. 测试数据库连接...
mysql -u root -p123456 -e "USE grade_management_system; SELECT COUNT(*) as user_count FROM user;"

echo.
echo 2. 测试应用启动...
echo 请手动启动应用：start_app.bat
echo 然后访问以下URL进行测试：

echo.
echo === 管理员功能测试 ===
echo 登录: http://localhost:8083/login (admin/admin)
echo 用户管理: http://localhost:8083/user/manage
echo 科目管理: http://localhost:8083/subject/manage
echo 考试管理: http://localhost:8083/exam/manage
echo 成绩管理: http://localhost:8083/score/manage

echo.
echo === 教师功能测试 ===
echo 登录: http://localhost:8083/login (zhangprof/123456)
echo 我的科目: http://localhost:8083/subject/mySubjects
echo 我的考试: http://localhost:8083/exam/myExams
echo 录入成绩: http://localhost:8083/score/add
echo 成绩管理: http://localhost:8083/score/myScores

echo.
echo === 学生功能测试 ===
echo 登录: http://localhost:8083/login (wangxiaoming/123456)
echo 我的成绩: http://localhost:8083/score/myScores

echo.
echo === 通用功能测试 ===
echo 个人信息: http://localhost:8083/user/profile

echo.
echo 测试完成后按任意键退出...
pause
