package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class MultipleNamedMocksShouldBeDistinguishableTest extends Specification {
	@AutowiredMock(name = "otherService")
	AnyService otherService;

	@AutowiredMock(name = "myService")
	AnyService myService;

	@Autowired
	Service service;

	def "should inject named mock instance to all object instances"() {
		expect:
		myService.is(service.anyService)
	}

	@Configuration
	static class Config {
		@Bean
		Service service(@Autowired @Qualifier("myService") AnyService anyService) {
			return new Service(anyService);
		}
	}

	static class Service {
		private final AnyService anyService;

		Service(AnyService anyService) {
			this.anyService = anyService;
		}
	}
}
