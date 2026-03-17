package com.cddx.www.grademanager.entity.pojo;

import java.util.Date;

/**
 * 考试实体（对应数据库表：exam）。
 *
 * 功能项（业务含义）：
 * - 描述一次考试/测验：名称、日期、学期、所属科目
 *
 * 关联关系：
 * - subjectId -> subject.subject_id（考试属于哪个科目）
 * - 通过 subject.teacherId 可推导考试归属教师（用于教师权限控制）
 */
public class Exam {
    private Integer examId;      // exam_id (INT, AUTO_INCREMENT, PRIMARY KEY)
    private String examName;     // exam_name (VARCHAR(100), NOT NULL)
    private Date examDate;       // exam_date (DATE, NOT NULL)
    private String semester;     // semester (VARCHAR(20), NOT NULL)
    private String subjectId;    // subject_id (VARCHAR(20), FOREIGN KEY REFERENCES subject(subject_id))

    // 无参构造函数
    public Exam() {
    }

    // 全参构造函数
    public Exam(Integer examId, String examName, Date examDate, String semester, String subjectId) {
        this.examId = examId;
        this.examName = examName;
        this.examDate = examDate;
        this.semester = semester;
        this.subjectId = subjectId;
    }

    // Getter 方法
    public Integer getExamId() {
        return examId;
    }

    public String getExamName() {
        return examName;
    }

    public Date getExamDate() {
        return examDate;
    }

    public String getSemester() {
        return semester;
    }

    public String getSubjectId() {
        return subjectId;
    }

    // Setter 方法
    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "examId=" + examId +
                ", examName='" + examName + '\'' +
                ", examDate=" + examDate +
                ", semester='" + semester + '\'' +
                ", subjectId='" + subjectId + '\'' +
                '}';
    }
}
