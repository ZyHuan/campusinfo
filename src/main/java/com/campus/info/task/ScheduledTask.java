package com.campus.info.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.info.entity.BizAnnouncement;
import com.campus.info.service.BizAnnouncementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    @Resource
    private BizAnnouncementService bizAnnouncementService;

    @Scheduled(cron = "0 * * * * ?")
    @CacheEvict(value = {"announcement:list", "announcement:detail"}, allEntries = true)
    public void publishScheduledAnnouncements() {
        List<BizAnnouncement> pendingList = bizAnnouncementService.list(
                new LambdaQueryWrapper<BizAnnouncement>()
                        .eq(BizAnnouncement::getStatus, 2)
                        .le(BizAnnouncement::getPublishTime, LocalDateTime.now())
        );
        for (BizAnnouncement a : pendingList) {
            a.setStatus(1);
            bizAnnouncementService.updateById(a);
            log.info("定时发布公告: id={}, title={}", a.getId(), a.getTitle());
        }
    }
}
