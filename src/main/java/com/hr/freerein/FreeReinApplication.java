package com.hr.freerein;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // ✅ FIXED
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.hr.freerein.repository")
@EntityScan(basePackages = "com.hr.freerein.entity") // ✅ Clean usage
@ComponentScan(basePackages = "com.hr.freerein")
public class FreeReinApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreeReinApplication.class, args);
	}
}