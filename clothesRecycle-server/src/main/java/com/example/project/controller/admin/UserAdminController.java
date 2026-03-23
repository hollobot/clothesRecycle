package com.example.project.controller.admin;

import com.example.project.common.result.Result;
import com.example.project.model.dto.user.CreateUserDto;
import com.example.project.model.dto.user.UpdateUserDto;
import com.example.project.model.po.User;
import com.example.project.service.UserManageService;
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

    private final UserManageService userManageService;

    public UserAdminController(UserManageService userManageService) {
        this.userManageService = userManageService;
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
        return Result.ok(userManageService.listUsers(campusId, status, keyword));
    }

    /**
     * 用户详情。
     */
    @GetMapping("/{userId}")
    public Result<User> detail(@PathVariable Long userId) {
        return Result.ok(userManageService.getUserDetail(userId));
    }

    /**
     * 新增用户。
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateUserDto dto) {
        return Result.ok(userManageService.createUser(dto));
    }

    /**
     * 更新用户。
     */
    @PutMapping("/{userId}")
    public Result<Void> update(@PathVariable Long userId, @Valid @RequestBody UpdateUserDto dto) {
        userManageService.updateUser(userId, dto);
        return Result.ok();
    }

    /**
     * 启用或禁用用户。
     */
    @PostMapping("/{userId}/status")
    public Result<Void> changeStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
        userManageService.changeUserStatus(userId, enabled);
        return Result.ok();
    }

    /**
     * 删除用户。
     */
    @DeleteMapping("/{userId}")
    public Result<Void> delete(@PathVariable Long userId) {
        userManageService.deleteUser(userId);
        return Result.ok();
    }
}
