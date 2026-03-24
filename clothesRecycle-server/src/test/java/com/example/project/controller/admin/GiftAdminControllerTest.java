package com.example.project.controller.admin;

import com.example.project.exception.BusinessException;
import com.example.project.exception.GlobalExceptionHandler;
import com.example.project.model.po.Admin;
import com.example.project.service.GiftService;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 礼品管理权限控制测试。
 */
@WebMvcTest(
        controllers = GiftAdminController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.project.config.SaTokenConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.project.handler.filter.CorsFilter.class)
        }
)
@Import(GlobalExceptionHandler.class)
class GiftAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 礼品业务 Mock。
     */
    @MockBean
    private GiftService giftService;

    /**
     * 管理端会话与权限范围服务 Mock。
     */
    @MockBean
    private AdminSessionService adminSessionService;

    @Test
    void should_reject_gift_management_when_operator_is_campus_admin() throws Exception {
        Admin campusAdmin = new Admin();
        campusAdmin.setId(21L);
        campusAdmin.setRole("CAMPUS_ADMIN");
        campusAdmin.setCampusId(1L);
        campusAdmin.setStatus(1);

        when(adminSessionService.getCurrentAdmin()).thenReturn(campusAdmin);
        doThrow(new BusinessException("仅超级管理员可管理礼品"))
                .when(adminSessionService)
                .requireSuperAdmin(any(Admin.class), any(String.class));

        String content = mockMvc.perform(get("/api/admin/gifts"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode response = objectMapper.readTree(content);
        assertThat(response.path("code").asInt()).isEqualTo(400);
        assertThat(response.path("msg").asText()).isNotBlank();
    }
}
