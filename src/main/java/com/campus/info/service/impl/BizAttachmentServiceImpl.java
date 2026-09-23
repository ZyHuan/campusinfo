package com.campus.info.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.info.common.exception.BusinessException;
import com.campus.info.entity.BizAttachment;
import com.campus.info.mapper.BizAttachmentMapper;
import com.campus.info.service.BizAttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class BizAttachmentServiceImpl
        extends ServiceImpl<BizAttachmentMapper, BizAttachment>
        implements BizAttachmentService {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Override
    public BizAttachment upload(MultipartFile file, Long announcementId) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件为空");
        }

        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path dir = Paths.get(uploadPath, dateDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new BusinessException("创建上传目录失败");
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;
        Path target = dir.resolve(storedName);

        try {
            file.transferTo(target.toFile().getAbsoluteFile());
        } catch (IOException e) {
            throw new BusinessException("文件保存失败: " + e.getMessage());
        }

        BizAttachment attachment = new BizAttachment();
        attachment.setAnnouncementId(announcementId);
        attachment.setFileName(originalName);
        attachment.setFilePath(target.toString());
        attachment.setFileSize(file.getSize());
        attachment.setFileType(file.getContentType());
        save(attachment);
        return attachment;
    }

    @Override
    public List<BizAttachment> listByAnnouncementId(Long announcementId) {
        return baseMapper.selectByAnnouncementId(announcementId);
    }

    @Override
    public void deleteAttachment(Long id) {
        BizAttachment attachment = getById(id);
        if (attachment == null) {
            throw new BusinessException("附件不存在");
        }
        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException ignored) {
        }
        removeById(id);
    }
}
