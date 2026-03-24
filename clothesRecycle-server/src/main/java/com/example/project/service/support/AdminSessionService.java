package com.example.project.service.support;

import cn.dev33.satoken.stp.StpUtil;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.AdminMapper;
import com.example.project.model.po.Admin;
import org.springframework.stereotype.Service;

/**
 * 管理端会话与权限范围服务。
 */
@Service
public class AdminSessionService {

    /**
     * 管理员数据访问。
     */
    private final AdminMapper adminMapper;

    public AdminSessionService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    /**
     * 获取当前登录管理员。
     *
     * @return 当前有效管理员
     */
    public Admin getCurrentAdmin() {
        String loginId = String.valueOf(StpUtil.getLoginId());
        if (!loginId.startsWith("ADMIN_")) {
            throw new BusinessException("管理员登录态无效，请重新登录");
        }
        String adminIdText = loginId.substring("ADMIN_".length());
        Long adminId;
        try {
            adminId = Long.valueOf(adminIdText);
        } catch (NumberFormatException e) {
            throw new BusinessException("管理员登录态无效，请重新登录");
        }
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null || admin.getStatus() == null || admin.getStatus() != 1) {
            throw new BusinessException("管理员不存在或已被禁用");
        }
        return admin;
    }

    /**
     * 判断是否超级管理员。
     *
     * @param admin 管理员实体
     * @return 是否超级管理员
     */
    public boolean isSuperAdmin(Admin admin) {
        return admin != null && "SUPER_ADMIN".equals(admin.getRole());
    }

    /**
     * 计算当前管理员可操作的校区范围。
     *
     * @param admin            当前管理员
     * @param requestedCampusId 请求中指定的校区 ID（可选）
     * @return 可操作校区 ID；超级管理员返回请求值（可为 null）
     */
    public Long resolveCampusScope(Admin admin, Long requestedCampusId) {
        if (isSuperAdmin(admin)) {
            return requestedCampusId;
        }
        Long adminCampusId = admin == null ? null : admin.getCampusId();
        if (adminCampusId == null) {
            throw new BusinessException("校区管理员未配置所属校区");
        }
        if (requestedCampusId != null && !adminCampusId.equals(requestedCampusId)) {
            throw new BusinessException("无权限操作其他校区数据");
        }
        return adminCampusId;
    }

    /**
     * 校验是否有指定校区操作权限。
     *
     * @param admin         当前管理员
     * @param targetCampusId 目标校区 ID
     * @param action        业务动作描述
     */
    public void requireCampusAccess(Admin admin, Long targetCampusId, String action) {
        if (isSuperAdmin(admin)) {
            return;
        }
        Long adminCampusId = admin == null ? null : admin.getCampusId();
        if (adminCampusId == null) {
            throw new BusinessException("校区管理员未配置所属校区");
        }
        if (targetCampusId == null || !adminCampusId.equals(targetCampusId)) {
            throw new BusinessException("无权限" + action + "其他校区数据");
        }
    }

    /**
     * 校验超级管理员权限。
     *
     * @param admin  当前管理员
     * @param action 业务动作描述
     */
    public void requireSuperAdmin(Admin admin, String action) {
        if (!isSuperAdmin(admin)) {
            throw new BusinessException("仅超级管理员可" + action);
        }
    }
}
