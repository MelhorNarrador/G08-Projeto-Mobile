package com.backend.lane.service.impl;

import com.backend.lane.domain.Gender;
import com.backend.lane.domain.RegisterRequestDTO;
import com.backend.lane.domain.User;
import com.backend.lane.repository.UserRepository;
import com.backend.lane.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IUserService implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User creatUser(RegisterRequestDTO registerRequest) {
        
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User newUser = User.builder()
                .account_name(registerRequest.getAccount_name())
                .account_username(registerRequest.getAccount_username())
                .account_email(registerRequest.getAccount_email())
                .account_password_hash(hashedPassword)
                .account_dob(registerRequest.getAccount_dob())
                .account_gender(registerRequest.getAccount_gender())
                .account_verified(false)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    public void deleUser(Integer id) {
        userRepository.deleteById(id);
    }
}
