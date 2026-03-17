package com.cddx.www.grademanager.service.impl;

import com.cddx.www.grademanager.entity.pojo.Subject;
import com.cddx.www.grademanager.mapper.SubjectMapper;
import com.cddx.www.grademanager.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 科目服务实现类（业务层）。
 *
 * 功能项：
 * - 科目增删改查
 * - 按教师查询“我教授的科目”
 */
@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectMapper subjectMapper;

    @Override
    public boolean addSubject(Subject subject) {
        try {
            // 新增科目（subject_id 为业务主键）
            return subjectMapper.insertSubject(subject) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Subject getSubjectById(String subjectId) {
        return subjectMapper.selectSubjectById(subjectId);
    }

    @Override
    public Subject getSubjectByName(String subjectName) {
        return subjectMapper.selectSubjectByName(subjectName);
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectMapper.selectAllSubjects();
    }

    @Override
    public List<Subject> getSubjectsByTeacherId(String teacherId) {
        // 查询某位教师教授的科目列表（teacher_id 外键指向 user.user_id）
        return subjectMapper.selectSubjectsByTeacherId(teacherId);
    }

    @Override
    public boolean updateSubject(Subject subject) {
        try {
            return subjectMapper.updateSubject(subject) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSubjectById(String subjectId) {
        try {
            // 删除科目（若科目下存在考试记录，数据库外键可能阻止删除；需要先清理 exam）
            return subjectMapper.deleteSubjectById(subjectId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
