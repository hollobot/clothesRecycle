package com.example.project.controller.admin;

import com.example.project.exception.GlobalExceptionHandler;
import com.example.project.model.dto.admin.CreateAdminDto;
import com.example.project.service.AdminAccountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理员账号管理控制器测试。
 */
@WebMvcTest(
        controllers = AdminAccountController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.project.config.SaTokenConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.project.handler.filter.CorsFilter.class)
        }
)
@Import(GlobalExceptionHandler.class)
class AdminAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminAccountService adminAccountService;

    @Test
    void should_create_admin_when_request_valid() throws Exception {
        when(adminAccountService.createAdmin(any(CreateAdminDto.class))).thenReturn(301L);

        String body = """
                {
                  "phone": "18800000001",
                  "password": "123456",
                  "name": "校区管理员",
                  "role": "CAMPUS_ADMIN",
                  "campusId": 1
                }
                """;

        String content = mockMvc.perform(post("/api/super/admin-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode response = objectMapper.readTree(content);
        assertThat(response.path("code").asInt()).isEqualTo(200);
        assertThat(response.path("data").asLong()).isEqualTo(301L);
    }

    @Test
    void should_return_400_when_create_admin_request_invalid() throws Exception {
        String body = """
                {
                  "phone": "",
                  "password": "",
                  "name": "",
                  "role": ""
                }
                """;

        String content = mockMvc.perform(post("/api/super/admin-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode response = objectMapper.readTree(content);
        assertThat(response.path("code").asInt()).isEqualTo(400);
    }
}
