package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回收点实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("drop_point")
public class DropPoint extends BaseEntity {

    private Long campusId;
    private String name;
    private String locationDesc;
    private String openTime;
    private String managerNote;
    private Integer stockCount;
    private Integer status;
}
