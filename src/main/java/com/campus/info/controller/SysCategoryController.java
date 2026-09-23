package com.campus.info.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.info.common.api.ApiResult;
import com.campus.info.common.exception.BusinessException;
import com.campus.info.entity.SysCategory;
import com.campus.info.mapper.SysCategoryMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class SysCategoryController {

    @Resource
    private SysCategoryMapper sysCategoryMapper;

    @GetMapping("/list")
    public ApiResult<?> list(@RequestParam(required = false) Integer pageNum,
                              @RequestParam(required = false) Integer pageSize) {
        if (pageNum != null && pageSize != null) {
            Page<SysCategory> page = new Page<>(pageNum, pageSize);
            Page<SysCategory> result = sysCategoryMapper.selectPage(page,
                    new LambdaQueryWrapper<SysCategory>().orderByAsc(SysCategory::getSortOrder));
            return ApiResult.success(Map.of("records", result.getRecords(), "total", result.getTotal()));
        }
        return ApiResult.success(sysCategoryMapper.selectList(
                new LambdaQueryWrapper<SysCategory>().orderByAsc(SysCategory::getSortOrder)));
    }

    @PostMapping
    public ApiResult<SysCategory> create(@Valid @RequestBody SysCategory category) {
        sysCategoryMapper.insert(category);
        return ApiResult.success(category);
    }

    @PutMapping("/{id}")
    public ApiResult<SysCategory> update(@PathVariable Long id, @Valid @RequestBody SysCategory category) {
        SysCategory existing = sysCategoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }
        category.setId(id);
        sysCategoryMapper.updateById(category);
        return ApiResult.success(category);
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        if (sysCategoryMapper.deleteById(id) == 0) {
            throw new BusinessException("分类不存在");
        }
        return ApiResult.success();
    }
}
