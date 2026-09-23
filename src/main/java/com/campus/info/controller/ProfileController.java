package com.campus.info.controller;

import com.campus.info.common.api.ApiResult;
import com.campus.info.common.exception.BusinessException;
import com.campus.info.common.vo.ProfileUpdateVO;
import com.campus.info.entity.SysUser;
import com.campus.info.service.SysUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Resource
    private SysUserService sysUserService;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @GetMapping
    public ApiResult<SysUser> getProfile() {
        SysUser currentUser = getCurrentUser();
        return ApiResult.success(sysUserService.getUserById(currentUser.getId()));
    }

    @PutMapping
    public ApiResult<SysUser> updateProfile(@RequestBody ProfileUpdateVO vo) {
        SysUser currentUser = getCurrentUser();
        SysUser user = sysUserService.getUserById(currentUser.getId());
        if (vo.getNickname() != null) user.setNickname(vo.getNickname());
        if (vo.getEmail() != null) user.setEmail(vo.getEmail());
        if (vo.getPhone() != null) user.setPhone(vo.getPhone());
        if (vo.getAvatar() != null) user.setAvatar(vo.getAvatar());
        sysUserService.updateById(user);
        return ApiResult.success(user);
    }

    @PostMapping("/avatar")
    public ApiResult<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) throw new BusinessException("文件为空");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过2MB");
        }

        String ext = ".png";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;
        Path dir = Paths.get(uploadPath, "avatars");
        try { Files.createDirectories(dir); } catch (IOException e) { throw new BusinessException("创建目录失败"); }
        Path target = dir.resolve(storedName);
        try { file.transferTo(target.toFile().getAbsoluteFile()); } catch (IOException e) { throw new BusinessException("文件保存失败: " + e.getMessage()); }

        String avatarPath = "/uploads/avatars/" + storedName;
        SysUser currentUser = getCurrentUser();
        SysUser user = sysUserService.getUserById(currentUser.getId());
        user.setAvatar(avatarPath);
        sysUserService.updateById(user);
        return ApiResult.success(avatarPath);
    }

    private SysUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SysUser) return (SysUser) principal;
        throw new BusinessException("未登录");
    }
}
