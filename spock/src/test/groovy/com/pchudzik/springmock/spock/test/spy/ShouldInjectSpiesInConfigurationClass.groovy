package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.spockframework.mock.MockUtil
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.annotation.PostConstruct

@ContextConfiguration
class ShouldInjectSpiesInConfigurationClass extends Specification {
	def "should setup context"() {
		expect:
		true
	}

	def noop() {}

	@Configuration
	static class Config {
		@AutowiredSpy
		Service service

		@PostConstruct
		void postConstruct() {
			assert service != null
			assert new MockUtil().isMock(service)
		}
	}

	private static class Service {
		public static final String DEFAULT_RESPONSE = "spy"

		String hello() {
			return DEFAULT_RESPONSE
		}
	}
}
