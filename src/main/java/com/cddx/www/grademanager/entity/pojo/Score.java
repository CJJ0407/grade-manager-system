package com.cddx.www.grademanager.entity.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 成绩实体（对应数据库表：score）。
 *
 * 功能项（业务含义）：
 * - 记录学生在某次考试的成绩
 * - recordedBy 用于追踪录入/最后修改该成绩的人
 * - recordTime 表示录入/最后修改时间（更新成绩时会刷新）
 *
 * 关联关系：
 * - studentId -> user.user_id（学生）
 * - examId -> exam.exam_id（考试）
 * - recordedBy -> user.user_id（录入/修改人）
 */
public class Score {
    private Integer scoreId;     // score_id (INT, AUTO_INCREMENT, PRIMARY KEY)
    private String studentId;    // student_id (VARCHAR(20), FOREIGN KEY REFERENCES user(user_id))
    private Integer examId;      // exam_id (INT, FOREIGN KEY REFERENCES exam(exam_id))
    private BigDecimal score;    // score (DECIMAL(5,2))
    private String recordedBy;   // recorded_by (VARCHAR(20), FOREIGN KEY REFERENCES user(user_id))
    private Date recordTime;     // record_time (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

    // 无参构造函数
    public Score() {
    }

    // 全参构造函数
    public Score(Integer scoreId, String studentId, Integer examId, BigDecimal score, String recordedBy, Date recordTime) {
        this.scoreId = scoreId;
        this.studentId = studentId;
        this.examId = examId;
        this.score = score;
        this.recordedBy = recordedBy;
        this.recordTime = recordTime;
    }

    // Getter 方法
    public Integer getScoreId() {
        return scoreId;
    }

    public String getStudentId() {
        return studentId;
    }

    public Integer getExamId() {
        return examId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    // Setter 方法
    public void setScoreId(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "Score{" +
                "scoreId=" + scoreId +
                ", studentId='" + studentId + '\'' +
                ", examId=" + examId +
                ", score=" + score +
                ", recordedBy='" + recordedBy + '\'' +
                ", recordTime=" + recordTime +
                '}';
    }
}
