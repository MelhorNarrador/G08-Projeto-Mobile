package com.backend.lane.controllers;

import com.backend.lane.domain.Event;
import com.backend.lane.service.EventService;
import org.springframework.web.bind.annotation.*;
import com.backend.lane.service.EParticipantsService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EParticipantsService eParticipantsService;

    public EventController(EventService eventService, EParticipantsService eParticipantsService){
        this.eventService = eventService;
        this.eParticipantsService = eParticipantsService;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents(){
        return eventService.getAllEvents();
    }

    @PostMapping("/create/events")
    public Event createEvent (@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEvent(@PathVariable Integer id){
        eventService.deleteEvent(id);
    }

    @GetMapping("/{id}/participants/count")
    public long getParticipantsCount(@PathVariable("id") Integer eventId) {
        return eParticipantsService.countParticipantsByEventId(eventId);
    }
}