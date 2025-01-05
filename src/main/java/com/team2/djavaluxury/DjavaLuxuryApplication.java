package com.team2.djavaluxury;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.team2.djavaluxury.repository")
@EntityScan(basePackages = "com.team2.djavaluxury.entity")
public class DjavaLuxuryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DjavaLuxuryApplication.class, args);
	}

}
