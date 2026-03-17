package com.cddx.www.grademanager.service.impl;

import com.cddx.www.grademanager.entity.pojo.Score;
import com.cddx.www.grademanager.mapper.ScoreMapper;
import com.cddx.www.grademanager.mapper.ScoreModifyRecordMapper;
import com.cddx.www.grademanager.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 成绩服务实现类（业务层）。
 *
 * 功能项：
 * - 成绩录入/查询/更新/删除
 * - 提供统计计算：平均分/最高分/最低分
 *
 * 注意：
 * - 删除成绩前需要先删除 score_modify_record，否则会触发外键约束失败
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private ScoreModifyRecordMapper scoreModifyRecordMapper;

    @Override
    public boolean addScore(Score score) {
        try {
            // 插入成绩记录（score_id 自增；record_time 使用数据库默认 CURRENT_TIMESTAMP）
            return scoreMapper.insertScore(score) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Score getScoreById(Integer scoreId) {
        return scoreMapper.selectScoreById(scoreId);
    }

    @Override
    public List<Score> getScoresByStudentId(String studentId) {
        return scoreMapper.selectScoresByStudentId(studentId);
    }

    @Override
    public List<Score> getScoresByExamId(Integer examId) {
        return scoreMapper.selectScoresByExamId(examId);
    }

    @Override
    public List<Score> getAllScores() {
        return scoreMapper.selectAllScores();
    }

    @Override
    public boolean updateScore(Score score) {
        try {
            // 更新成绩：SQL 中会刷新 record_time 为当前时间（用于“修改后立即更新时间”）
            return scoreMapper.updateScore(score) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteScoreById(Integer scoreId) {
        try {
            // 先删除该成绩相关的修改记录，避免外键约束导致删除失败
            scoreModifyRecordMapper.deleteRecordsByScoreId(scoreId);
            // 再删除成绩主记录
            return scoreMapper.deleteScoreById(scoreId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Score getScoreByStudentAndExam(String studentId, Integer examId) {
        return scoreMapper.selectScoreByStudentAndExam(studentId, examId);
    }

    @Override
    public double calculateAverageScore(Integer examId) {
        // 计算某场考试的平均分（无数据时返回 0）
        List<Score> scores = scoreMapper.selectScoresByExamId(examId);
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        double sum = scores.stream()
                .mapToDouble(score -> score.getScore().doubleValue())
                .sum();
        return sum / scores.size();
    }

    @Override
    public double getHighestScore(Integer examId) {
        // 计算某场考试最高分（无数据时返回 0）
        List<Score> scores = scoreMapper.selectScoresByExamId(examId);
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        return scores.stream()
                .mapToDouble(score -> score.getScore().doubleValue())
                .max()
                .orElse(0.0);
    }

    @Override
    public double getLowestScore(Integer examId) {
        // 计算某场考试最低分（无数据时返回 0）
        List<Score> scores = scoreMapper.selectScoresByExamId(examId);
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        return scores.stream()
                .mapToDouble(score -> score.getScore().doubleValue())
                .min()
                .orElse(0.0);
    }

    @Override
    public List<Score> getScoresByRecordedBy(String recordedBy) {
        return scoreMapper.selectScoresByRecordedBy(recordedBy);
    }
}
