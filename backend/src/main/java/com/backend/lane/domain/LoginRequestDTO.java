package com.backend.lane.domain;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String account_email;
    private String password;
}