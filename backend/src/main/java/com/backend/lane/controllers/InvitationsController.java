package com.backend.lane.controllers;

import com.backend.lane.domain.Invitations;
import com.backend.lane.service.InvitationsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationsController {
    InvitationsService invitationsService;

    public InvitationsController(InvitationsService invitationsService){
        this.invitationsService = invitationsService;
    }

    @GetMapping("/invitations")
    public List<Invitations> getAllInvitations(){
        return invitationsService.getAllInvitations();
    }

    @PostMapping("/create/invitations")
    public Invitations createInvitations(@RequestBody Invitations invitations){
        return invitationsService.createInvitations(invitations);
    }

    @DeleteMapping("/{id}")
    public void deleteInvitations (@PathVariable Integer id){
        invitationsService.deleteInvitations(id);
    }
}
