package com.example.project.handler.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * SPA 路由拦截器。 无文件后缀的路径视为前端路由，转发到对应的 index.html； 有后缀的路径（JS/CSS/图片等）直接放行，由静态资源处理器响应。
 */
@Component
public class SpaInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();

        // API 请求或静态文件（含后缀）直接放行
        if (path.startsWith("/api/") || path.contains(".")) {
            return true;
        }

        if (path.startsWith("/admin")) {
            request.getRequestDispatcher("/admin/index.html").forward(request, response);
        } else if (path.startsWith("/client")) {
            request.getRequestDispatcher("/client/index.html").forward(request, response);
        }
        return false;
    }
}
