package com.example.project.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 签到记录实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sign_record")
public class SignRecord extends BaseEntity {

    private Long userId;
    private LocalDate signDate;
    private Integer rewardPoint;
}
