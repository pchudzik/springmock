package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import com.pchudzik.springmock.spock.test.spy.infrastructure.InteractionRecorderConfig
import com.pchudzik.springmock.spock.test.spy.infrastructure.ServiceInteractionRecorder
import com.pchudzik.springmock.spock.test.spy.infrastructure.Service
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class ShouldInjectSpyByNameTest extends Specification {
	@AutowiredSpy(name = Config.SPY_NAME)
	@Qualifier(Config.SPY_NAME)
	Service spyService

	@Autowired
	@Qualifier(Config.NOT_SPY)
	Service notSpy

	def "should create and inject spy by name"() {
		given:
		final methodArgument = "any call argument"
		final methodResponse = "any method response"
		final mockUtil = new MockUtil()

		when:
		final spyResult = spyService.hello(methodArgument)
		final serviceResult = notSpy.hello(methodArgument)

		then:
		mockUtil.isMock(spyService)
		!mockUtil.isMock(notSpy)

		and:
		1 * spyService.hello(methodArgument) >> methodResponse

		and:
		spyResult == methodResponse
		spyService.serviceInteractionRecorder.interactions == []

		and:
		serviceResult == Service.DEFAULT_RESPONSE
		notSpy.serviceInteractionRecorder.interactions == [methodArgument]
	}

	@Configuration
	@Import(InteractionRecorderConfig)
	static class Config {
		public static final String NOT_SPY = 'service'
		public static final String SPY_NAME = 'serviceToSpyOn'

		@Autowired
		ServiceInteractionRecorder serviceInteractionRecorder

		@Bean('service')
		Service service() {
			new Service(new ServiceInteractionRecorder())
		}

		@Bean('serviceToSpyOn')
		Service spy() {
			new Service(serviceInteractionRecorder)
		}
	}
}
