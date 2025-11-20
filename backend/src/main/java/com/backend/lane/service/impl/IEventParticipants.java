package com.backend.lane.service.impl;

import com.backend.lane.repository.EventParticipantsRepository;
import com.backend.lane.service.EParticipantsService;
import org.springframework.stereotype.Service;

@Service
public class IEventParticipants implements EParticipantsService {

    private final EventParticipantsRepository eventParticipantsRepository;

    public IEventParticipants(EventParticipantsRepository eventParticipantsRepository) {
        this.eventParticipantsRepository = eventParticipantsRepository;
    }

    @Override
    public void addParticipantToEvent(Integer event_id, Integer participant_id) {
    }

    @Override
    public void deleteParticipantFromEvent(Integer event_id, Integer participant_id) {
    }

    @Override
    public long countParticipantsByEventId(Integer event_id) {
        return eventParticipantsRepository.countByEventId(event_id);
    }
}
