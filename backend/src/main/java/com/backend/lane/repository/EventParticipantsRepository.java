package com.backend.lane.repository;

import com.backend.lane.domain.EventParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventParticipantsRepository extends JpaRepository<EventParticipants, Integer> {
}
