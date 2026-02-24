package com.hunsha.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long packageId;

    private String packageName;

    private BigDecimal amount;

    /** 状态: 待确认/已确认/拍摄中/已完成/已取消 */
    private String status;

    private LocalDate shootDate;

    private String shootTime;

    private String contactName;

    private String contactPhone;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
