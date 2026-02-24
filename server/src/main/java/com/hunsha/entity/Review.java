package com.hunsha.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "review", autoResultMap = true)
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long orderId;

    /** 评分: 1-5 */
    private Integer rating;

    private String content;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    /** 是否精选: 0-否 1-是 */
    private Integer isFeatured;

    private String adminReply;

    private LocalDateTime replyAt;

    /** 状态: 0-隐藏 1-显示 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
