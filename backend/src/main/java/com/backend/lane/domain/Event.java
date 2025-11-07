package com.backend.lane.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long event_id;
    @Column(nullable = false, length = 150)
    private String event_title;
    @Column(columnDefinition = "text")
    private String event_description;
    @Column(nullable = false, length = 20)
    private String event_visibility;
    private Integer event_category_id;
    private Integer event_creator_id;
    @Column(nullable = false, length = 255)
    private String location;
    private Double event_latitude;
    private Double event_longitude;
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime event_date;
    @Column(precision = 10, scale = 2)
    private BigDecimal event_price =
            BigDecimal.ZERO;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime created_at =
            LocalDateTime.now();
    @Column(nullable = false)
    private Integer max_participants;

}
