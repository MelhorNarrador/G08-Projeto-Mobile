package com.backend.lane.service.impl;

import com.backend.lane.domain.Event;
import com.backend.lane.service.EventService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IEventService implements EventService {
    @Override
    public List<Event> getAllEvents() {

        return List.of();
    }

    @Override
    public Event createEvent(Event event) {

        return null;
    }

    @Override
    public void deleteEvent(Integer id) {

    }
}
