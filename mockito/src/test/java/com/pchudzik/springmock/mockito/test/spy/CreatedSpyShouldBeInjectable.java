package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.mockito.test.spy.infrastructure.Service;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.SpyConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CreatedSpyShouldBeInjectable {
	@AutowiredSpy
	Service service;

	@Autowired
	OtherService otherService;

	@Test
	public void created_spy_should_be_injectable() {
		//given
		final String spyResponse = "any spy response";
		Mockito
				.when(service.hello(OtherService.CALL_ARGUMENT))
				.thenReturn(spyResponse);

		//when
		final String result = otherService.execute();

		//then
		Assert.assertEquals(spyResponse, result);
		Mockito.verify(service).hello(OtherService.CALL_ARGUMENT);
	}

	@Configuration
	@Import(SpyConfig.class)
	static class Config {
		@Bean
		OtherService otherService(@Autowired Service service) {
			return new OtherService(service);
		}
	}

	static class OtherService {
		public static final String CALL_ARGUMENT = "call argument";
		private final Service service;

		OtherService(Service service) {
			this.service = service;
		}

		public String execute() {
			return service.hello(CALL_ARGUMENT);
		}
	}
}
