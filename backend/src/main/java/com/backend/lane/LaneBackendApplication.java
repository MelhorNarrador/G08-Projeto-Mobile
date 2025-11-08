package com.backend.lane;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class LaneBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(LaneBackendApplication.class, args);
	}

}
