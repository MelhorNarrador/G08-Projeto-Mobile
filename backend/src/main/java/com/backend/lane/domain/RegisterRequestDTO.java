package com.backend.lane.domain;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequestDTO {
 
    private String account_name;
    private String account_username;
    private String account_email;
    private String password; 
    private LocalDate account_dob;
    private Gender account_gender;
}