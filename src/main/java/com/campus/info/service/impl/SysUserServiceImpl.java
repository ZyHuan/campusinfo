package com.campus.info.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.info.common.exception.BusinessException;
import com.campus.info.common.vo.UserQueryVO;
import com.campus.info.common.vo.UserSaveVO;
import com.campus.info.entity.SysUser;
import com.campus.info.mapper.SysUserMapper;
import com.campus.info.service.SysUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser createUser(UserSaveVO vo) {
        if (existsByUsername(vo.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(vo.getUsername());
        user.setPassword(passwordEncoder.encode(vo.getPassword()));
        user.setNickname(vo.getNickname());
        user.setEmail(vo.getEmail());
        user.setPhone(vo.getPhone());
        user.setAvatar(vo.getAvatar());
        user.setStatus(vo.getStatus() != null ? vo.getStatus() : 1);
        user.setRole("USER");
        save(user);
        return user;
    }

    @Override
    public SysUser updateUser(Long id, UserSaveVO vo) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (vo.getUsername() != null && !vo.getUsername().equals(user.getUsername())
                && existsByUsername(vo.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (vo.getUsername() != null) user.setUsername(vo.getUsername());
        if (StringUtils.hasText(vo.getPassword())) {
            user.setPassword(passwordEncoder.encode(vo.getPassword()));
        }
        if (vo.getNickname() != null) user.setNickname(vo.getNickname());
        if (vo.getEmail() != null) user.setEmail(vo.getEmail());
        if (vo.getPhone() != null) user.setPhone(vo.getPhone());
        if (vo.getAvatar() != null) user.setAvatar(vo.getAvatar());
        if (vo.getStatus() != null) {
            if (vo.getStatus() == 0 && "ADMIN".equals(user.getRole())) {
                throw new BusinessException("不能禁用管理员账号");
            }
            user.setStatus(vo.getStatus());
        }
        updateById(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (!removeById(id)) {
            throw new BusinessException("用户不存在");
        }
    }

    @Override
    public SysUser getUserById(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public Page<SysUser> listUsers(UserQueryVO query) {
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(SysUser::getUsername, query.getKeyword())
                              .or()
                              .like(SysUser::getNickname, query.getKeyword()));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return page(page, wrapper);
    }

    private boolean existsByUsername(String username) {
        return count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)) > 0;
    }
}
