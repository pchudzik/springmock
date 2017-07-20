package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class MockWithDifferentNameShouldBeInjectableWhenOnlyCandidateTest extends Specification {
	@AutowiredMock(name = "testServiceMock")
	AnyService mockService;

	@Autowired
	ServiceWithDependencies serviceWithDependencies;

	def "should inject mock when its only acceptable candidate and name does not match"() {
		expect:
		mockService.is(serviceWithDependencies.anyService)

		and:
		new MockUtil().isMock(serviceWithDependencies.anyService)
	}

	@Configuration
	static class Config {
		@Bean
		ServiceWithDependencies service(@Autowired AnyService injectedMockService) {
			return new ServiceWithDependencies(injectedMockService);
		}
	}

	private static class ServiceWithDependencies {
		private final AnyService anyService;

		private ServiceWithDependencies(AnyService anyService) {
			this.anyService = anyService;
		}
	}
}
