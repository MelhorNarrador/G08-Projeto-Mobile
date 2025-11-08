package com.backend.lane.service;

import com.backend.lane.domain.User;

import java.util.List;

import com.backend.lane.domain.RegisterRequestDTO; 

public interface UserService {
    List<User> getAllUsers();
    
    User creatUser (RegisterRequestDTO registerRequest);
    
    void deleUser (Integer id);
}
