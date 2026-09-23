package com.campus.info.common.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AnnouncementSaveVO {
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotBlank(message = "内容不能为空")
    private String content;
    private Long userId;
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    private Integer status;
    private String publishType;
    private String publishTime;
}
