package com.cddx.www.grademanager.controller;

import com.cddx.www.grademanager.entity.pojo.Exam;
import com.cddx.www.grademanager.entity.pojo.Subject;
import com.cddx.www.grademanager.entity.pojo.User;
import com.cddx.www.grademanager.service.ExamService;
import com.cddx.www.grademanager.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 考试管理控制器。
 *
 * 功能项：
 * - 管理员：考试增删改查（全量）
 * - 教师：仅可对自己教授科目的考试进行管理（通过 subjectService 按 teacherId 过滤）
 *
 * 注意：
 * - 日期在表单中通常以字符串提交，Controller 中会做解析与校验
 * - 页面展示需要 subjectId -> subjectName 的映射（subjectMap）
 */
@Controller
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;
    
    @Autowired
    private SubjectService subjectService;

    /**
     * 显示考试管理页面（管理员）
     */
    @GetMapping("/manage")
    public String showExamManagePage(Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }
            
            // 获取数据
            List<Exam> exams = examService.getAllExams();
            List<Subject> subjects = subjectService.getAllSubjects();
            
            // 创建科目映射
            Map<String, String> subjectMap = new HashMap<>();
            for (Subject subject : subjects) {
                if (subject.getSubjectId() != null && subject.getSubjectName() != null) {
                    subjectMap.put(subject.getSubjectId(), subject.getSubjectName());
                }
            }
            
            // 设置模型属性
            model.addAttribute("exams", exams);
            model.addAttribute("subjectMap", subjectMap);
            model.addAttribute("user", currentUser);
            
            return "exam/manage";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 显示添加考试页面
     */
    @GetMapping("/add")
    public String showAddExamPage(Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/login";
            }

            if (!"admin".equals(currentUser.getRole()) && !"teacher".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            List<Subject> subjects;
            if ("teacher".equals(currentUser.getRole())) {
                subjects = subjectService.getSubjectsByTeacherId(currentUser.getUserId());
            } else {
                subjects = subjectService.getAllSubjects();
            }

            model.addAttribute("subjects", subjects);
            model.addAttribute("currentUser", currentUser);
            return "exam/add";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 处理添加考试请求
     */
    @PostMapping("/add")
    public ModelAndView addExam(@RequestParam("examName") String examName,
                               @RequestParam("examDate") String examDateStr,
                               @RequestParam("semester") String semester,
                               @RequestParam("subjectId") String subjectId,
                               HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            if (!"admin".equals(currentUser.getRole()) && !"teacher".equals(currentUser.getRole())) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date examDate = sdf.parse(examDateStr);
            
            Exam exam = new Exam();
            exam.setExamName(examName);
            exam.setExamDate(examDate);
            exam.setSemester(semester);
            exam.setSubjectId(subjectId);

            boolean success = examService.addExam(exam);
            if (success) {
                if ("admin".equals(currentUser.getRole())) {
                    modelAndView.setViewName("redirect:/exam/manage");
                } else {
                    modelAndView.setViewName("redirect:/exam/myExams");
                }
            } else {
                modelAndView.setViewName("exam/add");
                modelAndView.addObject("error", "添加考试失败");
                List<Subject> subjects;
                if ("teacher".equals(currentUser.getRole())) {
                    subjects = subjectService.getSubjectsByTeacherId(currentUser.getUserId());
                } else {
                    subjects = subjectService.getAllSubjects();
                }
                modelAndView.addObject("subjects", subjects);
                modelAndView.addObject("currentUser", currentUser);
            }
        } catch (ParseException e) {
            modelAndView.setViewName("exam/add");
            modelAndView.addObject("error", "日期格式错误");
            User currentUser = (User) session.getAttribute("user");
            List<Subject> subjects;
            if ("teacher".equals(currentUser.getRole())) {
                subjects = subjectService.getSubjectsByTeacherId(currentUser.getUserId());
            } else {
                subjects = subjectService.getAllSubjects();
            }
            modelAndView.addObject("subjects", subjects);
            modelAndView.addObject("currentUser", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            modelAndView.addObject("error", "添加考试失败: " + e.getMessage());
        }
        return modelAndView;
    }


    /**
     * 显示编辑考试页面
     */
    @GetMapping("/edit/{examId}")
    public String showEditExamPage(@PathVariable("examId") Integer examId, 
                                  Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            Exam exam = examService.getExamById(examId);
            if (exam != null) {
                model.addAttribute("exam", exam);
                List<Subject> subjects = subjectService.getAllSubjects();
                model.addAttribute("subjects", subjects);
                return "exam/edit";
            } else {
                return "redirect:/exam/manage";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载考试数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 处理更新考试请求
     */
    @PostMapping("/update")
    public ModelAndView updateExam(@RequestParam("examId") Integer examId,
                                  @RequestParam("examName") String examName,
                                  @RequestParam("examDate") String examDateStr,
                                  @RequestParam("semester") String semester,
                                  @RequestParam("subjectId") String subjectId,
                                  HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date examDate = sdf.parse(examDateStr);
            
            Exam exam = new Exam();
            exam.setExamId(examId);
            exam.setExamName(examName);
            exam.setExamDate(examDate);
            exam.setSemester(semester);
            exam.setSubjectId(subjectId);

            boolean success = examService.updateExam(exam);
            if (success) {
                modelAndView.setViewName("redirect:/exam/manage");
            } else {
                modelAndView.setViewName("redirect:/exam/edit/" + examId);
                modelAndView.addObject("error", "更新考试失败");
            }
        } catch (ParseException e) {
            modelAndView.setViewName("redirect:/exam/edit/" + examId);
            modelAndView.addObject("error", "日期格式错误");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            modelAndView.addObject("error", "更新考试失败: " + e.getMessage());
        }
        return modelAndView;
    }

    /**
     * 删除考试
     */
    @GetMapping("/delete/{examId}")
    public String deleteExam(@PathVariable("examId") Integer examId, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            examService.deleteExamById(examId);
            return "redirect:/exam/manage";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/exam/manage";
        }
    }

    /**
     * 显示我的考试（教师）
     */
    @GetMapping("/myExams")
    public String showMyExams(Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"teacher".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            // 获取教师教授的科目
            List<Subject> mySubjects = subjectService.getSubjectsByTeacherId(currentUser.getUserId());
            
            // 获取所有考试
            List<Exam> allExams = examService.getAllExams();
            
            // 过滤出教师相关的考试
            List<Exam> exams = allExams.stream()
                    .filter(exam -> mySubjects.stream()
                            .anyMatch(subject -> subject.getSubjectId().equals(exam.getSubjectId())))
                    .toList();

            // 创建科目映射
            Map<String, String> subjectMap = new HashMap<>();
            for (Subject subject : subjectService.getAllSubjects()) {
                if (subject.getSubjectId() != null && subject.getSubjectName() != null) {
                    subjectMap.put(subject.getSubjectId(), subject.getSubjectName());
                }
            }

            model.addAttribute("exams", exams);
            model.addAttribute("subjectMap", subjectMap);
            model.addAttribute("user", currentUser);
            return "exam/myExams";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载我的考试数据失败: " + e.getMessage());
            return "error";
        }
    }
}