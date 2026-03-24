package com.example.project.service;

import com.example.project.model.dto.user.CreateUserDto;
import com.example.project.model.dto.user.UpdateUserDto;
import com.example.project.model.po.User;

import java.util.List;

/**
 * 用户管理业务接口。
 */
public interface UserManageService {

    /**
     * 管理端查询用户列表。
     *
     * @param campusId          查询校区 ID（可选）
     * @param status            用户状态（可选）
     * @param keyword           关键词（可选）
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    List<User> listUsers(Long campusId, Integer status, String keyword, Long operatorCampusId);

    /**
     * 管理端查询用户详情。
     *
     * @param userId            用户 ID
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    User getUserDetail(Long userId, Long operatorCampusId);

    /**
     * 管理端新增用户。
     *
     * @param dto               新增参数
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    Long createUser(CreateUserDto dto, Long operatorCampusId);

    /**
     * 管理端更新用户。
     *
     * @param userId            用户 ID
     * @param dto               更新参数
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    void updateUser(Long userId, UpdateUserDto dto, Long operatorCampusId);

    /**
     * 管理端变更用户状态。
     *
     * @param userId            用户 ID
     * @param enabled           是否启用
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    void changeUserStatus(Long userId, boolean enabled, Long operatorCampusId);

    /**
     * 管理端删除用户。
     *
     * @param userId            用户 ID
     * @param operatorCampusId  操作管理员可管理的校区 ID；超级管理员传 null
     */
    void deleteUser(Long userId, Long operatorCampusId);
}
