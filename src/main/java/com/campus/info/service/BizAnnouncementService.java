package com.campus.info.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.info.common.dto.AnnouncementDetailDTO;
import com.campus.info.common.vo.AnnouncementQueryVO;
import com.campus.info.common.vo.AnnouncementSaveVO;
import com.campus.info.entity.BizAnnouncement;

public interface BizAnnouncementService extends IService<BizAnnouncement> {

    BizAnnouncement createAnnouncement(AnnouncementSaveVO vo);

    BizAnnouncement updateAnnouncement(Long id, AnnouncementSaveVO vo);

    void deleteAnnouncement(Long id);

    AnnouncementDetailDTO getAnnouncementDetail(Long id);

    Page<AnnouncementDetailDTO> listAnnouncements(AnnouncementQueryVO query);

    String summarizeAnnouncements(String startDate, String endDate);
}
