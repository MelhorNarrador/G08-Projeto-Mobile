package com.backend.lane.service.impl;

import com.backend.lane.repository.EventParticipantsRepository;
import com.backend.lane.service.EParticipantsService;
import org.springframework.stereotype.Service;
import com.backend.lane.domain.EventParticipants;
import java.time.LocalDateTime;


@Service
public class IEventParticipants implements EParticipantsService {

    private final EventParticipantsRepository eventParticipantsRepository;

    public IEventParticipants(EventParticipantsRepository eventParticipantsRepository) {
        this.eventParticipantsRepository = eventParticipantsRepository;
    }

    @Override
    public void addParticipantToEvent(Integer event_id, Integer user_id) {
        long exists = eventParticipantsRepository.countByEventAndUser(event_id, user_id);
        if (exists > 0) {
            throw new IllegalStateException("Utilizador já está inscrito neste evento.");
        }
        EventParticipants participant = new EventParticipants();
        participant.setEvent_id(event_id);
        participant.setUser_id(user_id);
        participant.setJoined_at(LocalDateTime.now());

        eventParticipantsRepository.save(participant);
    }

    @Override
    public void deleteParticipantFromEvent(Integer event_id, Integer participant_id) {
        eventParticipantsRepository.deleteByEventAndUser(event_id, participant_id);
    }

    @Override
    public long countParticipantsByEventId(Integer event_id) {
        return eventParticipantsRepository.countByEventId(event_id);
    }
}
