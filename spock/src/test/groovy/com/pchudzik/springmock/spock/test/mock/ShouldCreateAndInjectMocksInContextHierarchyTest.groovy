package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.ContextHierarchy
import spock.lang.Specification

@ContextHierarchy([
		@ContextConfiguration(classes = ShouldCreateAndInjectMocksInContextHierarchyTest.ChildConfig),
		@ContextConfiguration(classes = ShouldCreateAndInjectMocksInContextHierarchyTest.ParentConfig)
])
class ShouldCreateAndInjectMocksInContextHierarchyTest extends Specification {
	@AutowiredMock
	ChildService childService

	@Autowired
	ParentService parentService

	def "should create mocks in hierarchical configuration"() {
		given:
		final mockResponse = "mocked child"
		childService.hello() >> mockResponse

		when:
		final result = parentService.hello()

		then:
		result == ParentService.PARENT_PREFIX_RESPONSE + mockResponse
	}

	@Configuration
	static class ChildConfig {
		@Bean
		AnyService anyService() {
			{ -> "any service" } as AnyService
		}
	}

	@Configuration
	static class ParentConfig {
		@Bean
		ParentService parentService() {
			new ParentService()
		}
	}

	static class ChildService {
		String hello() {
			"child"
		}
	}

	static class ParentService {
		static final PARENT_PREFIX_RESPONSE = "parent says hello "

		@Autowired
		ChildService childService

		String hello() {
			PARENT_PREFIX_RESPONSE + childService.hello()
		}
	}
}
