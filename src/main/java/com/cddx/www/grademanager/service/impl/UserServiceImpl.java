package com.cddx.www.grademanager.service.impl;

import com.cddx.www.grademanager.entity.pojo.User;
import com.cddx.www.grademanager.entity.pojo.Subject;
import com.cddx.www.grademanager.entity.pojo.Exam;
import com.cddx.www.grademanager.mapper.UserMapper;
import com.cddx.www.grademanager.mapper.SubjectMapper;
import com.cddx.www.grademanager.mapper.ExamMapper;
import com.cddx.www.grademanager.mapper.ScoreMapper;
import com.cddx.www.grademanager.mapper.ScoreModifyRecordMapper;
import com.cddx.www.grademanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现类（业务层）。
 *
 * 功能项：
 * - 注册用户：写入 user 表
 * - 登录校验：按 username 查询并校验密码
 * - 用户资料查询/更新
 * - 删除用户：执行“级联清理”后再删除 user（避免外键约束失败）
 *
 * 说明：
 * - 该项目使用 MyBatis Mapper 直接执行 SQL
 * - 删除操作使用事务，确保要么全部成功要么全部回滚
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private ScoreModifyRecordMapper scoreModifyRecordMapper;

    @Override
    public boolean registerUser(User user) {
        try {
            // 新增用户：主键/唯一键冲突会抛异常或返回 0
            return userMapper.insertUser(user) > 0;
        } catch (Exception e) {
            log.error("注册用户失败, userId={}", user.getUserId(), e);
            return false;
        }
    }

    @Override
    public User login(String username, String password) {
        // 通过用户名查询用户（username 唯一）
        User user = userMapper.selectUserByUsername(username);
        // 密码明文比对（演示/课程项目用；生产环境应使用加盐哈希）
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public User getUserById(String userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    @Override
    public boolean updateUser(User user) {
        try {
            // 按 user_id 定位并更新其余字段
            return userMapper.updateUser(user) > 0;
        } catch (Exception e) {
            log.error("更新用户失败, userId={}, 请检查数据库是否已连接且库表是否存在", user.getUserId(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteUserById(String userId) {
        try {
            // 1) 如果该用户是教师：先清理其教授的科目数据链路
            //    subject.teacher_id -> exam.subject_id -> score.exam_id -> score_modify_record.score_id
            List<Subject> subjects = subjectMapper.selectSubjectsByTeacherId(userId);
            for (Subject subject : subjects) {
                String subjectId = subject.getSubjectId();
                List<Exam> exams = examMapper.selectExamsBySubjectId(subjectId);
                for (Exam exam : exams) {
                    Integer examId = exam.getExamId();
                    // 删除顺序非常关键：先删修改记录，再删成绩，最后删考试
                    scoreModifyRecordMapper.deleteRecordsByExamId(examId);
                    scoreMapper.deleteScoresByExamId(examId);
                    examMapper.deleteExamById(examId);
                }
                // 科目被该教师引用：删除科目本身
                subjectMapper.deleteSubjectById(subjectId);
            }

            // 2) 清理与用户相关的成绩修改记录/成绩
            //    - student_id：该用户作为学生的成绩
            //    - recorded_by：该用户作为录入人/教师录入的成绩
            //    - modifier：该用户作为修改人留下的修改记录
            scoreModifyRecordMapper.deleteRecordsByStudentId(userId);
            scoreModifyRecordMapper.deleteRecordsByRecordedBy(userId);
            scoreModifyRecordMapper.deleteRecordsByModifier(userId);

            scoreMapper.deleteScoresByStudentId(userId);
            scoreMapper.deleteScoresByRecordedBy(userId);

            // 3) 最后删除 user 表记录（若仍失败，说明还有其他表存在外键引用该 user_id）
            return userMapper.deleteUserById(userId) > 0;
        } catch (Exception e) {
            log.error("删除用户失败, userId={}", userId, e);
            return false;
        }
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userMapper.selectUsersByRole(role);
    }
}
