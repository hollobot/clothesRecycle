package com.example.project.security;

import cn.dev33.satoken.stp.StpInterface;
import com.example.project.config.SaTokenConfig;
import com.example.project.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 接口前缀和权限边界测试。
 */
@WebMvcTest(controllers = {
        TestPublicController.class,
        TestAuthController.class,
        TestUserController.class,
        TestAdminController.class
})
@Import({SaTokenConfig.class, GlobalExceptionHandler.class, ApiPrefixAndRoleTest.SaTokenRoleConfig.class})
class ApiPrefixAndRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 匿名访问 public 接口应允许。
     */
    @Test
    void should_allow_anonymous_call_public_api() throws Exception {
        String content = mockMvc.perform(get("/api/public/ping"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(content);
        assertThat(json.get("code").asInt()).isEqualTo(200);
    }

    /**
     * 匿名访问 user 接口应拒绝。
     */
    @Test
    void should_reject_anonymous_call_user_api() throws Exception {
        String content = mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(content);
        assertThat(json.get("code").asInt()).isEqualTo(401);
    }

    /**
     * 匿名访问 admin 接口应拒绝。
     */
    @Test
    void should_reject_anonymous_call_admin_api() throws Exception {
        String content = mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(content);
        assertThat(json.get("code").asInt()).isEqualTo(401);
    }

    /**
     * USER 角色访问 admin 接口应拒绝。
     */
    @Test
    void should_reject_user_token_when_call_admin_api() throws Exception {
        String userToken = loginAndGetToken("USER");

        String content = mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(content);
        assertThat(json.get("code").asInt()).isEqualTo(403);
    }

    /**
     * ADMIN 角色访问 admin 接口应允许。
     */
    @Test
    void should_allow_admin_token_when_call_admin_api() throws Exception {
        String adminToken = loginAndGetToken("ADMIN");

        String content = mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(content);
        assertThat(json.get("code").asInt()).isEqualTo(200);
    }

    private String loginAndGetToken(String role) throws Exception {
        String content = mockMvc.perform(get("/api/public/test-auth/token/{role}", role))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(content);
        return json.path("data").asText();
    }

    /**
     * Sa-Token 测试角色数据源。
     */
    @TestConfiguration
    static class SaTokenRoleConfig {
        @Bean
        public StpInterface stpInterface() {
            return new StpInterface() {
                @Override
                public List<String> getPermissionList(Object loginId, String loginType) {
                    return Collections.emptyList();
                }

                @Override
                public List<String> getRoleList(Object loginId, String loginType) {
                    String id = String.valueOf(loginId);
                    if (id.startsWith("ADMIN")) {
                        return List.of("ADMIN");
                    }
                    return List.of("USER");
                }
            };
        }
    }
}
