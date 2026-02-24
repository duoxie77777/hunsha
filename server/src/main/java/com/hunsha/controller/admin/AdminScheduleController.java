package com.hunsha.controller.admin;

import com.hunsha.common.Result;
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
    public Result<?> list() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "新增档期")
    @PostMapping
    public Result<?> create() {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "更新档期")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }

    @Operation(summary = "删除档期")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        // TODO
        return Result.ok();
    }
}
