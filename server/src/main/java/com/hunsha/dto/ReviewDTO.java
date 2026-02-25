package com.hunsha.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReviewDTO {

    private Long orderId;

    private Integer rating;

    private String content;

    private List<String> images;
}
