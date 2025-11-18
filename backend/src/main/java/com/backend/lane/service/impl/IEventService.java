package com.backend.lane.service.impl;

import com.backend.lane.domain.Event;
import com.backend.lane.repository.EventRepository;
import com.backend.lane.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IEventService implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public IEventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Integer id) {
        eventRepository.deleteById(id);
    }
}