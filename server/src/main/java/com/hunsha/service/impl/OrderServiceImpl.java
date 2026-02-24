package com.hunsha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hunsha.entity.Order;
import com.hunsha.mapper.OrderMapper;
import com.hunsha.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
}
