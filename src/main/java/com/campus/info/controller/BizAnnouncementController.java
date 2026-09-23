package com.campus.info.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.info.common.api.ApiResult;
import com.campus.info.common.dto.AnnouncementDetailDTO;
import com.campus.info.common.vo.AnnouncementQueryVO;
import com.campus.info.common.vo.AnnouncementSaveVO;
import com.campus.info.common.exception.BusinessException;
import com.campus.info.entity.BizAnnouncement;
import com.campus.info.service.BizAnnouncementService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/announcement")
public class BizAnnouncementController {

    @Resource
    private BizAnnouncementService bizAnnouncementService;

    @PostMapping
    public ApiResult<BizAnnouncement> create(@Valid @RequestBody AnnouncementSaveVO vo) {
        return ApiResult.success(bizAnnouncementService.createAnnouncement(vo));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        bizAnnouncementService.deleteAnnouncement(id);
        return ApiResult.success();
    }

    @PutMapping("/{id}")
    public ApiResult<BizAnnouncement> update(@PathVariable Long id, @Valid @RequestBody AnnouncementSaveVO vo) {
        return ApiResult.success(bizAnnouncementService.updateAnnouncement(id, vo));
    }

    /**
     * 获取公告详情 — 包含三表联查（公告+用户+分类）
     */
    @GetMapping("/{id}")
    public ApiResult<AnnouncementDetailDTO> getById(@PathVariable Long id) {
        return ApiResult.success(bizAnnouncementService.getAnnouncementDetail(id));
    }

    /**
     * 分页查询公告列表 — 包含三表联查（公告+用户+分类）
     */
    @GetMapping("/list")
    public ApiResult<Page<AnnouncementDetailDTO>> list(AnnouncementQueryVO query) {
        return ApiResult.success(bizAnnouncementService.listAnnouncements(query));
    }

    @PutMapping("/{id}/view")
    public ApiResult<Void> incrementView(@PathVariable Long id) {
        BizAnnouncement a = bizAnnouncementService.getById(id);
        if (a == null) throw new BusinessException("公告不存在");
        a.setViewCount(a.getViewCount() + 1);
        bizAnnouncementService.updateById(a);
        return ApiResult.success();
    }

    @GetMapping("/summary")
    public ApiResult<String> summary(@RequestParam String startDate, @RequestParam String endDate) {
        return ApiResult.success(bizAnnouncementService.summarizeAnnouncements(startDate, endDate));
    }
}
