package com.backend.lane.service;

import com.backend.lane.domain.Followers;

import java.util.List;

public interface FollowersService {

    List<Followers> getAllFollowers();
    String addFollowers(Integer follow_id, Integer follower_id );
    String deleteFollowers(Integer follow_id, Integer follower_id);
}
