package com.example.project.handler.filter;

import com.example.project.config.CorsProperties;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 跨域过滤器
 * <p>生产环境请将 Access-Control-Allow-Origin 替换为指定域名</p>
 */
@Order(1)
@Component
@WebFilter(urlPatterns = "/*")
public class CorsFilter implements Filter {

    @Autowired(required = false)
    private CorsProperties corsProperties;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String origin = request.getHeader("Origin");
        CorsProperties config = corsProperties == null ? new CorsProperties() : corsProperties;
        String allowOrigin = resolveAllowOrigin(origin, config);

        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Origin", allowOrigin);
        response.setHeader("Access-Control-Allow-Methods", String.join(", ", config.getAllowedMethods()));
        response.setHeader("Access-Control-Allow-Headers", String.join(", ", config.getAllowedHeaders()));
        response.setHeader("Access-Control-Expose-Headers", String.join(", ", config.getExposedHeaders()));
        response.setHeader("Access-Control-Max-Age", String.valueOf(config.getMaxAge()));
        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(Boolean.TRUE.equals(config.getAllowCredentials())));

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    private String resolveAllowOrigin(String requestOrigin, CorsProperties config) {
        List<String> allowedOrigins = config.getAllowedOrigins();
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            return "*";
        }

        if (allowedOrigins.contains("*")) {
            if (Boolean.TRUE.equals(config.getAllowCredentials())) {
                return (requestOrigin == null || requestOrigin.isBlank()) ? "null" : requestOrigin;
            }
            return "*";
        }

        if (requestOrigin != null && allowedOrigins.contains(requestOrigin)) {
            return requestOrigin;
        }
        return "null";
    }
}
