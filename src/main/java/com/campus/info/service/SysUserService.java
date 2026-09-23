package com.campus.info.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.info.entity.SysUser;
import com.campus.info.common.vo.UserQueryVO;
import com.campus.info.common.vo.UserSaveVO;

public interface SysUserService extends IService<SysUser> {

    SysUser createUser(UserSaveVO vo);

    SysUser updateUser(Long id, UserSaveVO vo);

    void deleteUser(Long id);

    SysUser getUserById(Long id);

    Page<SysUser> listUsers(UserQueryVO query);
}
