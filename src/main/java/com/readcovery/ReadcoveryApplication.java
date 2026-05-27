package com.readcovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ReadcoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadcoveryApplication.class, args);
	}

}
