package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.UserMapper;
import com.example.project.model.dto.user.CreateUserDto;
import com.example.project.model.dto.user.UpdateUserDto;
import com.example.project.model.po.User;
import com.example.project.service.UserManageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户管理业务实现。
 */
@Service
public class UserManageServiceImpl implements UserManageService {

    private final UserMapper userMapper;

    public UserManageServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> listUsers(Long campusId, Integer status, String keyword) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .eq(campusId != null, User::getCampusId, campusId)
                .eq(status != null, User::getStatus, status)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(User::getPhone, keyword)
                        .or()
                        .like(User::getStudentId, keyword)
                        .or()
                        .like(User::getName, keyword))
                .orderByDesc(User::getCreateTime);
        return userMapper.selectList(query);
    }

    @Override
    public User getUserDetail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public Long createUser(CreateUserDto dto) {
        if (existsPhone(dto.getPhone())) {
            throw new BusinessException("手机号已存在");
        }
        if (existsStudentId(dto.getStudentId())) {
            throw new BusinessException("学号已存在");
        }

        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setStudentId(dto.getStudentId());
        user.setName(dto.getName());
        user.setCampusId(dto.getCampusId());
        user.setAvatarUrl(dto.getAvatarUrl() == null ? "" : dto.getAvatarUrl());
        user.setPointBalance(0);
        user.setFrozenPoint(0);
        user.setStatus(1);
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public void updateUser(Long userId, UpdateUserDto dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setName(dto.getName());
        user.setCampusId(dto.getCampusId());
        user.setAvatarUrl(dto.getAvatarUrl() == null ? "" : dto.getAvatarUrl());
        userMapper.updateById(user);
    }

    @Override
    public void changeUserStatus(Long userId, boolean enabled) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(enabled ? 1 : 0);
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(userId);
    }

    private boolean existsPhone(String phone) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone);
        return userMapper.selectCount(query) > 0;
    }

    private boolean existsStudentId(String studentId) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .eq(User::getStudentId, studentId);
        return userMapper.selectCount(query) > 0;
    }
}
