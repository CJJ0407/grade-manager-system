package com.cddx.www.grademanager.service;

import com.cddx.www.grademanager.entity.pojo.User;

import java.util.List;

public interface UserService {
    // 用户注册
    boolean registerUser(User user);

    // 用户登录验证
    User login(String username, String password);

    // 根据用户ID查询用户
    User getUserById(String userId);

    // 根据用户名查询用户
    User getUserByUsername(String username);

    // 获取所有用户
    List<User> getAllUsers();

    // 更新用户信息
    boolean updateUser(User user);

    // 删除用户
    boolean deleteUserById(String userId);

    // 根据角色获取用户列表
    List<User> getUsersByRole(String role);
}
