package com.backend.lane.repository;

import com.backend.lane.domain.Invitations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationsRepository extends JpaRepository<Invitations, Integer> {
}
