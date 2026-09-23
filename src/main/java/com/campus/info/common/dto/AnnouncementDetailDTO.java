package com.campus.info.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementDetailDTO {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String username;
    private String nickname;
    private Long categoryId;
    private String categoryName;
    private Integer status;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
