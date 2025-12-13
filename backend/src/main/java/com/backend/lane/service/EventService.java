package com.backend.lane.service;

import com.backend.lane.domain.Event;

import java.util.List;

public interface EventService {

    List<Event> getAllEvents();
    Event createEvent (Event event);
    void deleteEvent (Integer id);
    Event updateEvent(Integer id, Event event);
    Event getEventById(Integer id);
}
