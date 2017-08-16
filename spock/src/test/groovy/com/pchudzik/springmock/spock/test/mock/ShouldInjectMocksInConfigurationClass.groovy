package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.spockframework.mock.MockUtil
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.annotation.PostConstruct

@ContextConfiguration
class ShouldInjectMocksInConfigurationClass extends Specification {
	def "should setup context"() {
		expect:
		true
	}

	@Configuration
	static class Config {
		@AutowiredMock
		AnyService service

		@PostConstruct
		void initialize() {
			assert new MockUtil().isMock(service)
		}
	}
}
