package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import com.pchudzik.springmock.spock.test.spy.infrastructure.ServiceInteractionRecorder
import com.pchudzik.springmock.spock.test.spy.infrastructure.SpyConfig
import com.pchudzik.springmock.spock.test.spy.infrastructure.Service
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class BasicSpyInitializationTest extends Specification {
	@AutowiredSpy
	Service service

	@Autowired
	ServiceInteractionRecorder interactionRecorder

	def setup() {
		service.serviceInteractionRecorder.resetInteractions()
	}

	def "should inject spy"() {
		given:
		final callArgument = "any call argument"

		when:
		final result = service.hello(callArgument)

		then:
		new MockUtil().isMock(service)

		and:
		result == Service.DEFAULT_RESPONSE
		service.serviceInteractionRecorder.interactions == [callArgument]
	}

	def "should replace bean with spy"() {
		given:
		final methodArgument = "spy"
		final methodResponse = "spy response"

		and:
		service.hello(methodArgument) >> methodResponse

		when:
		final result = service.hello(methodArgument)

		then:
		methodResponse == result
		interactionRecorder.interactions.isEmpty()	//spock doesn't call through if method is overridden
	}

	@Configuration
	@Import(SpyConfig)
	static class Config {}
}
