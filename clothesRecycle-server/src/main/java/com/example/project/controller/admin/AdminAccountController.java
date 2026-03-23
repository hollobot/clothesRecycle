package com.example.project.controller.admin;

import com.example.project.common.result.Result;
import com.example.project.model.dto.admin.CreateAdminDto;
import com.example.project.model.dto.admin.UpdateAdminDto;
import com.example.project.model.po.Admin;
import com.example.project.service.AdminAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
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
 * 超级管理员账号管理控制器。
 */
@RestController
@Validated
@RequestMapping("/api/super/admin-accounts")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    public AdminAccountController(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    /**
     * 管理员账号列表。
     */
    @GetMapping
    public Result<List<Admin>> list(
            @RequestParam(required = false) Long campusId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword
    ) {
        return Result.ok(adminAccountService.listAdmins(campusId, status, keyword));
    }

    /**
     * 管理员账号详情。
     */
    @GetMapping("/{adminId}")
    public Result<Admin> detail(@PathVariable Long adminId) {
        return Result.ok(adminAccountService.getAdminDetail(adminId));
    }

    /**
     * 新增管理员账号。
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateAdminDto dto) {
        return Result.ok(adminAccountService.createAdmin(dto));
    }

    /**
     * 更新管理员账号。
     */
    @PutMapping("/{adminId}")
    public Result<Void> update(@PathVariable Long adminId, @Valid @RequestBody UpdateAdminDto dto) {
        adminAccountService.updateAdmin(adminId, dto);
        return Result.ok();
    }

    /**
     * 重置管理员密码。
     */
    @PostMapping("/{adminId}/password")
    public Result<Void> resetPassword(
            @PathVariable Long adminId,
            @RequestParam @NotBlank(message = "新密码不能为空") String password
    ) {
        adminAccountService.resetPassword(adminId, password);
        return Result.ok();
    }

    /**
     * 启用或禁用管理员账号。
     */
    @PostMapping("/{adminId}/status")
    public Result<Void> changeStatus(@PathVariable Long adminId, @RequestParam boolean enabled) {
        adminAccountService.changeAdminStatus(adminId, enabled);
        return Result.ok();
    }

    /**
     * 删除管理员账号。
     */
    @DeleteMapping("/{adminId}")
    public Result<Void> delete(@PathVariable Long adminId) {
        adminAccountService.deleteAdmin(adminId);
        return Result.ok();
    }
}
