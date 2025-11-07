package com.backend.lane.service;

public interface EParticipantsService {

    void addParticipantToEvent(Long event_id, Long participant_id);
    void deleteParticipantFromEvent(Long event_id, Long participant_id);

}
