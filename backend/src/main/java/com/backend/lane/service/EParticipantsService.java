package com.backend.lane.service;

public interface EParticipantsService {

    void addParticipantToEvent(Integer event_id, Integer participant_id);
    void deleteParticipantFromEvent(Integer event_id, Integer participant_id);
    long countParticipantsByEventId(Integer event_id);
}
