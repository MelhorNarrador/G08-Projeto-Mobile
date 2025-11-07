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
@Table(name = "Invitations")
public class Invitations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private String invitations_id;
    @Column(nullable = false)
    private Integer event_id;
    @Column(nullable = false)
    private Integer sender_id;
    @Column(nullable = false)
    private Integer receiver_id;
    @Column(name = "status",nullable = false, length = 20)
    private String status = "pending";
    @Column(updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime send_at;
    //event_id, sender_id, receiver_id should be unique together
    @Column(unique = true, nullable = false)
    private String unique_triplet = event_id + "_" + sender_id + "_" + receiver_id;
}
