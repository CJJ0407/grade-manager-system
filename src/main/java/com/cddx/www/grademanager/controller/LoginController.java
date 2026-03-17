package com.cddx.www.grademanager.controller;

import com.cddx.www.grademanager.entity.pojo.User;
import com.cddx.www.grademanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

/**
 * 登录与主页相关控制器。
 *
 * 功能项：
 * - 展示登录页
 * - 校验用户名/密码并写入 Session
 * - 进入系统主页
 * - 退出登录（清理 Session）
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 根路径重定向到登录页面
     */
    @GetMapping("/")
    public String redirectToLogin() {
        // 未指定路径时统一跳转到登录页
        return "redirect:/login";
    }

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String showLoginPage() {
        // 渲染 templates/login.html
        return "login";
    }

    /**
     * 处理登录请求
     */
    @PostMapping("/login")
    public ModelAndView login(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            // 调用用户服务进行账号校验（数据库查询 + 密码比对）
            User user = userService.login(username, password);
            if (user != null) {
                // 登录成功：把当前用户放入 Session，后续请求用 Session 做鉴权/取身份
                session.setAttribute("user", user);
                modelAndView.setViewName("redirect:/main");
            } else {
                // 登录失败：回到登录页并提示错误
                modelAndView.setViewName("login");
                modelAndView.addObject("error", "用户名或密码错误");
            }
        } catch (Exception e) {
            // 异常兜底：避免页面白屏
            e.printStackTrace();
            modelAndView.setViewName("login");
            modelAndView.addObject("error", "登录失败: " + e.getMessage());
        }
        return modelAndView;
    }

    /**
     * 显示主界面
     */
    @GetMapping("/main")
    public String showMainPage(HttpSession session, Model model) {
        try {
            // 从 Session 获取当前登录用户
            User user = (User) session.getAttribute("user");
            if (user == null) {
                // 未登录直接跳转到登录页
                return "redirect:/login";
            }
            // 传入前端，用于主页显示用户信息/菜单
            model.addAttribute("user", user);
            return "main";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login";
        }
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        try {
            // 清理 Session（相当于退出登录）
            session.invalidate();
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login";
        }
    }

    /**
     * 测试页面
     */
    @GetMapping("/test")
    public String test() {
        // 预留测试页面入口（若 templates/test.html 不存在，可删除该接口）
        return "test";
    }
}