package com.hunsha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hunsha.entity.Review;
import com.hunsha.mapper.ReviewMapper;
import com.hunsha.service.ReviewService;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
}
