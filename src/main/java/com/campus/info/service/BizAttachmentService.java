package com.campus.info.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.info.entity.BizAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BizAttachmentService extends IService<BizAttachment> {

    BizAttachment upload(MultipartFile file, Long announcementId);

    List<BizAttachment> listByAnnouncementId(Long announcementId);

    void deleteAttachment(Long id);
}
