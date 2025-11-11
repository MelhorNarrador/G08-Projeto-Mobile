package com.backend.lane.controllers;

import com.backend.lane.domain.User;
import com.backend.lane.service.UserService;
import org.springframework.web.bind.annotation.*;
import com.backend.lane.domain.RegisterRequestDTO;
import java.util.List;
import com.backend.lane.domain.LoginRequestDTO;
import com.backend.lane.domain.LoginResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody RegisterRequestDTO registerRequest) {
        return userService.creatUser(registerRequest);
    }

    @DeleteMapping("/delete/id")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleUser(id);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = userService.loginUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException | SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
