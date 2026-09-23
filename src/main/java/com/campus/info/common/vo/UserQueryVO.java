package com.campus.info.common.vo;

import lombok.Data;

@Data
public class UserQueryVO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
}
