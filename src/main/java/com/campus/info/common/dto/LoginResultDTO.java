package com.campus.info.common.dto;

import lombok.Data;

@Data
public class LoginResultDTO {
    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String role;
    private String avatar;
}
