package com.cddx.www.grademanager.controller;

import com.cddx.www.grademanager.entity.pojo.Exam;
import com.cddx.www.grademanager.entity.pojo.Score;
import com.cddx.www.grademanager.entity.pojo.Subject;
import com.cddx.www.grademanager.entity.pojo.User;
import com.cddx.www.grademanager.service.ExamService;
import com.cddx.www.grademanager.service.ScoreService;
import com.cddx.www.grademanager.service.SubjectService;
import com.cddx.www.grademanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ExamService examService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    /**
     * 教师权限校验：该教师是否可以操作某个考试（examId）。
     *
     * 权限规则（功能项）：
     * - 教师只能操作“自己所教科目”的考试成绩
     * - exam -> subjectId -> subject.teacherId 进行归属判断
     */
    private boolean teacherCanAccessExam(Integer examId, String teacherId) {
        if (examId == null || teacherId == null) {
            return false;
        }
        Exam exam = examService.getExamById(examId);
        if (exam == null || exam.getSubjectId() == null) {
            return false;
        }
        Subject subject = subjectService.getSubjectById(exam.getSubjectId());
        return subject != null && teacherId.equals(subject.getTeacherId());
    }

    /**
     * 获取某个教师“可管理的考试列表”。
     *
     * 功能项：
     * - 先查该教师教授的所有科目
     * - 再查每个科目下的考试
     * - 去重后返回（防止重复科目/重复考试导致重复渲染）
     */
    private List<Exam> getTeacherExams(String teacherId) {
        List<Subject> subjects = subjectService.getSubjectsByTeacherId(teacherId);
        List<Exam> exams = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        for (Subject subject : subjects) {
            if (subject.getSubjectId() == null) continue;
            List<Exam> subjectExams = examService.getExamsBySubjectId(subject.getSubjectId());
            for (Exam e : subjectExams) {
                if (e.getExamId() != null && seen.add(e.getExamId())) {
                    exams.add(e);
                }
            }
        }
        return exams;
    }

    /**
     * 显示成绩管理页面（管理员）
     */
    @GetMapping("/manage")
    public String showScoreManagePage(Model model, HttpSession session) {
        try {
            // 1) 鉴权：仅管理员可访问成绩总览管理页
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            // 2) 数据查询：管理员查看所有成绩
            List<Score> scores = scoreService.getAllScores();

            // 3) 附加数据：考试/用户/科目用于前端映射显示（ID -> 名称）
            List<Exam> exams = examService.getAllExams();

            List<User> users = userService.getAllUsers();

            List<Subject> subjects = subjectService.getAllSubjects();

            // 4) 构建 examId -> examName 映射
            Map<Integer, String> examMap = new HashMap<>();
            for (Exam exam : exams) {
                if (exam.getExamId() != null && exam.getExamName() != null) {
                    examMap.put(exam.getExamId(), exam.getExamName());
                }
            }

            // 5) 构建 userId -> userName 映射
            Map<String, String> userMap = new HashMap<>();
            for (User user : users) {
                if (user.getUserId() != null && user.getName() != null) {
                    userMap.put(user.getUserId(), user.getName());
                }
            }
            
            // 添加默认值处理
            userMap.put("", "未知用户");
            userMap.put(null, "未知用户");
            
            // 添加默认值处理
            userMap.put("", "未知用户");
            userMap.put(null, "未知用户");

            // 6) 构建 subjectId -> subjectName 映射
            Map<String, String> subjectMap = new HashMap<>();
            for (Subject subject : subjects) {
                if (subject.getSubjectId() != null && subject.getSubjectName() != null) {
                    subjectMap.put(subject.getSubjectId(), subject.getSubjectName());
                }
            }

            // 7) 注入页面渲染所需数据
            model.addAttribute("scores", scores);
            model.addAttribute("examMap", examMap);
            model.addAttribute("userMap", userMap);
            model.addAttribute("subjectMap", subjectMap);
            model.addAttribute("user", currentUser);

            return "score/manage";
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("成绩管理页面错误: " + e.getMessage());
            e.printStackTrace();

            // 返回错误页面
            model.addAttribute("error", "加载成绩数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 显示录入成绩页面（教师）
     */
    @GetMapping("/add")
    public String showAddScorePage(Model model, HttpSession session) {
        try {
            // 1) 鉴权：仅教师可录入成绩
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"teacher".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            // 2) 权限过滤：只显示该教师所教科目的考试
            List<Exam> exams = getTeacherExams(currentUser.getUserId());
            // 3) 学生列表用于选择成绩录入对象
            List<User> students = userService.getUsersByRole("student");

            model.addAttribute("exams", exams);
            model.addAttribute("students", students);
            model.addAttribute("user", currentUser);
            return "score/add";
        } catch (Exception e) {
            System.err.println("显示录入成绩页面错误: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "加载数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 处理录入成绩请求
     */
    @PostMapping("/add")
    public ModelAndView addScore(@RequestParam("studentId") String studentId,
                                @RequestParam("examId") Integer examId,
                                @RequestParam("score") BigDecimal scoreValue,
                                HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        User currentUser = (User) session.getAttribute("user");
        
        try {
            if (currentUser == null || !"teacher".equals(currentUser.getRole())) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            // 二次校验：防止通过构造请求录入非本教师科目的考试成绩
            if (!teacherCanAccessExam(examId, currentUser.getUserId())) {
                modelAndView.setViewName("redirect:/score/myScores");
                return modelAndView;
            }

            // 组装成绩实体（record_time 由数据库默认值生成）
            Score score = new Score();
            score.setStudentId(studentId);
            score.setExamId(examId);
            score.setScore(scoreValue);
            // recorded_by：记录“谁录入/修改了该成绩”，用于追溯
            score.setRecordedBy(currentUser.getUserId());

            boolean success = scoreService.addScore(score);
            if (success) {
                modelAndView.setViewName("redirect:/score/myScores");
            } else {
                modelAndView.setViewName("score/add");
                modelAndView.addObject("error", "录入成绩失败，该学生该考试的成绩可能已存在");
                List<Exam> exams = examService.getAllExams();
                List<User> students = userService.getUsersByRole("student");
                modelAndView.addObject("exams", exams);
                modelAndView.addObject("students", students);
                modelAndView.addObject("user", currentUser);
            }
        } catch (Exception e) {
            System.err.println("录入成绩错误: " + e.getMessage());
            e.printStackTrace();
            modelAndView.setViewName("score/add");
            modelAndView.addObject("error", "录入成绩失败: " + e.getMessage());
            List<Exam> exams = examService.getAllExams();
            List<User> students = userService.getUsersByRole("student");
            modelAndView.addObject("exams", exams);
            modelAndView.addObject("students", students);
            modelAndView.addObject("user", currentUser);
        }
        return modelAndView;
    }

    /**
     * 显示编辑成绩页面
     */
    @GetMapping("/edit/{scoreId}")
    public String showEditScorePage(@PathVariable("scoreId") Integer scoreId,
                                   Model model, HttpSession session) {
        try {
            // 1) 鉴权：教师/管理员可编辑成绩
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || (!"teacher".equals(currentUser.getRole()) && !"admin".equals(currentUser.getRole()))) {
                return "redirect:/login";
            }

            // 2) 查询要编辑的成绩
            Score score = scoreService.getScoreById(scoreId);
            // 3) 教师只能选择自己科目的考试；管理员可选择全部考试
            List<Exam> exams = "teacher".equals(currentUser.getRole())
                    ? getTeacherExams(currentUser.getUserId())
                    : examService.getAllExams();
            List<User> students = userService.getUsersByRole("student");

            if (score != null) {
                // 二次校验：教师不可查看/编辑非本人所教科目的成绩
                if ("teacher".equals(currentUser.getRole())
                        && !teacherCanAccessExam(score.getExamId(), currentUser.getUserId())) {
                    return "redirect:/score/myScores";
                }
                model.addAttribute("score", score);
                model.addAttribute("exams", exams);
                model.addAttribute("students", students);
                model.addAttribute("user", currentUser);
                return "score/edit";
            } else {
                return "redirect:/score/manage";
            }
        } catch (Exception e) {
            System.err.println("显示编辑成绩页面错误: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "加载成绩数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 处理更新成绩请求
     */
    @PostMapping("/update")
    public ModelAndView updateScore(@RequestParam("scoreId") Integer scoreId,
                                   @RequestParam("studentId") String studentId,
                                   @RequestParam("examId") Integer examId,
                                   @RequestParam("score") BigDecimal scoreValue,
                                   @RequestParam(value = "reason", required = false) String reason,
                                   HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || (!"teacher".equals(currentUser.getRole()) && !"admin".equals(currentUser.getRole()))) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            // 权限校验：教师只能更新自己所教科目的成绩
            if ("teacher".equals(currentUser.getRole()) && !teacherCanAccessExam(examId, currentUser.getUserId())) {
                modelAndView.setViewName("redirect:/score/myScores");
                return modelAndView;
            }

            // 组装更新对象；record_time 会在 SQL 中被刷新为当前时间（见 ScoreMapper.updateScore）
            Score score = new Score();
            score.setScoreId(scoreId);
            score.setStudentId(studentId);
            score.setExamId(examId);
            score.setScore(scoreValue);
            score.setRecordedBy(currentUser.getUserId());

            boolean success = scoreService.updateScore(score);
            if (success) {
                if ("admin".equals(currentUser.getRole())) {
                    modelAndView.setViewName("redirect:/score/manage");
                } else {
                    modelAndView.setViewName("redirect:/score/myScores");
                }
            } else {
                modelAndView.setViewName("redirect:/score/edit/" + scoreId);
                modelAndView.addObject("error", "更新成绩失败");
            }
        } catch (Exception e) {
            System.err.println("更新成绩错误: " + e.getMessage());
            e.printStackTrace();
            modelAndView.setViewName("redirect:/score/edit/" + scoreId);
            modelAndView.addObject("error", "更新成绩失败: " + e.getMessage());
        }
        return modelAndView;
    }

    /**
     * 删除成绩
     */
    @GetMapping("/delete/{scoreId}")
    public String deleteScore(@PathVariable("scoreId") Integer scoreId, HttpSession session) {
        try {
            // 鉴权：教师/管理员可删除成绩（教师会被进一步限制到“自己所教科目”）
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || (!"admin".equals(currentUser.getRole()) && !"teacher".equals(currentUser.getRole()))) {
                return "redirect:/login";
            }

            if ("teacher".equals(currentUser.getRole())) {
                // 查询要删除的成绩，用于判断该成绩是否属于本教师科目
                Score score = scoreService.getScoreById(scoreId);
                if (score == null || !teacherCanAccessExam(score.getExamId(), currentUser.getUserId())) {
                    return "redirect:/score/myScores";
                }
                // 业务删除：Service 内部会先删 score_modify_record 再删 score，避免外键约束失败
                scoreService.deleteScoreById(scoreId);
                return "redirect:/score/myScores";
            }

            scoreService.deleteScoreById(scoreId);
            return "redirect:/score/manage";
        } catch (Exception e) {
            System.err.println("删除成绩错误: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/score/manage";
        }
    }

    /**
     * 显示我的成绩（学生和教师）
     */
    @GetMapping("/myScores")
    public String showMyScores(Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/login";
            }

            List<Score> scores;
            if ("student".equals(currentUser.getRole())) {
                // 学生：只看自己的成绩
                scores = scoreService.getScoresByStudentId(currentUser.getUserId());
            } else if ("teacher".equals(currentUser.getRole())) {
                // 教师：查看自己所教科目下所有考试的学生成绩（按 examId 聚合）
                List<Exam> teacherExams = getTeacherExams(currentUser.getUserId());
                scores = new ArrayList<>();
                for (Exam e : teacherExams) {
                    if (e.getExamId() != null) {
                        scores.addAll(scoreService.getScoresByExamId(e.getExamId()));
                    }
                }
            } else {
                // 管理员：查看所有成绩
                scores = scoreService.getAllScores();
            }

            // 获取相关数据用于显示
            List<Exam> exams = examService.getAllExams();
            List<User> users = userService.getAllUsers();
            List<Subject> subjects = subjectService.getAllSubjects();

            // 创建映射
            Map<Integer, String> examMap = new HashMap<>();
            for (Exam exam : exams) {
                if (exam.getExamId() != null && exam.getExamName() != null) {
                    examMap.put(exam.getExamId(), exam.getExamName());
                }
            }

            Map<String, String> userMap = new HashMap<>();
            for (User user : users) {
                if (user.getUserId() != null && user.getName() != null) {
                    userMap.put(user.getUserId(), user.getName());
                }
            }
            
            // 添加默认值处理
            userMap.put("", "未知用户");
            userMap.put(null, "未知用户");

            Map<String, String> subjectMap = new HashMap<>();
            for (Subject subject : subjects) {
                if (subject.getSubjectId() != null && subject.getSubjectName() != null) {
                    subjectMap.put(subject.getSubjectId(), subject.getSubjectName());
                }
            }

            model.addAttribute("scores", scores);
            model.addAttribute("examMap", examMap);
            model.addAttribute("userMap", userMap);
            model.addAttribute("subjectMap", subjectMap);
            model.addAttribute("user", currentUser);
            return "score/myScores";
        } catch (Exception e) {
            System.err.println("加载我的成绩数据失败: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "加载我的成绩数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 显示成绩统计页面
     */
    @GetMapping("/statistics/{examId}")
    public String showScoreStatistics(@PathVariable("examId") Integer examId,
                                    Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/login";
            }

            Exam exam = examService.getExamById(examId);
            List<Score> scores = scoreService.getScoresByExamId(examId);

            double averageScore = scoreService.calculateAverageScore(examId);
            double highestScore = scoreService.getHighestScore(examId);
            double lowestScore = scoreService.getLowestScore(examId);

            model.addAttribute("exam", exam);
            model.addAttribute("scores", scores);
            model.addAttribute("averageScore", averageScore);
            model.addAttribute("highestScore", highestScore);
            model.addAttribute("lowestScore", lowestScore);
            model.addAttribute("user", currentUser);
            return "score/statistics";
        } catch (Exception e) {
            System.err.println("加载成绩统计数据失败: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "加载成绩统计数据失败: " + e.getMessage());
            return "error";
        }
    }
}