package com.backend.lane.controllers;

import com.backend.lane.domain.Event;
import com.backend.lane.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    EventService eventService;

    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents(){
        return eventService.getAllEvents();
    }

    @PostMapping("/create/events")
    public Event createEvent (@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @DeleteMapping("delete/id")
    public void deleteEvent(@PathVariable Integer id){
        eventService.deleteEvent(id);
    }

}
