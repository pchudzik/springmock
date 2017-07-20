package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import com.pchudzik.springmock.spock.test.spy.infrastructure.InteractionRecorderConfig
import com.pchudzik.springmock.spock.test.spy.infrastructure.ServiceInteractionRecorder
import com.pchudzik.springmock.spock.test.spy.infrastructure.Service
import org.spockframework.mock.MockUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class ShouldInjectSpyByBeanClassTest extends Specification {
	@AutowiredSpy(name = "name match is not necessary for mock injection")
	Service service

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

	@Configuration
	@Import(InteractionRecorderConfig)
	static class Config {
		private final ServiceInteractionRecorder interactionRecorder

		Config(ServiceInteractionRecorder interactionRecorder) {
			this.interactionRecorder = interactionRecorder
		}

		@Bean("customServiceName")
		Service service() {
			new Service(interactionRecorder)
		}
	}
}
