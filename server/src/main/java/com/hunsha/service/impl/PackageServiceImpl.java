package com.hunsha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hunsha.entity.Package;
import com.hunsha.mapper.PackageMapper;
import com.hunsha.service.PackageService;
import org.springframework.stereotype.Service;

@Service
public class PackageServiceImpl extends ServiceImpl<PackageMapper, Package> implements PackageService {
}
