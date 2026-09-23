package com.campus.info.controller;

import com.campus.info.common.api.ApiResult;
import com.campus.info.entity.BizAttachment;
import com.campus.info.service.BizAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/attachment")
public class BizAttachmentController {

    @Autowired
    private BizAttachmentService bizAttachmentService;

    @PostMapping("/upload")
    public ApiResult<BizAttachment> upload(@RequestParam("file") MultipartFile file,
                                           @RequestParam("announcementId") Long announcementId) {
        return ApiResult.success(bizAttachmentService.upload(file, announcementId));
    }

    @GetMapping("/list/{announcementId}")
    public ApiResult<List<BizAttachment>> list(@PathVariable Long announcementId) {
        return ApiResult.success(bizAttachmentService.listByAnnouncementId(announcementId));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        BizAttachment attachment = bizAttachmentService.getById(id);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(attachment.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String encodedName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedName)
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        bizAttachmentService.deleteAttachment(id);
        return ApiResult.success();
    }
}
