package com.hunsha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hunsha.entity.Portfolio;
import com.hunsha.mapper.PortfolioMapper;
import com.hunsha.service.PortfolioService;
import org.springframework.stereotype.Service;

@Service
public class PortfolioServiceImpl extends ServiceImpl<PortfolioMapper, Portfolio> implements PortfolioService {
}
