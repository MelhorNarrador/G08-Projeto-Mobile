package com.backend.lane.service.impl;

import com.backend.lane.domain.Followers;
import com.backend.lane.service.FollowersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IFollowersService implements FollowersService{

    @Override
    public List<Followers> getAllFollowers() {
        return List.of();
    }

    @Override
    public String addFollowers(Integer follow_id, Integer follower_id) {

        return null;
    }

    @Override
    public String deleteFollowers(Integer follow_id, Integer follower_id){
        return null;
    }

}
