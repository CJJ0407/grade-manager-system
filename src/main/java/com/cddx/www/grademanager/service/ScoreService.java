package com.cddx.www.grademanager.service;

import com.cddx.www.grademanager.entity.pojo.Score;

import java.util.List;

public interface ScoreService {
    // 录入成绩
    boolean addScore(Score score);

    // 根据成绩ID查询成绩
    Score getScoreById(Integer scoreId);

    // 根据学生ID查询成绩列表
    List<Score> getScoresByStudentId(String studentId);

    // 根据考试ID查询成绩列表
    List<Score> getScoresByExamId(Integer examId);

    // 获取所有成绩记录
    List<Score> getAllScores();

    // 更新成绩信息
    boolean updateScore(Score score);

    // 删除成绩记录
    boolean deleteScoreById(Integer scoreId);

    // 根据学生ID和考试ID查询特定成绩
    Score getScoreByStudentAndExam(String studentId, Integer examId);

    // 计算平均分
    double calculateAverageScore(Integer examId);

    // 获取最高分
    double getHighestScore(Integer examId);

    // 获取最低分
    double getLowestScore(Integer examId);

    // 根据录入人查询成绩列表
    List<Score> getScoresByRecordedBy(String recordedBy);
}
