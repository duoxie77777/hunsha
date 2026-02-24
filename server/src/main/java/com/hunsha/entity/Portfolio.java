package com.hunsha.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "portfolio", autoResultMap = true)
public class Portfolio {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private String coverImage;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    /** 分类: 婚纱照/写真/旅拍等 */
    private String category;

    /** 是否推荐: 0-否 1-是 */
    private Integer isRecommended;

    private Integer viewCount;

    private Integer sortOrder;

    /** 状态: 0-隐藏 1-显示 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
