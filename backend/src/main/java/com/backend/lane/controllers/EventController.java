package com.backend.lane.controllers;

import com.backend.lane.domain.Event;
import com.backend.lane.service.EParticipantsService;
import com.backend.lane.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/{id}/participants/join")
    public ResponseEntity<String> joinEvent(
            @PathVariable("id") Integer eventId,
            @RequestParam("userId") Integer userId
    ) {
        try {
            eParticipantsService.addParticipantToEvent(eventId, userId);
            return ResponseEntity.ok("Participação registada com sucesso.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registar participação.");
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Integer id,
            @RequestBody Event event
    ) {
        try {
            Event updated = eventService.updateEvent(id, event);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{id}/participants/leave")
    public ResponseEntity<String> leaveEvent(
            @PathVariable("id") Integer eventId,
            @RequestParam("userId") Integer userId
    ) {
        try {
            eParticipantsService.deleteParticipantFromEvent(eventId, userId);
            return ResponseEntity.ok("Participação removida com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao remover participação.");
        }
    }

}