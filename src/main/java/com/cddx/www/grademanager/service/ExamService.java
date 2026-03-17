package com.cddx.www.grademanager.service;

import com.cddx.www.grademanager.entity.pojo.Exam;

import java.util.Date;
import java.util.List;

public interface ExamService {
    // 添加考试
    boolean addExam(Exam exam);

    // 根据考试ID查询考试
    Exam getExamById(Integer examId);

    // 获取所有考试
    List<Exam> getAllExams();

    // 根据科目ID查询考试列表
    List<Exam> getExamsBySubjectId(String subjectId);

    // 根据学期查询考试列表
    List<Exam> getExamsBySemester(String semester);

    // 根据日期范围查询考试
    List<Exam> getExamsByDateRange(Date startDate, Date endDate);

    // 更新考试信息
    boolean updateExam(Exam exam);

    // 删除考试
    boolean deleteExamById(Integer examId);
}
