package com.backend.lane.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    @JsonProperty("token")
    private String token;

    @JsonProperty("userId")
    private Integer userId;
}