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

@Table(name = "invitations", uniqueConstraints = {
    @UniqueConstraint(name = "unique_invitation", columnNames = {"event_id", "sender_id", "receiver_id"})
})
public class Invitations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitations_id")
    private Integer invitations_id; 

    @Column(name = "event_id", nullable = false)
    private Integer event_id;

    @Column(name = "sender_id", nullable = false)
    private Integer sender_id;

    @Column(name = "receiver_id", nullable = false)
    private Integer receiver_id;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "pending";

    @Column(name = "sent_at", updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime sent_at;

}