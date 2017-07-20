package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockingDetails;

@RunWith(SpringRunner.class)
public class MockWithDifferentNameShouldBeInjectableWhenOnlyCandidateTest {
	@AutowiredMock(name = "testServiceMock")
	AnyService mockService;

	@Autowired
	ServiceWithDependencies serviceWithDependencies;

	@Test
	public void should_inject_mock_when_its_only_acceptable_canditate_and_name_doesnt_match() {
		assertSame(mockService, serviceWithDependencies.getAnyService());
		assertTrue(mockingDetails(serviceWithDependencies.getAnyService()).isMock());
	}

	@Configuration
	static class Config {
		@Bean
		public ServiceWithDependencies service(@Autowired AnyService injectedMockService) {
			return new ServiceWithDependencies(injectedMockService);
		}
	}

	private static class ServiceWithDependencies {
		private final AnyService anyService;

		private ServiceWithDependencies(AnyService anyService) {
			this.anyService = anyService;
		}

		public AnyService getAnyService() {
			return anyService;
		}
	}
}
