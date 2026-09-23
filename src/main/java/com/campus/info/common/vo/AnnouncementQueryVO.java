package com.campus.info.common.vo;

import lombok.Data;

@Data
public class AnnouncementQueryVO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long categoryId;
    private Long userId;
}
