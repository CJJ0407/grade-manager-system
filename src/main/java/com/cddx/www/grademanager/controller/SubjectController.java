package com.cddx.www.grademanager.controller;

import com.cddx.www.grademanager.entity.pojo.Subject;
import com.cddx.www.grademanager.entity.pojo.User;
import com.cddx.www.grademanager.service.SubjectService;
import com.cddx.www.grademanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 科目管理控制器。
 *
 * 功能项：
 * - 管理员：科目增删改查、分配授课教师
 * - 教师：查看“我教授的科目”、新增/修改（若页面允许）
 *
 * 备注：
 * - 具体页面与路由由 templates/subject/*.html 配合渲染
 * - 鉴权统一基于 Session 的 user 与 role
 */
@Controller
@RequestMapping("/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserService userService;

    /**
     * 显示科目管理页面（管理员）
     */
    @GetMapping("/manage")
    public String showSubjectManagePage(Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            List<Subject> subjects = subjectService.getAllSubjects();
            List<User> teachers = userService.getAllUsers();

            // 创建教师ID到教师姓名的映射
            Map<String, String> teacherMap = new HashMap<>();
            for (User user : teachers) {
                if ("teacher".equals(user.getRole()) && user.getUserId() != null && user.getName() != null) {
                    teacherMap.put(user.getUserId(), user.getName());
                }
            }

            model.addAttribute("subjects", subjects);
            model.addAttribute("teacherMap", teacherMap);
            model.addAttribute("user", currentUser);
            return "subject/manage";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 显示添加科目页面
     */
    @GetMapping("/add")
    public String showAddSubjectPage(Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/login";
            }

            if (!"admin".equals(currentUser.getRole()) && !"teacher".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            List<User> teachers = userService.getUsersByRole("teacher");
            model.addAttribute("teachers", teachers);
            model.addAttribute("currentUser", currentUser);
            return "subject/add";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 处理添加科目请求
     */
    @PostMapping("/add")
    public ModelAndView addSubject(@RequestParam("subjectId") String subjectId,
                                  @RequestParam("subjectName") String subjectName,
                                  @RequestParam("credit") BigDecimal credit,
                                  @RequestParam("teacherId") String teacherId,
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

            Subject subject = new Subject();
            subject.setSubjectId(subjectId);
            subject.setSubjectName(subjectName);
            subject.setCredit(credit);
            subject.setTeacherId(teacherId);

            boolean success = subjectService.addSubject(subject);
            if (success) {
                if ("admin".equals(currentUser.getRole())) {
                    modelAndView.setViewName("redirect:/subject/manage");
                } else {
                    modelAndView.setViewName("redirect:/subject/mySubjects");
                }
            } else {
                modelAndView.setViewName("subject/add");
                modelAndView.addObject("error", "添加科目失败，科目ID可能已存在");
                List<User> teachers = userService.getUsersByRole("teacher");
                modelAndView.addObject("teachers", teachers);
                modelAndView.addObject("currentUser", currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            modelAndView.addObject("error", "添加科目失败: " + e.getMessage());
        }
        return modelAndView;
    }

    /**
     * 显示编辑科目页面
     */
    @GetMapping("/edit/{subjectId}")
    public String showEditSubjectPage(@PathVariable("subjectId") String subjectId, 
                                     Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            Subject subject = subjectService.getSubjectById(subjectId);
            if (subject != null) {
                model.addAttribute("subject", subject);
                List<User> teachers = userService.getUsersByRole("teacher");
                model.addAttribute("teachers", teachers);
                return "subject/edit";
            } else {
                return "redirect:/subject/manage";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载科目数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 处理更新科目请求
     */
    @PostMapping("/update")
    public ModelAndView updateSubject(@RequestParam("subjectId") String subjectId,
                                     @RequestParam("subjectName") String subjectName,
                                     @RequestParam("credit") BigDecimal credit,
                                     @RequestParam("teacherId") String teacherId,
                                     HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            Subject subject = new Subject();
            subject.setSubjectId(subjectId);
            subject.setSubjectName(subjectName);
            subject.setCredit(credit);
            subject.setTeacherId(teacherId);

            boolean success = subjectService.updateSubject(subject);
            if (success) {
                modelAndView.setViewName("redirect:/subject/manage");
            } else {
                modelAndView.setViewName("redirect:/subject/edit/" + subjectId);
                modelAndView.addObject("error", "更新科目失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            modelAndView.addObject("error", "更新科目失败: " + e.getMessage());
        }
        return modelAndView;
    }

    /**
     * 删除科目
     */
    @GetMapping("/delete/{subjectId}")
    public String deleteSubject(@PathVariable("subjectId") String subjectId, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            subjectService.deleteSubjectById(subjectId);
            return "redirect:/subject/manage";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/subject/manage";
        }
    }

    /**
     * 显示科目列表（教师查看自己教授的科目）
     */
    @GetMapping("/mySubjects")
    public String showMySubjects(Model model, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"teacher".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            List<Subject> subjects = subjectService.getSubjectsByTeacherId(currentUser.getUserId());
            model.addAttribute("subjects", subjects);
            model.addAttribute("user", currentUser);
            return "subject/mySubjects";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载科目数据失败: " + e.getMessage());
            return "error";
        }
    }

}