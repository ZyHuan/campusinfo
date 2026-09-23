package com.campus.info.common.vo;

import lombok.Data;

@Data
public class UserSaveVO {
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
}
