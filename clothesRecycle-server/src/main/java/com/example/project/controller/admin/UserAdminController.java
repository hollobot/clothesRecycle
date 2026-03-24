package com.example.project.controller.admin;

import com.example.project.common.result.Result;
import com.example.project.model.dto.user.CreateUserDto;
import com.example.project.model.dto.user.UpdateUserDto;
import com.example.project.model.po.Admin;
import com.example.project.model.po.User;
import com.example.project.service.UserManageService;
import com.example.project.service.support.AdminSessionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端用户管理控制器。
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    /**
     * 用户管理业务。
     */
    private final UserManageService userManageService;
    /**
     * 管理端会话与权限范围服务。
     */
    private final AdminSessionService adminSessionService;

    public UserAdminController(UserManageService userManageService,
                               AdminSessionService adminSessionService) {
        this.userManageService = userManageService;
        this.adminSessionService = adminSessionService;
    }

    /**
     * 用户列表。
     */
    @GetMapping
    public Result<List<User>> list(
            @RequestParam(required = false) Long campusId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword
    ) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, campusId);
        return Result.ok(userManageService.listUsers(scopedCampusId, status, keyword, scopedCampusId));
    }

    /**
     * 用户详情。
     */
    @GetMapping("/{userId}")
    public Result<User> detail(@PathVariable Long userId) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, null);
        return Result.ok(userManageService.getUserDetail(userId, scopedCampusId));
    }

    /**
     * 新增用户。
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateUserDto dto) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, dto.getCampusId());
        return Result.ok(userManageService.createUser(dto, scopedCampusId));
    }

    /**
     * 更新用户。
     */
    @PutMapping("/{userId}")
    public Result<Void> update(@PathVariable Long userId, @Valid @RequestBody UpdateUserDto dto) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, dto.getCampusId());
        userManageService.updateUser(userId, dto, scopedCampusId);
        return Result.ok();
    }

    /**
     * 启用或禁用用户。
     */
    @PostMapping("/{userId}/status")
    public Result<Void> changeStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, null);
        userManageService.changeUserStatus(userId, enabled, scopedCampusId);
        return Result.ok();
    }

    /**
     * 删除用户。
     */
    @DeleteMapping("/{userId}")
    public Result<Void> delete(@PathVariable Long userId) {
        Admin currentAdmin = adminSessionService.getCurrentAdmin();
        Long scopedCampusId = adminSessionService.resolveCampusScope(currentAdmin, null);
        userManageService.deleteUser(userId, scopedCampusId);
        return Result.ok();
    }
}
