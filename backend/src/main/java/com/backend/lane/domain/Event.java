package com.backend.lane.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    @JsonProperty("event_id")
    private Integer event_id;

    @Column(name = "event_title", nullable = false, length = 150)
    @JsonProperty("event_title")
    private String event_title;

    @Column(name = "event_description", columnDefinition = "text")
    @JsonProperty("event_description")
    private String event_description;

    @Column(name = "event_visibility", nullable = false, length = 20)
    @JsonProperty("event_visibility")
    private String event_visibility;

    @Column(name = "event_category_id")
    @JsonProperty("event_category_id")
    private Integer event_category_id;

    @Column(name = "event_creator_id")
    @JsonProperty("event_creator_id")
    private Integer event_creator_id;

    @Column(name = "location", length = 255)
    @JsonProperty("location")
    private String location;

    @Column(name = "event_latitude", precision = 9, scale = 6)
    @JsonProperty("event_latitude")
    private BigDecimal event_latitude;

    @Column(name = "event_longitude", precision = 9, scale = 6)
    @JsonProperty("event_longitude")
    private BigDecimal event_longitude;

    @Column(name = "event_date", nullable = false)
    @JsonProperty("event_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime event_date;

    @Column(name = "event_price", precision = 10, scale = 2)
    @JsonProperty("event_price")
    private BigDecimal event_price = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "max_participants", nullable = false)
    @JsonProperty("max_participants")
    private Integer max_participants;
}