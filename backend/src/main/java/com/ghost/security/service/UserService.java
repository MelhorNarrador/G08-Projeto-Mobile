package com.ghost.security.service;

import com.ghost.security.domain.Role;
import com.ghost.security.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleTouser(String username, String roleName);
    User getUser(String username);
    List<User>getUsers();
}
