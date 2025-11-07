package com.backend.lane.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Followers")
public class Followers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer follow_id;
    @Column(nullable = false)
    private Integer follower_id;
    @Column(nullable = false)
    private Integer following_id;
    @Column(name = "followed_at",nullable = false, updatable = false ,columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime followed_at;
    //follower_id and following_id should be unique together
    @Column(unique = true, nullable = false)
    private String unique_pair = follower_id + "_" + following_id;
}
