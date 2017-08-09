package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import com.pchudzik.springmock.spock.configuration.SpockDouble
import com.pchudzik.springmock.spock.test.spy.infrastructure.Service
import com.pchudzik.springmock.spock.test.spy.infrastructure.ServiceInteractionRecorder
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class SpyShouldBeCreatedWithoutExistingObjectInstanceTest extends Specification {
	public static ServiceInteractionRecorder interactionRecorder = new ServiceInteractionRecorder()

	@AutowiredSpy
	@SpockDouble(constructorArguments = InteractionRecorderProvider)
	Service service

	@AutowiredSpy
	SimpleService simpleService

	def "should inject spy when no backing object present"() {
		when:
		final response = simpleService.hello()

		then:

		response == SimpleService.DEFAULT_RESPONSE

		1 * simpleService.hello()
	}

	def "should create spy with provided constructor arguments"() {
		when:
		final result = service.hello("arg")

		then:
		result == Service.DEFAULT_RESPONSE
		1* service.hello("arg")

		and:
		interactionRecorder.interactions == ["arg"]
	}

	static class SimpleService {
		public static final String DEFAULT_RESPONSE = "hello"

		String hello() {
			return DEFAULT_RESPONSE
		}
	}

	static class InteractionRecorderProvider implements SpockDouble.ConstructorArgumentsProvider {
		@Override
		List<Object> getConstructorArguments() {
			return [interactionRecorder]
		}
	}
}
