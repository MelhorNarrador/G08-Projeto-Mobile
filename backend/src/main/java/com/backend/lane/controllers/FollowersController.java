package com.backend.lane.controllers;

import com.backend.lane.domain.Followers;
import com.backend.lane.service.FollowersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/followers")
public class FollowersController {
    FollowersService followersService;

    public FollowersController(FollowersService followersService){
        this.followersService = followersService;
    }

    @GetMapping("/all")
    public List<Followers> getAllFollowers (){
        return followersService.getAllFollowers();
    }


    @PostMapping("/add")
    public String addFollowers(){
        return followersService.addFollowers(1,1);
    }

    @DeleteMapping("/{id}")
    public String deleteFollowers(@PathVariable Integer id){
        return "Follower Deleted";
    }


}

