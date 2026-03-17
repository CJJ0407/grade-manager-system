package com.cddx.www.grademanager.mapper;

import com.cddx.www.grademanager.entity.pojo.Exam;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 考试表（exam）数据访问层。
 *
 * 功能项：
 * - 对 exam 表进行增删改查
 * - 按 subject_id 查询考试列表（用于教师权限过滤与成绩录入/查询）
 */
@Mapper
public interface ExamMapper {

    // 插入考试信息（exam_id 自增主键）
    @Insert("INSERT INTO exam(exam_name, exam_date, semester, subject_id) " +
            "VALUES(#{examName}, #{examDate}, #{semester}, #{subjectId})")
    @Options(useGeneratedKeys = true, keyProperty = "examId")
    int insertExam(Exam exam);

    // 根据考试ID查询考试（用于成绩权限校验：exam -> subject -> teacher）
    @Select("SELECT exam_id, exam_name, exam_date, semester, subject_id FROM exam WHERE exam_id = #{examId}")
    @Results({
        @Result(property = "examId", column = "exam_id"),
        @Result(property = "examName", column = "exam_name"),
        @Result(property = "examDate", column = "exam_date"),
        @Result(property = "semester", column = "semester"),
        @Result(property = "subjectId", column = "subject_id")
    })
    Exam selectExamById(@Param("examId") Integer examId);

    // 查询所有考试（管理员管理页使用）
    @Select("SELECT exam_id, exam_name, exam_date, semester, subject_id FROM exam")
    @Results({
        @Result(property = "examId", column = "exam_id"),
        @Result(property = "examName", column = "exam_name"),
        @Result(property = "examDate", column = "exam_date"),
        @Result(property = "semester", column = "semester"),
        @Result(property = "subjectId", column = "subject_id")
    })
    List<Exam> selectAllExams();

    // 根据科目ID查询考试列表（教师只能看自己科目的考试）
    @Select("SELECT exam_id, exam_name, exam_date, semester, subject_id FROM exam WHERE subject_id = #{subjectId}")
    @Results({
        @Result(property = "examId", column = "exam_id"),
        @Result(property = "examName", column = "exam_name"),
        @Result(property = "examDate", column = "exam_date"),
        @Result(property = "semester", column = "semester"),
        @Result(property = "subjectId", column = "subject_id")
    })
    List<Exam> selectExamsBySubjectId(@Param("subjectId") String subjectId);

    // 删除某个科目下的所有考试（删除前需确保 score 已清理，否则外键会失败）
    @Delete("DELETE FROM exam WHERE subject_id = #{subjectId}")
    int deleteExamsBySubjectId(@Param("subjectId") String subjectId);

    // 根据学期查询考试列表（可用于筛选）
    @Select("SELECT exam_id, exam_name, exam_date, semester, subject_id FROM exam WHERE semester = #{semester}")
    @Results({
        @Result(property = "examId", column = "exam_id"),
        @Result(property = "examName", column = "exam_name"),
        @Result(property = "examDate", column = "exam_date"),
        @Result(property = "semester", column = "semester"),
        @Result(property = "subjectId", column = "subject_id")
    })
    List<Exam> selectExamsBySemester(@Param("semester") String semester);

    // 根据考试日期范围查询考试（时间范围筛选）
    @Select("SELECT * FROM exam WHERE exam_date BETWEEN #{startDate} AND #{endDate}")
    List<Exam> selectExamsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // 更新考试信息（按 exam_id 定位记录）
    @Update("UPDATE exam SET exam_name=#{examName}, exam_date=#{examDate}, semester=#{semester}, " +
            "subject_id=#{subjectId} WHERE exam_id=#{examId}")
    int updateExam(Exam exam);

    // 根据考试ID删除考试（若存在 score 外键引用，需要先清理 score）
    @Delete("DELETE FROM exam WHERE exam_id = #{examId}")
    int deleteExamById(@Param("examId") Integer examId);
}
