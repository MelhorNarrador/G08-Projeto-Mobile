package com.backend.lane.service.impl;

import com.backend.lane.domain.Invitations;
import com.backend.lane.service.InvitationsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IInvitationsService implements InvitationsService {
    @Override
    public List<Invitations> getAllInvitations() {
        return List.of();
    }

    @Override
    public Invitations createInvitations(Invitations invitations) {
        return null;
    }

    @Override
    public void deleteInvitations(Integer id) {

    }
}
