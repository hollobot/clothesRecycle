package com.example.project.model.dto.gift;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 保存礼品请求。
 */
@Data
public class SaveGiftDto {

    @NotBlank(message = "礼品名称不能为空")
    @Size(max = 64, message = "礼品名称不能超过64个字符")
    private String name;

    @NotBlank(message = "礼品描述不能为空")
    @Size(max = 255, message = "礼品描述不能超过255个字符")
    private String description;

    @Size(max = 255, message = "礼品图片地址不能超过255个字符")
    private String imageUrl;

    @NotNull(message = "所需积分不能为空")
    @Min(value = 1, message = "所需积分至少为1")
    @Max(value = 10000, message = "所需积分不能超过10000")
    private Integer pointCost;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;
}
