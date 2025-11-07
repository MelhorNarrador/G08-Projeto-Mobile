package com.backend.lane.service;

import com.backend.lane.domain.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User creatUser (User user);
    void deleUser (Long id);
}
