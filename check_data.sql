-- 检查数据库数据
USE grade_management_system;

-- 检查用户数据
SELECT '用户数据' as table_name, COUNT(*) as count FROM user;
SELECT user_id, username, role, name FROM user;

-- 检查科目数据
SELECT '科目数据' as table_name, COUNT(*) as count FROM subject;
SELECT subject_id, subject_name, credit, teacher_id FROM subject;

-- 检查考试数据
SELECT '考试数据' as table_name, COUNT(*) as count FROM exam;
SELECT exam_id, exam_name, exam_date, semester, subject_id FROM exam;

-- 检查成绩数据
SELECT '成绩数据' as table_name, COUNT(*) as count FROM score;
SELECT score_id, student_id, exam_id, score, recorded_by, record_time FROM score;

-- 检查外键关系
SELECT '外键检查' as check_type;
SELECT s.subject_id, s.subject_name, u.name as teacher_name 
FROM subject s 
LEFT JOIN user u ON s.teacher_id = u.user_id;

SELECT e.exam_id, e.exam_name, s.subject_name 
FROM exam e 
LEFT JOIN subject s ON e.subject_id = s.subject_id;

SELECT sc.score_id, u1.name as student_name, e.exam_name, sc.score, u2.name as recorded_by_name
FROM score sc 
LEFT JOIN user u1 ON sc.student_id = u1.user_id
LEFT JOIN exam e ON sc.exam_id = e.exam_id
LEFT JOIN user u2 ON sc.recorded_by = u2.user_id;
