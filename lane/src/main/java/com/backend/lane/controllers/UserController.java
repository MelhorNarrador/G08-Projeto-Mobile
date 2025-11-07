package com.backend.lane.controllers;

import com.backend.lane.domain.User;
import com.backend.lane.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping("/users")
    public User createUser (@RequestBody User user) {
        return userService.creatUser(user);
    }

    @DeleteMapping("/delete/id")
    public void deleteUser(@PathVariable Long id) {
        userService.deleUser(id);
    }
}
