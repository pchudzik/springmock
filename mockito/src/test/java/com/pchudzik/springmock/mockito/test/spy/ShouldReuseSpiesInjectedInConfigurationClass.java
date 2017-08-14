package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
public class ShouldReuseSpiesInjectedInConfigurationClass {
	@AutowiredSpy
	Service service;

	@Autowired
	Config config;

	@Test
	public void should_reuse_spy_in_configuration_class_and_test_context() {
		assertSame(
				config.service,
				service);

		Mockito
				.verify(service)
				.hello();
	}

	@Configuration
	static class Config {
		@AutowiredSpy
		Service service;

		@PostConstruct
		public void bean() {
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
