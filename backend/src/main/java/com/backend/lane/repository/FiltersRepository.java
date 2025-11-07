package com.backend.lane.repository;

import com.backend.lane.domain.Filters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FiltersRepository extends JpaRepository<Filters, Long> {
}
