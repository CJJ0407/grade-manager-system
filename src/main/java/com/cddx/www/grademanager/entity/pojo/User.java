package com.cddx.www.grademanager.entity.pojo;

/**
 * 用户实体（对应数据库表：user）。
 *
 * 功能项（业务含义）：
 * - 用于登录鉴权（username/password/role）
 * - 用于页面展示用户资料（name/gender/phone/email）
 * - 用于权限控制（role：student/teacher/admin）
 *
 * 注意：
 * - 当前项目中 password 为明文存储/比对（课程项目用），生产环境应使用哈希加盐存储。
 */
public class User {
    private String userId;      // user_id (VARCHAR(20), PRIMARY KEY)
    private String username;    // username (VARCHAR(30), UNIQUE, NOT NULL)
    private String password;    // password (VARCHAR(100), NOT NULL)
    private String role;        // role (ENUM('student', 'teacher', 'admin'), NOT NULL)
    private String name;        // name (VARCHAR(50), NOT NULL)
    private String gender;      // gender (ENUM('男', '女'), NOT NULL)
    private String phone;       // phone (VARCHAR(15))
    private String email;       // email (VARCHAR(50))

    // 无参构造函数
    public User() {
    }

    // 全参构造函数
    public User(String userId, String username, String password, String role, String name, String gender, String phone, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
    }

    // Getter 方法
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    // Setter 方法
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
