package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.ContextHierarchy
import spock.lang.Specification

@ContextHierarchy([
		@ContextConfiguration(classes = ShouldInjectAndReplaceBeansWithSpiesInContextHierarchyTest.ChildConfig),
		@ContextConfiguration(classes = ShouldInjectAndReplaceBeansWithSpiesInContextHierarchyTest.ParentConfig)
])
class ShouldInjectAndReplaceBeansWithSpiesInContextHierarchyTest extends Specification {
	@AutowiredSpy
	ChildService childService

	@Autowired
	ParentService parentService

	def "should replace beans with spies in hierarchical configuration"() {
		when:
		final result = parentService.hello()

		then:
		1 * childService.hello()

		and:
		result == ParentService.PARENT_PREFIX_RESPONSE + ChildService.CHILD_RESPONSE
	}

	@Configuration
	static class ChildConfig {
		@Bean
		ChildService childService() {
			new ChildService()
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
		static final CHILD_RESPONSE = "child"

		String hello() {
			CHILD_RESPONSE
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
