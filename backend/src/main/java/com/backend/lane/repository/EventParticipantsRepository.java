package com.backend.lane.repository;

import com.backend.lane.domain.EventParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventParticipantsRepository extends JpaRepository<EventParticipants, Integer> {

    @Query("SELECT COUNT(ep) FROM EventParticipants ep WHERE ep.event_id = :eventId")
    long countByEventId(@Param("eventId") Integer eventId);

    @Query("SELECT COUNT(ep) FROM EventParticipants ep WHERE ep.event_id = :eventId AND ep.user_id = :userId")
    long countByEventAndUser(@Param("eventId") Integer eventId, @Param("userId") Integer userId);
}
