package com.hunsha.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hunsha.common.PageResult;
import com.hunsha.common.Result;
import com.hunsha.entity.Schedule;
import com.hunsha.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-档期管理")
@RestController
@RequestMapping("/api/admin/schedules")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "获取档期列表")
    @GetMapping
    public Result<PageResult<Schedule>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String photographer) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Schedule::getStatus, status);
        }
        if (photographer != null && !photographer.isEmpty()) {
            wrapper.like(Schedule::getPhotographer, photographer);
        }
        wrapper.orderByAsc(Schedule::getShootDate);
        Page<Schedule> pageResult = scheduleService.page(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(), pageResult.getRecords()));
    }

    @Operation(summary = "新增档期")
    @PostMapping
    public Result<Void> create(@RequestBody Schedule schedule) {
        scheduleService.save(schedule);
        return Result.ok();
    }

    @Operation(summary = "更新档期")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Schedule schedule) {
        schedule.setId(id);
        scheduleService.updateById(schedule);
        return Result.ok();
    }

    @Operation(summary = "删除档期")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scheduleService.removeById(id);
        return Result.ok();
    }
}
