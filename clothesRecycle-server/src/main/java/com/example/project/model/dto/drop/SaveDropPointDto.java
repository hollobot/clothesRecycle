package com.example.project.model.dto.drop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 保存回收点请求。
 */
@Data
public class SaveDropPointDto {

    @NotNull(message = "所属校区不能为空")
    private Long campusId;

    @NotBlank(message = "回收点名称不能为空")
    @Size(max = 64, message = "回收点名称不能超过64个字符")
    private String name;

    @NotBlank(message = "位置描述不能为空")
    @Size(max = 255, message = "位置描述不能超过255个字符")
    private String locationDesc;

    @NotBlank(message = "开放时间不能为空")
    @Size(max = 64, message = "开放时间不能超过64个字符")
    private String openTime;

    @Size(max = 255, message = "负责人说明不能超过255个字符")
    private String managerNote;
}
