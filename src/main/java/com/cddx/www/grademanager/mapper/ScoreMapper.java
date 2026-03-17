package com.cddx.www.grademanager.mapper;

import com.cddx.www.grademanager.entity.pojo.Score;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 成绩表（score）数据访问层。
 *
 * 功能项：
 * - 成绩录入：插入 score 记录
 * - 成绩查询：按 score_id/student_id/exam_id/recorded_by 查询
 * - 成绩更新：更新分数并刷新 record_time
 * - 成绩删除：按主键或条件批量删除（配合用户级联删除）
 *
 * 字段说明（与数据库表对应）：
 * - student_id：学生 user_id
 * - exam_id：考试 exam_id
 * - recorded_by：录入/修改人 user_id（通常为教师或管理员）
 * - record_time：录入/最后修改时间（更新时会刷新）
 */
@Mapper
public interface ScoreMapper {

    // 插入成绩记录（score_id 自增；record_time 默认 CURRENT_TIMESTAMP）
    @Insert("INSERT INTO score(student_id, exam_id, score, recorded_by) " +
            "VALUES(#{studentId}, #{examId}, #{score}, #{recordedBy})")
    @Options(useGeneratedKeys = true, keyProperty = "scoreId")
    int insertScore(Score score);

    // 根据成绩ID查询成绩
    @Select("SELECT score_id, student_id, exam_id, score, recorded_by, record_time FROM score WHERE score_id = #{scoreId}")
    @Results({
        @Result(property = "scoreId", column = "score_id"),
        @Result(property = "studentId", column = "student_id"),
        @Result(property = "examId", column = "exam_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "recordedBy", column = "recorded_by"),
        @Result(property = "recordTime", column = "record_time")
    })
    Score selectScoreById(@Param("scoreId") Integer scoreId);

    // 根据学生ID查询成绩列表
    @Select("SELECT score_id, student_id, exam_id, score, recorded_by, record_time FROM score WHERE student_id = #{studentId}")
    @Results({
        @Result(property = "scoreId", column = "score_id"),
        @Result(property = "studentId", column = "student_id"),
        @Result(property = "examId", column = "exam_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "recordedBy", column = "recorded_by"),
        @Result(property = "recordTime", column = "record_time")
    })
    List<Score> selectScoresByStudentId(@Param("studentId") String studentId);

    // 根据考试ID查询成绩列表
    @Select("SELECT score_id, student_id, exam_id, score, recorded_by, record_time FROM score WHERE exam_id = #{examId}")
    @Results({
        @Result(property = "scoreId", column = "score_id"),
        @Result(property = "studentId", column = "student_id"),
        @Result(property = "examId", column = "exam_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "recordedBy", column = "recorded_by"),
        @Result(property = "recordTime", column = "record_time")
    })
    List<Score> selectScoresByExamId(@Param("examId") Integer examId);

    // 查询所有成绩记录
    @Select("SELECT score_id, student_id, exam_id, score, recorded_by, record_time FROM score")
    @Results({
        @Result(property = "scoreId", column = "score_id"),
        @Result(property = "studentId", column = "student_id"),
        @Result(property = "examId", column = "exam_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "recordedBy", column = "recorded_by"),
        @Result(property = "recordTime", column = "record_time")
    })
    List<Score> selectAllScores();

    // 更新成绩信息，并刷新 record_time（用于“修改后录入时间立即更新”）
    @Update("UPDATE score SET student_id=#{studentId}, exam_id=#{examId}, score=#{score}, " +
            "recorded_by=#{recordedBy}, record_time=CURRENT_TIMESTAMP WHERE score_id=#{scoreId}")
    int updateScore(Score score);

    // 根据成绩ID删除成绩记录
    @Delete("DELETE FROM score WHERE score_id = #{scoreId}")
    int deleteScoreById(@Param("scoreId") Integer scoreId);

    // 删除某个学生的所有成绩（用于删除用户时级联清理）
    @Delete("DELETE FROM score WHERE student_id = #{studentId}")
    int deleteScoresByStudentId(@Param("studentId") String studentId);

    // 删除某个录入人的所有成绩（用于删除用户时级联清理）
    @Delete("DELETE FROM score WHERE recorded_by = #{recordedBy}")
    int deleteScoresByRecordedBy(@Param("recordedBy") String recordedBy);

    // 删除某场考试的所有成绩（删除 exam 前必须先清理 score）
    @Delete("DELETE FROM score WHERE exam_id = #{examId}")
    int deleteScoresByExamId(@Param("examId") Integer examId);

    // 根据学生ID和考试ID查询特定成绩
    @Select("SELECT * FROM score WHERE student_id = #{studentId} AND exam_id = #{examId}")
    Score selectScoreByStudentAndExam(@Param("studentId") String studentId, @Param("examId") Integer examId);

    // 根据录入人查询成绩列表
    @Select("SELECT score_id, student_id, exam_id, score, recorded_by, record_time FROM score WHERE recorded_by = #{recordedBy}")
    @Results({
        @Result(property = "scoreId", column = "score_id"),
        @Result(property = "studentId", column = "student_id"),
        @Result(property = "examId", column = "exam_id"),
        @Result(property = "score", column = "score"),
        @Result(property = "recordedBy", column = "recorded_by"),
        @Result(property = "recordTime", column = "record_time")
    })
    List<Score> selectScoresByRecordedBy(@Param("recordedBy") String recordedBy);
}
