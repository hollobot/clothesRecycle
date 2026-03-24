package com.example.project.controller.admin;

import com.example.project.exception.GlobalExceptionHandler;
import com.example.project.model.dto.user.CreateUserDto;
import com.example.project.model.po.Admin;
import com.example.project.service.UserManageService;
import com.example.project.service.support.AdminSessionService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户管理控制器测试。
 */
@WebMvcTest(
        controllers = UserAdminController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.project.config.SaTokenConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.project.handler.filter.CorsFilter.class)
        }
)
@Import(GlobalExceptionHandler.class)
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserManageService userManageService;

    @MockBean
    private AdminSessionService adminSessionService;

    private Admin buildCampusAdmin() {
        Admin admin = new Admin();
        admin.setId(11L);
        admin.setRole("CAMPUS_ADMIN");
        admin.setCampusId(1L);
        admin.setStatus(1);
        return admin;
    }

    @Test
    void should_create_user_when_request_valid() throws Exception {
        when(adminSessionService.getCurrentAdmin()).thenReturn(buildCampusAdmin());
        when(adminSessionService.resolveCampusScope(any(Admin.class), anyLong())).thenReturn(1L);
        when(userManageService.createUser(any(CreateUserDto.class), anyLong())).thenReturn(101L);

        String body = """
                {
                  "phone": "13800000001",
                  "password": "123456",
                  "studentId": "20230001",
                  "name": "测试用户",
                  "campusId": 1,
                  "avatarUrl": ""
                }
                """;

        String content = mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode response = objectMapper.readTree(content);
        assertThat(response.path("code").asInt()).isEqualTo(200);
        assertThat(response.path("data").asLong()).isEqualTo(101L);
    }

    @Test
    void should_return_400_when_create_user_request_invalid() throws Exception {
        when(adminSessionService.getCurrentAdmin()).thenReturn(buildCampusAdmin());

        String body = """
                {
                  "phone": "",
                  "password": "",
                  "studentId": "",
                  "name": "",
                  "campusId": null
                }
                """;

        String content = mockMvc.perform(post("/api/admin/users")
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
