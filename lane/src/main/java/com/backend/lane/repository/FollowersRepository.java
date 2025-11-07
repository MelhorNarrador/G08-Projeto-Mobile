package com.backend.lane.repository;

import com.backend.lane.domain.Followers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowersRepository extends JpaRepository<Followers, Long> {
}
