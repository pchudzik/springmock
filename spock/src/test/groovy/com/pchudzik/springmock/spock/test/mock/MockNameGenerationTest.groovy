package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class MockNameGenerationTest extends Specification {
	@Autowired
	ApplicationContext applicationContext

	@AutowiredMock
	AnyService anyService

	def "should inject not named mock"() {
		expect:
		anyService != null
	}

	def "should generate mock name based on field name"() {
		when:
		final service = applicationContext.getBean("anyService", AnyService.class)

		then:
		anyService.is(service)
	}
}
