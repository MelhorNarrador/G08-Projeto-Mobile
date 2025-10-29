package com.ghost.security;

import com.ghost.security.domain.Role;
import com.ghost.security.domain.User;
import com.ghost.security.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {

        SpringApplication.run(SecurityApplication.class, args);
	}

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
          userService.saveRole(new Role(null, "ROLE_USER"));
          userService.saveRole(new Role(null, "ROLE_MANAGER"));
          userService.saveRole(new Role(null, "ROLE_ADMIN"));
          userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

          userService.saveUser(new User(null, "Filomeno Sabino", "sabino", "1234", new ArrayList<>()));
          userService.saveUser(new User(null, "Savio Casimira", "casimira", "1234", new ArrayList<>()));
          userService.saveUser(new User(null, "Gianni Lopes", "gianni", "1234", new ArrayList<>()));
          userService.saveUser(new User(null, "Pedro Afonso", "melhor narrador", "1234", new ArrayList<>()));
          userService.saveUser(new User(null, "Francisco Abecassis", "francisco", "1234", new ArrayList<>()));

          userService.addRoleTouser("sabino", "ROLE_SUPER_ADMIN");
          userService.addRoleTouser("casimira", "ROLE_USER");
          userService.addRoleTouser("gianni", "ROLE_MANAGER");
          userService.addRoleTouser("melhor narrador", "ROLE_ADMIN");
          userService.addRoleTouser("francisco", "ROLE_USER");
        };
    }

}
