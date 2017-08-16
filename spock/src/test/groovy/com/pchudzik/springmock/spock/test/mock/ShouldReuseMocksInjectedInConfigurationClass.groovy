package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

import javax.annotation.PostConstruct

@ContextConfiguration
class ShouldReuseMocksInjectedInConfigurationClass {
	@Autowired
	Config config;

	@AutowiredMock
	AnyService service;

	def "should reuse mocks in configuration and test class"() {
		expect:
		new MockUtil().isMock(service)
		service.is(config.service)
	}

	@Configuration
	static class Config {
		@AutowiredMock
		AnyService service;

		@PostConstruct
		void postConstruct() {
			assert new MockUtil().isMock(service)
		}
	}
}
