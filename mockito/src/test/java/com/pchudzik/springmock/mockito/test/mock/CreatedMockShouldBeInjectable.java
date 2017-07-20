package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
public class CreatedMockShouldBeInjectable {
	@AutowiredMock
	AnyService anyService;

	@Autowired
	OtherService otherService;

	@Test
	public void should_inject_created_mock_when_it_is_required() {
		assertSame(anyService, otherService.getAnyService());
	}

	@Configuration
	static class Config {
		@Bean
		OtherService otherService(@Autowired AnyService anyService) {
			return new OtherService(anyService);
		}
	}

	static class OtherService {
		private final AnyService anyService;

		OtherService(AnyService anyService) {
			this.anyService = anyService;
		}

		public AnyService getAnyService() {
			return anyService;
		}
	}
}
