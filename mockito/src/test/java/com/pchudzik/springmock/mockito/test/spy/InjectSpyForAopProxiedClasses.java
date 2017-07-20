package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class InjectSpyForAopProxiedClasses {
	@AutowiredSpy
	Service service;

	@Test
	public void should_inject_spy() {
		assertTrue(Mockito.mockingDetails(service).isSpy());
	}

	@Test(expected = ExpectedException.class)
	public void should_inject_async_service_as_a_synchronous_mock() {
		//given
		when(service.hello()).thenThrow(new ExpectedException());

		//expect exception
		service.hello();
	}

	@Configuration
	@EnableAsync
	static class Config {
		@Bean
		Service service() {
			return new Service();
		}
	}

	static class Service {
		@Async
		public String hello() {
			return "hello";
		}
	}

	private static class ExpectedException extends RuntimeException {
	}
}
