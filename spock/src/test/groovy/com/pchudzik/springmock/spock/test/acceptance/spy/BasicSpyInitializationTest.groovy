package com.pchudzik.springmock.spock.test.acceptance.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import com.pchudzik.springmock.spock.configuration.SpockDouble
import org.spockframework.mock.DefaultJavaLangObjectInteractions
import org.spockframework.mock.IDefaultResponse
import org.spockframework.mock.IMockInvocation
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class BasicSpyInitializationTest extends Specification {
	@AutowiredSpy
	Service service

	@AutowiredSpy
	@SpockDouble(defaultResponse = ReturnsHelloSpyResponse)
	Service withDefaultResponse

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
		interactionRecorder.interactions.isEmpty()    //spock doesn't call through if method is overridden
	}

	def "should configure spy default response"() {
		expect:
		withDefaultResponse.hello("any argument") == ReturnsHelloSpyResponse.DEFAULT_RESPONSE
	}

	@Configuration
	static class Config {
		@Bean
		Service withDefaultResponse() {
			return new Service(new ServiceInteractionRecorder())
		}

		@Bean
		ServiceInteractionRecorder interactionRecorder() {
			new ServiceInteractionRecorder()
		}

		@Bean
		Service service() {
			new Service(interactionRecorder())
		}
	}

	private static class ReturnsHelloSpyResponse implements IDefaultResponse {
		public static final String DEFAULT_RESPONSE = "hello spy!!1"

		@Override
		Object respond(IMockInvocation invocation) {
			final interaction = DefaultJavaLangObjectInteractions.INSTANCE.match(invocation);
			if (interaction != null) {
				return interaction.accept(invocation)
			}

			return DEFAULT_RESPONSE
		}
	}

	static class Service {
		public static final String DEFAULT_RESPONSE = "not a spy"
		private final ServiceInteractionRecorder serviceInteractionRecorder

		Service(ServiceInteractionRecorder serviceInteractionRecorder) {
			this.serviceInteractionRecorder = serviceInteractionRecorder
		}

		String hello(String argument) {
			serviceInteractionRecorder.record(argument);
			return DEFAULT_RESPONSE;
		}

		ServiceInteractionRecorder getServiceInteractionRecorder() {
			return serviceInteractionRecorder
		}
	}

	static class ServiceInteractionRecorder {
		private LinkedList<String> interactions = []

		void record(String argument) {
			this.interactions.add(argument)
		}

		List<String> getInteractions() {
			return Collections.unmodifiableList(interactions)
		}

		String getLatestInteraction() {
			return interactions[0]
		}

		void resetLatestInteraction() {
			interactions.removeLast()
		}

		void resetInteractions() {
			interactions = []
		}
	}
}
