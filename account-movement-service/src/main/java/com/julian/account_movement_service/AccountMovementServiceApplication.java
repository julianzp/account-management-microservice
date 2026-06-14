package com.julian.account_movement_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AccountMovementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountMovementServiceApplication.class, args);
	}

}
