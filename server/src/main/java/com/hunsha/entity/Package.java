package com.hunsha.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "`package`", autoResultMap = true)
public class Package {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String coverImage;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> includes;

    private String category;

    /** 是否热门: 0-否 1-是 */
    private Integer isHot;

    /** 状态: 0-下架 1-上架 */
    private Integer status;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
