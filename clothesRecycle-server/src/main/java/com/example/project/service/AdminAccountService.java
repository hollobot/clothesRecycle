package com.example.project.service;

import com.example.project.model.dto.admin.CreateAdminDto;
import com.example.project.model.dto.admin.UpdateAdminDto;
import com.example.project.model.po.Admin;

import java.util.List;

/**
 * 管理员账号管理业务接口。
 */
public interface AdminAccountService {

    List<Admin> listAdmins(Long campusId, Integer status, String keyword);

    Admin getAdminDetail(Long adminId);

    Long createAdmin(CreateAdminDto dto);

    void updateAdmin(Long adminId, UpdateAdminDto dto);

    void resetPassword(Long adminId, String password);

    void changeAdminStatus(Long adminId, boolean enabled);

    void deleteAdmin(Long adminId);
}
