package com.pchudzik.springmock.samples.mockito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class MockitoSamplesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockitoSamplesApplication.class, args);
	}
}
