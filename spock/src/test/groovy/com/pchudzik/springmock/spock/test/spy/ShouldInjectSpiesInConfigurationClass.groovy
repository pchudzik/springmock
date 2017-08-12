package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.annotation.PostConstruct

@ContextConfiguration
class ShouldInjectSpiesInConfigurationClass extends Specification {
	@AutowiredSpy
	Service service

	def "should setup context"() {
		when:
		noop()

		then:
		1 * service.hello()
	}

	def noop() {}

	@Configuration
	static class Config {
		@AutowiredSpy
		Service service

		@PostConstruct
		public void bean() {
			service.hello()
		}
	}

	private static class Service {
		public static final String DEFAULT_RESPONSE = "spy"

		String hello() {
			return DEFAULT_RESPONSE
		}
	}
}
