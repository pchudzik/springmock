package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class ShouldInjectmockByAliasTest extends Specification {
	@AutowiredMock(alias = "alias")
	AnyService mock

	@AutowiredMock
	AnyService otherMock

	@Autowired
	Service service

	def "should inject mock by alias"() {
		expect:
		mock.is(service.dependency)
		new MockUtil().isMock(service.dependency)
	}

	@Configuration
	static class Config {
		@Bean
		Service service() {
			new Service()
		}
	}

	static class Service {
		@Autowired
		@Qualifier("alias")
		AnyService dependency
	}
}
