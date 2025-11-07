package com.backend.lane.service.impl;

import com.backend.lane.domain.User;
import com.backend.lane.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IUserService implements UserService {


    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public User creatUser(User user) {
        return null;
    }

    @Override
    public void deleUser(Long id) {

    }
}
