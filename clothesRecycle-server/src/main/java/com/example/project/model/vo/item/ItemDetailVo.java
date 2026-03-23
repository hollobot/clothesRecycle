package com.example.project.model.vo.item;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物品详情返回对象。
 */
@Data
public class ItemDetailVo {

    private Long id;
    private Long userId;
    private Long campusId;
    private String title;
    private String category;
    private String genderType;
    private String sizeType;
    private String conditionLevel;
    private String description;
    private String coverUrl;
    private List<String> imageUrls;
    private String acquireType;
    private Integer pointPrice;
    private String status;
    private LocalDateTime createTime;
}
