package com.cddx.www.grademanager.service;

import com.cddx.www.grademanager.entity.pojo.Subject;

import java.util.List;

public interface SubjectService {
    // 添加科目
    boolean addSubject(Subject subject);

    // 根据科目ID查询科目
    Subject getSubjectById(String subjectId);

    // 根据科目名称查询科目
    Subject getSubjectByName(String subjectName);

    // 获取所有科目
    List<Subject> getAllSubjects();

    // 根据教师ID查询科目列表
    List<Subject> getSubjectsByTeacherId(String teacherId);

    // 更新科目信息
    boolean updateSubject(Subject subject);

    // 删除科目
    boolean deleteSubjectById(String subjectId);
}
