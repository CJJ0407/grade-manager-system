package com.cddx.www.grademanager.entity.pojo;

import java.math.BigDecimal;

/**
 * 科目实体（对应数据库表：subject）。
 *
 * 功能项（业务含义）：
 * - 描述课程/科目：名称、学分、授课教师
 *
 * 关联关系：
 * - teacherId -> user.user_id（授课教师）
 * - 一个科目可包含多场考试（exam.subject_id）
 */
public class Subject {
    private String subjectId;    // subject_id (VARCHAR(20), PRIMARY KEY)
    private String subjectName;  // subject_name (VARCHAR(100), NOT NULL)
    private BigDecimal credit;   // credit (DECIMAL(3,1), NOT NULL)
    private String teacherId;    // teacher_id (VARCHAR(20), FOREIGN KEY REFERENCES user(user_id))

    // 无参构造函数
    public Subject() {
    }

    // 全参构造函数
    public Subject(String subjectId, String subjectName, BigDecimal credit, String teacherId) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.credit = credit;
        this.teacherId = teacherId;
    }

    // Getter 方法
    public String getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public String getTeacherId() {
        return teacherId;
    }

    // Setter 方法
    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subjectId='" + subjectId + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", credit=" + credit +
                ", teacherId='" + teacherId + '\'' +
                '}';
    }
}
