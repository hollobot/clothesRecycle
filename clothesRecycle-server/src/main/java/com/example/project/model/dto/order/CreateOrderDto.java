package com.example.project.model.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建订单请求。
 */
@Data
public class CreateOrderDto {

    @NotNull(message = "物品ID不能为空")
    private Long itemId;

    @Size(max = 255, message = "备注长度不能超过255")
    private String remark;
}
