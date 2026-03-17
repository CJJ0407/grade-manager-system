package com.cddx.www.grademanager.service.impl;

import com.cddx.www.grademanager.entity.pojo.Exam;
import com.cddx.www.grademanager.mapper.ExamMapper;
import com.cddx.www.grademanager.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 考试服务实现类（业务层）。
 *
 * 功能项：
 * - 考试增删改查
 * - 支持按科目/学期/日期范围筛选考试
 *
 * 说明：
 * - 具体权限控制主要在 Controller 层完成（管理员/教师）
 */
@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public boolean addExam(Exam exam) {
        try {
            // 插入考试（exam_id 自增）
            return examMapper.insertExam(exam) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Exam getExamById(Integer examId) {
        return examMapper.selectExamById(examId);
    }

    @Override
    public List<Exam> getAllExams() {
        return examMapper.selectAllExams();
    }

    @Override
    public List<Exam> getExamsBySubjectId(String subjectId) {
        return examMapper.selectExamsBySubjectId(subjectId);
    }

    @Override
    public List<Exam> getExamsBySemester(String semester) {
        return examMapper.selectExamsBySemester(semester);
    }

    @Override
    public List<Exam> getExamsByDateRange(Date startDate, Date endDate) {
        return examMapper.selectExamsByDateRange(startDate, endDate);
    }

    @Override
    public boolean updateExam(Exam exam) {
        try {
            // 更新考试基本信息（名称/日期/学期/所属科目）
            return examMapper.updateExam(exam) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteExamById(Integer examId) {
        try {
            // 删除考试（若考试下存在成绩记录，数据库外键可能阻止删除；需要先清理 score）
            return examMapper.deleteExamById(examId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
