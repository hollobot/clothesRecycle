package com.example.project.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import com.example.project.mapper.AdminMapper;
import com.example.project.model.po.Admin;
import com.example.project.utils.LogMaskUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 接口前缀与权限边界配置。
 * <p>当前阶段按路径前缀进行最小权限控制：</p>
 * <p>- /api/public/**: 匿名可访问</p>
 * <p>- /api/user/**: 需要登录 token</p>
 * <p>- /api/admin/**: 需要 ADMIN token</p>
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SaTokenConfig.class);
    private static final String TOKEN_HEADER = "Authorization";
    @Autowired(required = false)
    private AdminMapper adminMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiPrefixAuthInterceptor()).addPathPatterns("/api/**");
    }

    /**
     * 记录登录日志并执行敏感信息脱敏。
     */
    public static void logLoginEvent(String phone, String studentId, String token) {
        String logText = String.format("登录成功 phone=%s sid=%s token=%s", phone, studentId, token);
        log.info("{}", LogMaskUtil.mask(logText));
    }

    /**
     * 基于路径前缀做最小权限控制的拦截器。
     */
    private class ApiPrefixAuthInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) {
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                return true;
            }

            String uri = request.getRequestURI();
            if (uri.startsWith("/api/public/")) {
                return true;
            }

            String token = request.getHeader(TOKEN_HEADER);
            Object loginId = (token == null || token.isBlank()) ? null : StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                throw NotLoginException.newInstance(
                        NotLoginException.NOT_TOKEN,
                        NotLoginException.DEFAULT_MESSAGE,
                        null,
                        StpUtil.getLoginType()
                );
            }

            boolean isAdmin = SaManager.getStpInterface()
                    .getRoleList(loginId, StpUtil.getLoginType())
                    .contains("ADMIN");
            if (!isAdmin) {
                isAdmin = String.valueOf(loginId).startsWith("ADMIN");
            }
            if ((uri.startsWith("/api/admin/") || uri.startsWith("/api/super/")) && !isAdmin) {
                throw new NotPermissionException("ADMIN", StpUtil.getLoginType());
            }

            if (uri.startsWith("/api/super/")) {
                if (!isSuperAdmin(String.valueOf(loginId))) {
                    throw new NotPermissionException("SUPER_ADMIN", StpUtil.getLoginType());
                }
            }
            return true;
        }

        private boolean isSuperAdmin(String loginId) {
            if (!loginId.startsWith("ADMIN_")) {
                return false;
            }
            if (adminMapper == null) {
                return false;
            }
            String adminIdText = loginId.substring("ADMIN_".length());
            Long adminId;
            try {
                adminId = Long.valueOf(adminIdText);
            } catch (NumberFormatException e) {
                return false;
            }
            Admin admin = adminMapper.selectById(adminId);
            return admin != null && "SUPER_ADMIN".equals(admin.getRole()) && admin.getStatus() != null && admin.getStatus() == 1;
        }
    }
}
