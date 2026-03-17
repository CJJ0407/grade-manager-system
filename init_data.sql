-- 初始化测试数据
USE grade_management_system;

-- 清空现有数据
DELETE FROM score_modify_record;
DELETE FROM score;
DELETE FROM exam;
DELETE FROM subject;
DELETE FROM user;

-- 插入用户数据
INSERT INTO user (user_id, username, password, role, name, gender, phone, email) VALUES
('S001', 'wangxiaoming', '123456', 'student', '王小明', '男', '13900139001', 'wangxiaoming@email.com'),
('S002', 'lixiaohong', '123456', 'student', '李小红', '女', '13900139002', 'lixiaohong@email.com'),
('T001', 'zhangprof', '123456', 'teacher', '张教授', '男', '13800138001', 'zhang@university.edu'),
('T002', 'liprof', '123456', 'teacher', '李副教授', '女', '13800138002', 'li@university.edu'),
('A001', 'admin', 'admin', 'admin', '系统管理员', '男', '13800138003', 'admin@university.edu');

-- 插入科目数据
INSERT INTO subject (subject_id, subject_name, credit, teacher_id) VALUES
('CS101', '计算机基础', 3.0, 'T001'),
('CS102', '数据结构', 4.0, 'T001'),
('MATH101', '高等数学', 5.0, 'T002');

-- 插入考试数据
INSERT INTO exam (exam_name, exam_date, semester, subject_id) VALUES
('计算机基础期末考试', '2024-01-15', '2023-2024-1', 'CS101'),
('数据结构期末考试', '2024-01-16', '2023-2024-1', 'CS102'),
('高等数学期末考试', '2024-01-17', '2023-2024-1', 'MATH101');

-- 插入成绩数据
INSERT INTO score (student_id, exam_id, score, recorded_by) VALUES
('S001', 1, 85.5, 'T001'),
('S001', 2, 92.0, 'T001'),
('S002', 1, 78.5, 'T001'),
('S002', 2, 85.0, 'T001'),
('S001', 3, 78.5, 'T002');
