package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class InjectMockForAopProxiedClasses {
	@AutowiredMock
	Service service;

	@Test(expected = ExpectedException.class)
	public void should_inject_async_service_as_a_synchronous_mock() {
		//given
		Mockito
				.doThrow(new ExpectedException())
				.when(service).hello();

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
		void hello() {
		}
	}

	private static class ExpectedException extends RuntimeException {
	}
}
