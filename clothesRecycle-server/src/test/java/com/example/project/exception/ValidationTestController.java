package com.example.project.exception;

import com.example.project.common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常处理测试控制器。
 */
@RestController
@RequestMapping("/api/public/test")
public class ValidationTestController {

    /**
     * 触发参数校验。
     */
    @PostMapping("/validation")
    public Result<Void> validation(@Valid @RequestBody ValidationRequest request) {
        return Result.ok();
    }

    /**
     * 参数校验请求体。
     */
    public static class ValidationRequest {
        @NotBlank(message = "姓名不能为空")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
