package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class ShouldInjectSpiesInConfigurationClass {
	@Test
	public void should_setup_context() {
		//pass
	}

	@Configuration
	static class Config {
		@AutowiredSpy
		Service service;

		@PostConstruct
		public void initialize() {
			assertEquals(
					Service.DEFAULT_RESPONSE,
					service.hello());

			Mockito
					.verify(service)
					.hello();
		}
	}

	private static class Service {
		public static final String DEFAULT_RESPONSE = "spy";

		String hello() {
			return DEFAULT_RESPONSE;
		}
	}
}
