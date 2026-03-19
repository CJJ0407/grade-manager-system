package com.cddx.www.grademanager.mapper;

import com.cddx.www.grademanager.entity.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户表（user）数据访问层。
 *
 * 功能项：
 * - 对 user 表进行增删改查
 *
 * 说明：
 * - 该项目使用 MyBatis 注解方式编写 SQL
 * - @Results 用于把数据库列名（snake_case）映射到 Java 属性（camelCase）
 */
@Mapper
public interface UserMapper {

    // 插入用户（主键：user_id；username 唯一）
    @Insert("INSERT INTO user(user_id, username, password, role, name, gender, phone, email) " +
            "VALUES(#{userId}, #{username}, #{password}, #{role}, #{name}, #{gender}, #{phone}, #{email})")
    int insertUser(User user);

    // 根据用户ID查询用户（用于用户管理/个人信息）
    @Select("SELECT user_id, username, password, role, name, gender, phone, email FROM user WHERE user_id = #{userId}")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "role", column = "role"),
        @Result(property = "name", column = "name"),
        @Result(property = "gender", column = "gender"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "email", column = "email")
    })
    User selectUserById(@Param("userId") String userId);

    // 根据用户名查询用户（用于登录校验）
    @Select("SELECT user_id, username, password, role, name, gender, phone, email FROM user WHERE username = #{username}")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "role", column = "role"),
        @Result(property = "name", column = "name"),
        @Result(property = "gender", column = "gender"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "email", column = "email")
    })
    User selectUserByUsername(@Param("username") String username);

    // 查询所有用户（管理员用户管理页使用）
    @Select("SELECT user_id, username, password, role, name, gender, phone, email FROM user")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "role", column = "role"),
        @Result(property = "name", column = "name"),
        @Result(property = "gender", column = "gender"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "email", column = "email")
    })
    List<User> selectAllUsers();

    // 更新用户信息（按 user_id 定位记录）
    @Update("UPDATE user SET username=#{username}, password=#{password}, role=#{role}, " +
            "name=#{name}, gender=#{gender}, phone=#{phone}, email=#{email} WHERE user_id=#{userId}")
    int updateUser(User user);

    // 根据用户ID删除用户（删除前需要先处理外键引用，详见 UserServiceImpl.deleteUserById）
    @Delete("DELETE FROM user WHERE user_id = #{userId}")
    int deleteUserById(@Param("userId") String userId);

    // 根据角色查询用户列表（用于教师录入成绩时选择学生等）
    @Select("SELECT user_id, username, password, role, name, gender, phone, email FROM user WHERE role = #{role}")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "role", column = "role"),
        @Result(property = "name", column = "name"),
        @Result(property = "gender", column = "gender"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "email", column = "email")
    })
    List<User> selectUsersByRole(@Param("role") String role);
}
