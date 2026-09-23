package com.campus.info.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.info.common.api.ApiResult;
import com.campus.info.common.vo.UserQueryVO;
import com.campus.info.common.vo.UserSaveVO;
import com.campus.info.entity.SysUser;
import com.campus.info.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @PostMapping
    public ApiResult<SysUser> create(@RequestBody UserSaveVO vo) {
        return ApiResult.success(sysUserService.createUser(vo));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return ApiResult.success();
    }

    @PutMapping("/{id}")
    public ApiResult<SysUser> update(@PathVariable Long id, @RequestBody UserSaveVO vo) {
        return ApiResult.success(sysUserService.updateUser(id, vo));
    }

    @GetMapping("/{id}")
    public ApiResult<SysUser> getById(@PathVariable Long id) {
        return ApiResult.success(sysUserService.getUserById(id));
    }

    @GetMapping("/list")
    public ApiResult<Page<SysUser>> list(UserQueryVO query) {
        return ApiResult.success(sysUserService.listUsers(query));
    }
}
