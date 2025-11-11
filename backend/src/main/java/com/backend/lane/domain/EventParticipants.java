package com.backend.lane.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_participants", uniqueConstraints = {
    @UniqueConstraint(name = "unique_participation", columnNames = {"event_id", "user_id"})
})
public class EventParticipants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Integer participant_id;

    @Column(name = "event_id", nullable = false)
    private Integer event_id;

    @Column(name = "user_id", nullable = false)
    private Integer user_id;
    
    @Column(name = "joined_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime joined_at;
}
