package com.pchudzik.springmock.samples.spock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SpockSamplesApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpockSamplesApplication.class, args);
	}
}
