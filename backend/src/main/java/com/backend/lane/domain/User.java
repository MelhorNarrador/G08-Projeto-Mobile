package com.backend.lane.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long account_id;
    @NotBlank
    @NotNull
    @Column(nullable = false, length = 100)
    private String account_name;
    @NotBlank
    @NotNull
    @Column(nullable = false, length = 50)
    private String account_username;
    @NotBlank
    @NotNull
    @Column(nullable = false, length = 120)
    @Email
    private String account_email;
    @NotBlank
    @NotNull
    @Column(nullable = false, length = 255)
    private String account_password_hash;
    @Column(columnDefinition = "text")
    private String account_bio;
    @Column(columnDefinition = "text")
    private String account_photo_url;
    @Column(name = "account_verified", nullable = false)
    private boolean account_verified =
            false;
    private Date account_dob;
    @Column(length = 30)
    private String gender;

}
