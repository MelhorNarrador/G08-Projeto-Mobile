package com.backend.lane.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; 

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_details")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer account_id;

    @Column(name = "account_name", nullable = false, length = 100)
    private String account_name;

    @Column(name = "account_username", nullable = false, length = 50, unique = true)
    private String account_username;

    @Column(name = "account_email", nullable = false, length = 120, unique = true)
    @Email
    private String account_email;

    @Column(name = "account_password_hash", nullable = false, length = 255)
    private String account_password_hash;

    @Column(name = "account_bio", columnDefinition = "text")
    private String account_bio;

    @Column(name = "account_photo_url", columnDefinition = "text")
    private String account_photo_url;

    @Column(name = "account_verified", nullable = false)
    private boolean account_verified = false;

    @Column(name = "account_dob") 
    private LocalDate account_dob;

    @Enumerated(EnumType.STRING) 
    @Column(name = "account_gender", length = 30)
    private Gender account_gender; 
}
