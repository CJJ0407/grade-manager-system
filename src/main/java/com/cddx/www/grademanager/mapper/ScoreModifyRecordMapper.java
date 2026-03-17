package com.cddx.www.grademanager.mapper;

import com.cddx.www.grademanager.entity.pojo.ScoreModifyRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 成绩修改记录表（score_modify_record）数据访问层。
 *
 * 功能项：
 * - 插入修改记录：记录“成绩被谁、在何时、从多少改到多少、原因是什么”
 * - 查询修改记录：按 modify_id / score_id / modifier 查询
 * - 删除修改记录：按 modify_id 或按关联条件批量删除（用于级联清理）
 *
 * 外键关系：
 * - score_modify_record.score_id -> score.score_id
 * - score_modify_record.modifier -> user.user_id
 *
 * 因此：
 * - 删除 score 前必须先删除对应的 score_modify_record（否则外键约束失败）
 */
@Mapper
public interface ScoreModifyRecordMapper {

    // 插入成绩修改记录
    @Insert("INSERT INTO score_modify_record(score_id, old_score, new_score, modifier, reason) " +
            "VALUES(#{scoreId}, #{oldScore}, #{newScore}, #{modifier}, #{reason})")
    @Options(useGeneratedKeys = true, keyProperty = "modifyId")
    int insertScoreModifyRecord(ScoreModifyRecord record);

    // 根据修改ID查询记录
    @Select("SELECT * FROM score_modify_record WHERE modify_id = #{modifyId}")
    ScoreModifyRecord selectRecordById(@Param("modifyId") Integer modifyId);

    // 根据成绩ID查询修改记录列表
    @Select("SELECT * FROM score_modify_record WHERE score_id = #{scoreId} ORDER BY modify_time DESC")
    List<ScoreModifyRecord> selectRecordsByScoreId(@Param("scoreId") Integer scoreId);

    // 删除某个成绩的所有修改记录
    @Delete("DELETE FROM score_modify_record WHERE score_id = #{scoreId}")
    int deleteRecordsByScoreId(@Param("scoreId") Integer scoreId);

    // 根据修改人查询记录列表
    @Select("SELECT * FROM score_modify_record WHERE modifier = #{modifier} ORDER BY modify_time DESC")
    List<ScoreModifyRecord> selectRecordsByModifier(@Param("modifier") String modifier);

    // 删除某个修改人的所有修改记录
    @Delete("DELETE FROM score_modify_record WHERE modifier = #{modifier}")
    int deleteRecordsByModifier(@Param("modifier") String modifier);

    // 删除某个学生相关的所有修改记录（通过 score 表关联）
    @Delete("DELETE FROM score_modify_record WHERE score_id IN (SELECT score_id FROM score WHERE student_id = #{studentId})")
    int deleteRecordsByStudentId(@Param("studentId") String studentId);

    // 删除某个录入人相关的所有修改记录（通过 score 表关联）
    @Delete("DELETE FROM score_modify_record WHERE score_id IN (SELECT score_id FROM score WHERE recorded_by = #{recordedBy})")
    int deleteRecordsByRecordedBy(@Param("recordedBy") String recordedBy);

    // 删除某场考试相关的所有修改记录（通过 score 表关联）
    @Delete("DELETE FROM score_modify_record WHERE score_id IN (SELECT score_id FROM score WHERE exam_id = #{examId})")
    int deleteRecordsByExamId(@Param("examId") Integer examId);

    // 查询所有修改记录
    @Select("SELECT * FROM score_modify_record ORDER BY modify_time DESC")
    List<ScoreModifyRecord> selectAllRecords();

    // 根据修改ID删除记录
    @Delete("DELETE FROM score_modify_record WHERE modify_id = #{modifyId}")
    int deleteRecordById(@Param("modifyId") Integer modifyId);
}
