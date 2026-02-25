package com.hunsha.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hunsha.common.Result;
import com.hunsha.entity.Package;
import com.hunsha.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "套餐接口")
@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @Operation(summary = "获取套餐列表")
    @GetMapping
    public Result<List<Package>> list(@RequestParam(required = false) String category) {
        LambdaQueryWrapper<Package> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Package::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Package::getCategory, category);
        }
        wrapper.orderByAsc(Package::getSortOrder).orderByDesc(Package::getCreatedAt);
        List<Package> list = packageService.list(wrapper);
        return Result.ok(list);
    }

    @Operation(summary = "获取套餐详情")
    @GetMapping("/{id}")
    public Result<Package> detail(@PathVariable Long id) {
        Package pkg = packageService.getById(id);
        if (pkg == null || pkg.getStatus() != 1) {
            return Result.fail("套餐不存在");
        }
        return Result.ok(pkg);
    }
}
