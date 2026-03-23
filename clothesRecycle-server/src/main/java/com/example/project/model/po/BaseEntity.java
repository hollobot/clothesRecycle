package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * PO 公共字段基类
 * <p>所有数据库实体类继承此类，自动维护 id / createTime / updateTime / deleted</p>
 */
@Data
public abstract class BaseEntity implements Serializable {

    /** 主键，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除：0-正常，1-已删除 */
    @TableLogic
    private Integer deleted;
}
