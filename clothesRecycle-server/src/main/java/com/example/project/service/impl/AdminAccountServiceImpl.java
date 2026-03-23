package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.AdminMapper;
import com.example.project.model.dto.admin.CreateAdminDto;
import com.example.project.model.dto.admin.UpdateAdminDto;
import com.example.project.model.po.Admin;
import com.example.project.service.AdminAccountService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员账号管理业务实现。
 */
@Service
public class AdminAccountServiceImpl implements AdminAccountService {

    private final AdminMapper adminMapper;

    public AdminAccountServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public List<Admin> listAdmins(Long campusId, Integer status, String keyword) {
        LambdaQueryWrapper<Admin> query = new LambdaQueryWrapper<Admin>()
                .eq(campusId != null, Admin::getCampusId, campusId)
                .eq(status != null, Admin::getStatus, status)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(Admin::getPhone, keyword)
                        .or()
                        .like(Admin::getName, keyword))
                .orderByDesc(Admin::getCreateTime);
        return adminMapper.selectList(query);
    }

    @Override
    public Admin getAdminDetail(Long adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        return admin;
    }

    @Override
    public Long createAdmin(CreateAdminDto dto) {
        if (existsPhone(dto.getPhone())) {
            throw new BusinessException("管理员手机号已存在");
        }

        validateRoleAndCampus(dto.getRole(), dto.getCampusId());

        Admin admin = new Admin();
        admin.setPhone(dto.getPhone());
        admin.setPassword(dto.getPassword());
        admin.setName(dto.getName());
        admin.setRole(dto.getRole());
        admin.setCampusId("SUPER_ADMIN".equals(dto.getRole()) ? null : dto.getCampusId());
        admin.setStatus(1);
        adminMapper.insert(admin);
        return admin.getId();
    }

    @Override
    public void updateAdmin(Long adminId, UpdateAdminDto dto) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        validateRoleAndCampus(dto.getRole(), dto.getCampusId());

        admin.setName(dto.getName());
        admin.setRole(dto.getRole());
        admin.setCampusId("SUPER_ADMIN".equals(dto.getRole()) ? null : dto.getCampusId());
        adminMapper.updateById(admin);
    }

    @Override
    public void resetPassword(Long adminId, String password) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        if (password == null || password.isBlank()) {
            throw new BusinessException("密码不能为空");
        }
        admin.setPassword(password);
        adminMapper.updateById(admin);
    }

    @Override
    public void changeAdminStatus(Long adminId, boolean enabled) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        admin.setStatus(enabled ? 1 : 0);
        adminMapper.updateById(admin);
    }

    @Override
    public void deleteAdmin(Long adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        adminMapper.deleteById(adminId);
    }

    private boolean existsPhone(String phone) {
        LambdaQueryWrapper<Admin> query = new LambdaQueryWrapper<Admin>()
                .eq(Admin::getPhone, phone);
        return adminMapper.selectCount(query) > 0;
    }

    private void validateRoleAndCampus(String role, Long campusId) {
        if (!"SUPER_ADMIN".equals(role) && !"CAMPUS_ADMIN".equals(role)) {
            throw new BusinessException("管理员角色不合法");
        }
        if ("CAMPUS_ADMIN".equals(role) && campusId == null) {
            throw new BusinessException("校区管理员必须绑定校区");
        }
    }
}
