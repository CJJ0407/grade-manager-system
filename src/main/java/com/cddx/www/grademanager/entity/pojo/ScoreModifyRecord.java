package com.cddx.www.grademanager.entity.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 成绩修改记录实体（对应数据库表：score_modify_record）。
 *
 * 功能项（业务含义）：
 * - 记录一次成绩修改行为：改动前分数、改动后分数、修改人、修改时间、修改原因
 * - 用于审计追踪：谁在什么时候修改了哪条成绩
 *
 * 关联关系：
 * - scoreId -> score.score_id（关联到具体的成绩记录）
 * - modifier -> user.user_id（修改人：教师/管理员）
 */
public class ScoreModifyRecord {
    private Integer modifyId;    // modify_id (INT, AUTO_INCREMENT, PRIMARY KEY)
    private Integer scoreId;     // score_id (INT, FOREIGN KEY REFERENCES score(score_id))
    private BigDecimal oldScore; // old_score (DECIMAL(5,2))
    private BigDecimal newScore; // new_score (DECIMAL(5,2))
    private String modifier;     // modifier (VARCHAR(20), FOREIGN KEY REFERENCES user(user_id))
    private Date modifyTime;     // modify_time (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
    private String reason;       // reason (TEXT)

    // 无参构造函数
    public ScoreModifyRecord() {
    }

    // 全参构造函数
    public ScoreModifyRecord(Integer modifyId, Integer scoreId, BigDecimal oldScore, BigDecimal newScore, String modifier, Date modifyTime, String reason) {
        this.modifyId = modifyId;
        this.scoreId = scoreId;
        this.oldScore = oldScore;
        this.newScore = newScore;
        this.modifier = modifier;
        this.modifyTime = modifyTime;
        this.reason = reason;
    }

    // Getter 方法
    public Integer getModifyId() {
        return modifyId;
    }

    public Integer getScoreId() {
        return scoreId;
    }

    public BigDecimal getOldScore() {
        return oldScore;
    }

    public BigDecimal getNewScore() {
        return newScore;
    }

    public String getModifier() {
        return modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public String getReason() {
        return reason;
    }

    // Setter 方法
    public void setModifyId(Integer modifyId) {
        this.modifyId = modifyId;
    }

    public void setScoreId(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public void setOldScore(BigDecimal oldScore) {
        this.oldScore = oldScore;
    }

    public void setNewScore(BigDecimal newScore) {
        this.newScore = newScore;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ScoreModifyRecord{" +
                "modifyId=" + modifyId +
                ", scoreId=" + scoreId +
                ", oldScore=" + oldScore +
                ", newScore=" + newScore +
                ", modifier='" + modifier + '\'' +
                ", modifyTime=" + modifyTime +
                ", reason='" + reason + '\'' +
                '}';
    }
}
