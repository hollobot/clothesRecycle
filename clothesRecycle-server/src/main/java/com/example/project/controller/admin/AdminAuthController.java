package com.example.project.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.common.result.Result;
import com.example.project.exception.BusinessException;
import com.example.project.mapper.AdminMapper;
import com.example.project.model.po.Admin;
import com.example.project.model.vo.auth.LoginVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理端认证控制器。
 */
@RestController
@RequestMapping("/api/public/admin")
public class AdminAuthController {

    private final AdminMapper adminMapper;

    public AdminAuthController(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    /**
     * 管理员登录。
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody Map<String, String> body) {
        String phone = body.getOrDefault("phone", "");
        String password = body.getOrDefault("password", "");

        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getPhone, phone)
                .eq(Admin::getPassword, password)
                .eq(Admin::getStatus, 1));
        if (admin == null) {
            throw new BusinessException("管理员账号或密码错误");
        }

        String loginId = "ADMIN_" + admin.getId();
        StpUtil.login(loginId);

        LoginVo vo = new LoginVo();
        vo.setUserId(admin.getId());
        vo.setToken(StpUtil.getTokenValue());
        vo.setRole(admin.getRole());
        vo.setName(admin.getName());
        // 返回管理员所属校区，供前端按校区管理员权限做默认筛选与只读展示。
        vo.setCampusId(admin.getCampusId());
        return Result.ok(vo);
    }
}
