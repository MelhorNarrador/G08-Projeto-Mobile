package com.backend.lane.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "filters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Filters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filters_id")
    @JsonProperty("filters_id")
    private Integer filters_id;

    @Column(name = "filters_name", nullable = false, length = 50, unique = true)
    @JsonProperty("filters_name")
    private String filters_name;
}