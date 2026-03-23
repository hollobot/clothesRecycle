package com.example.project.model.dto.item;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 发布物品请求。
 */
@Data
public class PublishItemDto {

    @NotBlank(message = "物品名称不能为空")
    private String title;

    @NotBlank(message = "品类不能为空")
    private String category;

    @NotBlank(message = "性别不能为空")
    private String genderType;

    @NotBlank(message = "尺码不能为空")
    private String sizeType;

    @NotBlank(message = "新旧程度不能为空")
    private String conditionLevel;

    @NotBlank(message = "描述不能为空")
    private String description;

    @NotBlank(message = "获取方式不能为空")
    private String acquireType;

    @NotNull(message = "校区不能为空")
    private Long campusId;

    @NotEmpty(message = "请至少上传一张物品图片")
    @Size(max = 9, message = "最多上传9张物品图片")
    private List<String> imageUrls;

    @Min(value = 0, message = "积分价格不能为负数")
    @Max(value = 500, message = "积分价格不能超过500")
    private Integer pointPrice;
}
