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
@Table(name = "EventParticipants")
public class EventParticipants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer participant_id;
    @Column(nullable = false)
    private Integer event_id;
    @Column(nullable = false)
    private Integer user_id;
    //event_id and user_id should be unique together
    @Column(name = "joined_at",nullable = false, updatable = false ,columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime joined_at;
}
