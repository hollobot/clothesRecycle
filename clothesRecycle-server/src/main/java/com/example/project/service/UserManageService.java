package com.example.project.service;

import com.example.project.model.dto.user.CreateUserDto;
import com.example.project.model.dto.user.UpdateUserDto;
import com.example.project.model.po.User;

import java.util.List;

/**
 * 用户管理业务接口。
 */
public interface UserManageService {

    List<User> listUsers(Long campusId, Integer status, String keyword);

    User getUserDetail(Long userId);

    Long createUser(CreateUserDto dto);

    void updateUser(Long userId, UpdateUserDto dto);

    void changeUserStatus(Long userId, boolean enabled);

    void deleteUser(Long userId);
}
