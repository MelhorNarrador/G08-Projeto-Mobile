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

@Table(name = "followers", uniqueConstraints = {
    @UniqueConstraint(name = "unique_follow", columnNames = {"follower_id", "following_id"})
})
public class Followers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Integer follow_id;

    @Column(name = "follower_id", nullable = false)
    private Integer follower_id;

    @Column(name = "following_id", nullable = false)
    private Integer following_id;

    @Column(name = "followed_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime followed_at;


}