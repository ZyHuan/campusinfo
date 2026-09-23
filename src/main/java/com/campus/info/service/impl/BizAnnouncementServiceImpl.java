package com.campus.info.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.info.common.ai.AIService;
import com.campus.info.common.dto.AnnouncementDetailDTO;
import com.campus.info.common.exception.BusinessException;
import com.campus.info.common.vo.AnnouncementQueryVO;
import com.campus.info.common.vo.AnnouncementSaveVO;
import com.campus.info.entity.BizAnnouncement;
import com.campus.info.entity.SysUser;
import com.campus.info.mapper.BizAnnouncementMapper;
import com.campus.info.service.BizAnnouncementService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BizAnnouncementServiceImpl
        extends ServiceImpl<BizAnnouncementMapper, BizAnnouncement>
        implements BizAnnouncementService {

    @Resource
    private AIService aiService;

    @Override
    @CacheEvict(value = {"announcement:list", "summary"}, allEntries = true)
    public BizAnnouncement createAnnouncement(AnnouncementSaveVO vo) {
        BizAnnouncement announcement = new BizAnnouncement();
        announcement.setTitle(vo.getTitle());
        announcement.setContent(vo.getContent());
        announcement.setUserId(vo.getUserId() != null ? vo.getUserId() : getCurrentUserId());
        announcement.setCategoryId(vo.getCategoryId());
        announcement.setViewCount(0);
        if ("scheduled".equals(vo.getPublishType()) && vo.getPublishTime() != null && !vo.getPublishTime().isEmpty()) {
            LocalDateTime publishTime = LocalDateTime.parse(vo.getPublishTime());
            if (publishTime.isBefore(LocalDateTime.now())) {
                throw new BusinessException("发布时间必须晚于当前时间");
            }
            announcement.setStatus(2);
            announcement.setPublishTime(publishTime);
        } else {
            announcement.setStatus(vo.getStatus() != null && vo.getStatus() == 0 ? 0 : 1);
        }
        save(announcement);
        return announcement;
    }

    @Override
    @CacheEvict(value = {"announcement:list", "announcement:detail", "summary"}, allEntries = true)
    public BizAnnouncement updateAnnouncement(Long id, AnnouncementSaveVO vo) {
        BizAnnouncement announcement = getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcement.setTitle(vo.getTitle());
        announcement.setContent(vo.getContent());
        if (vo.getUserId() != null) announcement.setUserId(vo.getUserId());
        if (vo.getCategoryId() != null) announcement.setCategoryId(vo.getCategoryId());
        if (vo.getStatus() != null) announcement.setStatus(vo.getStatus());
        updateById(announcement);
        return announcement;
    }

    @Override
    @CacheEvict(value = {"announcement:list", "announcement:detail", "summary"}, allEntries = true)
    public void deleteAnnouncement(Long id) {
        if (!removeById(id)) {
            throw new BusinessException("公告不存在");
        }
    }

    @Override
    @Cacheable(value = "announcement:detail", key = "#id", unless = "#result == null")
    public AnnouncementDetailDTO getAnnouncementDetail(Long id) {
        AnnouncementDetailDTO detail = baseMapper.selectDetailById(id);
        if (detail == null) {
            throw new BusinessException("公告不存在");
        }
        return detail;
    }

    @Override
    @Cacheable(value = "announcement:list", key = "#query.pageNum + ':' + #query.pageSize + ':' + #query.keyword + ':' + #query.categoryId + ':' + #query.userId", unless = "#result == null || #result.records.size() == 0")
    public Page<AnnouncementDetailDTO> listAnnouncements(AnnouncementQueryVO query) {
        Page<BizAnnouncement> page = new Page<>(query.getPageNum(), query.getPageSize());
        return baseMapper.selectPageWithDetails(page, query.getKeyword(),
                query.getCategoryId(), query.getUserId());
    }

    @Override
    @Cacheable(value = "summary", key = "#startDate + ':' + #endDate", unless = "#result == null")
    public String summarizeAnnouncements(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        if (Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays() > 90) {
            throw new BusinessException("时间范围不能超过90天，请缩小范围");
        }
        String endDateInclusive = end.plusDays(1).toString();
        List<AnnouncementDetailDTO> list = baseMapper.selectByDateRange(startDate, endDateInclusive);
        if (list.size() >= 100) {
            throw new BusinessException("公告数量超过100条，请缩小时间范围");
        }
        if (list.isEmpty()) {
            return "该时间范围内没有已发布的公告。";
        }
        String text = list.stream()
                .map(a -> "【" + a.getCategoryName() + "】" + a.getTitle() + "- 内容:" + a.getContent() + " - " + a.getNickname() + " (" + a.getCreateTime() + ")")
                .collect(Collectors.joining("\n"));
        return aiService.summarize(text);
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SysUser) {
            return ((SysUser) principal).getId();
        }
        throw new BusinessException("无法获取当前用户");
    }
}
