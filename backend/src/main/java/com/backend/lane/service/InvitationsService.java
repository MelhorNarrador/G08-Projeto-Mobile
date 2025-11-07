package com.backend.lane.service;

import com.backend.lane.domain.Invitations;

import java.util.List;

public interface InvitationsService {

    List<Invitations> getAllInvitations();
    Invitations createInvitations(Invitations invitations);
    void deleteInvitations(Long id);
}
