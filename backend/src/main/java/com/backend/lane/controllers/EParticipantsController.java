package com.backend.lane.controllers;

import com.backend.lane.service.EParticipantsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eparticipants")
public class EParticipantsController {
    EParticipantsService eParticipantsService;

    public EParticipantsController(EParticipantsService eParticipantsService) {
        this.eParticipantsService = eParticipantsService;
    }

    @PostMapping("/add")
    public String addParticipant() {
        eParticipantsService.addParticipantToEvent(1, 1);
        return "Participant added";
    }

    @DeleteMapping("/{id}")
    public String deleteParticipant(@PathVariable Integer id) {
        return "Participant deleted";
    }
}
