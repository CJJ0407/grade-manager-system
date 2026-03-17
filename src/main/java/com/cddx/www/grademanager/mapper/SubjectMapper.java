package com.cddx.www.grademanager.mapper;

import com.cddx.www.grademanager.entity.pojo.Subject;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 科目表（subject）数据访问层。
 *
 * 功能项：
 * - 对 subject 表进行增删改查
 * - 按 teacher_id 查询某教师教授的科目，用于权限控制与页面过滤
 */
@Mapper
public interface SubjectMapper {

    // 插入科目信息（subject_id 为业务主键；teacher_id 可为空）
    @Insert("INSERT INTO subject(subject_id, subject_name, credit, teacher_id) " +
            "VALUES(#{subjectId}, #{subjectName}, #{credit}, #{teacherId})")
    int insertSubject(Subject subject);

    // 根据科目ID查询科目（用于考试归属判断、页面回显）
    @Select("SELECT subject_id, subject_name, credit, teacher_id FROM subject WHERE subject_id = #{subjectId}")
    @Results({
        @Result(property = "subjectId", column = "subject_id"),
        @Result(property = "subjectName", column = "subject_name"),
        @Result(property = "credit", column = "credit"),
        @Result(property = "teacherId", column = "teacher_id")
    })
    Subject selectSubjectById(@Param("subjectId") String subjectId);

    // 根据科目名称查询科目（如需按名称去重/校验）
    @Select("SELECT subject_id, subject_name, credit, teacher_id FROM subject WHERE subject_name = #{subjectName}")
    @Results({
        @Result(property = "subjectId", column = "subject_id"),
        @Result(property = "subjectName", column = "subject_name"),
        @Result(property = "credit", column = "credit"),
        @Result(property = "teacherId", column = "teacher_id")
    })
    Subject selectSubjectByName(@Param("subjectName") String subjectName);

    // 查询所有科目（管理员管理页使用）
    @Select("SELECT subject_id, subject_name, credit, teacher_id FROM subject")
    @Results({
        @Result(property = "subjectId", column = "subject_id"),
        @Result(property = "subjectName", column = "subject_name"),
        @Result(property = "credit", column = "credit"),
        @Result(property = "teacherId", column = "teacher_id")
    })
    List<Subject> selectAllSubjects();

    // 根据教师ID查询科目列表（教师权限控制：只能管理自己的科目/考试/成绩）
    @Select("SELECT subject_id, subject_name, credit, teacher_id FROM subject WHERE teacher_id = #{teacherId}")
    @Results({
        @Result(property = "subjectId", column = "subject_id"),
        @Result(property = "subjectName", column = "subject_name"),
        @Result(property = "credit", column = "credit"),
        @Result(property = "teacherId", column = "teacher_id")
    })
    List<Subject> selectSubjectsByTeacherId(@Param("teacherId") String teacherId);

    // 更新科目信息（可修改授课教师 teacher_id）
    @Update("UPDATE subject SET subject_name=#{subjectName}, credit=#{credit}, teacher_id=#{teacherId} " +
            "WHERE subject_id=#{subjectId}")
    int updateSubject(Subject subject);

    // 根据科目ID删除科目（若科目下存在 exam，外键会阻止删除，需要先清理 exam）
    @Delete("DELETE FROM subject WHERE subject_id = #{subjectId}")
    int deleteSubjectById(@Param("subjectId") String subjectId);
}
