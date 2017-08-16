package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.annotation.PostConstruct

@ContextConfiguration
class ShouldReuseSpiesInjectedInConfigurationClass extends Specification {
	@AutowiredSpy
	Service service

	@Autowired
	Config config

	def "should reuse spy in configuration class and test context"() {
		expect:
		service.is(config.service)
		new MockUtil().isMock(service)
	}

	@Configuration
	static class Config {
		@AutowiredSpy
		Service service

		@PostConstruct
		void postConstruct() {
			assert Service.DEFAULT_RESPONSE == service.hello()
		}
	}

	private static class Service {
		public static final String DEFAULT_RESPONSE = "spy"

		String hello() {
			return DEFAULT_RESPONSE
		}
	}
}
