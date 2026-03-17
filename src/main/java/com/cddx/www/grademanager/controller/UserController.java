package com.cddx.www.grademanager.controller;

import com.cddx.www.grademanager.entity.pojo.User;
import com.cddx.www.grademanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户管理控制器。
 *
 * 功能项：
 * - 管理员：用户增删改查（用户管理页）
 * - 所有登录用户：查看/修改个人资料（Profile）
 *
 * 鉴权方式：
 * - 通过 HttpSession 中的 "user" 判断是否登录
 * - 通过 user.role 判断是否拥有管理员权限
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 显示用户管理页面（管理员）
     */
    @GetMapping("/manage")
    public String showUserManagePage(Model model, HttpSession session) {
        try {
            // 1) 鉴权：仅管理员可访问用户管理页
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            // 2) 查询用户列表用于表格展示
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("user", currentUser);
            return "user/manage";
        } catch (Exception e) {
            // 兜底：出现异常时跳转到统一错误页并展示原因
            e.printStackTrace();
            model.addAttribute("error", "加载用户数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 显示添加用户页面
     */
    @GetMapping("/add")
    public String showAddUserPage(Model model, HttpSession session) {
        try {
            // 鉴权：仅管理员可新增用户
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            // 渲染 templates/user/add.html
            return "user/add";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载页面失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 处理添加用户请求
     */
    @PostMapping("/add")
    public ModelAndView addUser(@RequestParam("userId") String userId,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("role") String role,
                               @RequestParam("name") String name,
                               @RequestParam("gender") String gender,
                               @RequestParam("phone") String phone,
                               @RequestParam("email") String email,
                               HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            // 1) 鉴权：仅管理员可新增用户
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            // 2) 组装用户实体（对应数据库 user 表字段）
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            user.setName(name);
            user.setGender(gender);
            user.setPhone(phone);
            user.setEmail(email);

            // 3) 持久化：写入数据库（失败常见原因：user_id/username 冲突）
            boolean success = userService.registerUser(user);
            if (success) {
                modelAndView.setViewName("redirect:/user/manage");
            } else {
                modelAndView.setViewName("user/add");
                modelAndView.addObject("error", "添加用户失败，用户ID或用户名可能已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            modelAndView.addObject("error", "添加用户失败: " + e.getMessage());
        }
        return modelAndView;
    }

    /**
     * 显示编辑用户页面
     */
    @GetMapping("/edit/{userId}")
    public String showEditUserPage(@PathVariable("userId") String userId, 
                                  Model model, HttpSession session) {
        try {
            // 鉴权：仅管理员可编辑任意用户
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            // 查询目标用户并传给编辑页渲染
            User user = userService.getUserById(userId);
            if (user != null) {
                model.addAttribute("user", user);
                return "user/edit";
            } else {
                return "redirect:/user/manage";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载用户数据失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 处理更新用户请求
     */
    @PostMapping("/update")
    public ModelAndView updateUser(@RequestParam("userId") String userId,
                                  @RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  @RequestParam("role") String role,
                                  @RequestParam("name") String name,
                                  @RequestParam("gender") String gender,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("email") String email,
                                  HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            // 1) 鉴权：仅管理员可更新任意用户
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            // 2) 组装更新对象（按 user_id 定位记录并更新其余字段）
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            user.setName(name);
            user.setGender(gender);
            user.setPhone(phone);
            user.setEmail(email);

            // 3) 更新数据库
            boolean success = userService.updateUser(user);
            if (success) {
                modelAndView.setViewName("redirect:/user/manage");
            } else {
                modelAndView.setViewName("redirect:/user/edit/" + userId);
                modelAndView.addObject("error", "更新用户失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            modelAndView.addObject("error", "更新用户失败: " + e.getMessage());
        }
        return modelAndView;
    }

    /**
     * 删除用户
     */
    @GetMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") String userId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            // 1) 鉴权：仅管理员可删除用户
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return "redirect:/login";
            }

            // 2) 保护：禁止删除当前登录的管理员账号（避免把自己删掉无法管理）
            if (currentUser.getUserId() != null && currentUser.getUserId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "不能删除当前登录的管理员账号");
                return "redirect:/user/manage";
            }

            // 3) 删除用户（Service 内部实现级联清理相关表数据以满足外键约束）
            boolean success = userService.deleteUserById(userId);
            if (!success) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "删除失败：该用户可能已被科目/考试/成绩等数据引用（外键约束），请先删除/修改相关数据后再试。"
                );
            } else {
                redirectAttributes.addFlashAttribute("success", "删除成功");
            }
            return "redirect:/user/manage";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
            return "redirect:/user/manage";
        }
    }

    /**
     * 显示个人信息页面
     */
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        try {
            // 个人信息页：仅登录用户可访问
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/login";
            }

            // 传入当前用户用于 profile 页面展示
            model.addAttribute("user", currentUser);
            return "user/profile";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载个人信息失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 显示编辑个人信息页面
     */
    @GetMapping("/editProfile")
    public String showEditProfile(Model model, HttpSession session) {
        try {
            // 编辑个人信息页：仅登录用户可访问
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/login";
            }

            // 传入当前用户用于表单回显
            model.addAttribute("user", currentUser);
            return "user/editProfile";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "加载编辑页面失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 更新个人信息
     */
    @PostMapping("/updateProfile")
    public ModelAndView updateProfile(@RequestParam("name") String name,
                                     @RequestParam("gender") String gender,
                                     @RequestParam("phone") String phone,
                                     @RequestParam("email") String email,
                                     HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            currentUser.setName(name);
            currentUser.setGender(gender);
            currentUser.setPhone(phone);
            currentUser.setEmail(email);

            boolean success = userService.updateUser(currentUser);
            if (success) {
                session.setAttribute("user", currentUser);
                modelAndView.setViewName("redirect:/user/profile");
            } else {
                modelAndView.setViewName("redirect:/user/profile");
                modelAndView.addObject("error", "更新个人信息失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            modelAndView.addObject("error", "更新个人信息失败: " + e.getMessage());
        }
        return modelAndView;
    }
}