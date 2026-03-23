package com.example.project.model.dto.campus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 保存校区请求。
 */
@Data
public class SaveCampusDto {

    @NotBlank(message = "校区名称不能为空")
    @Size(max = 64, message = "校区名称不能超过64个字符")
    private String name;

    @NotBlank(message = "校区地址不能为空")
    @Size(max = 255, message = "校区地址不能超过255个字符")
    private String address;

    @Size(max = 255, message = "备注不能超过255个字符")
    private String remark;
}
