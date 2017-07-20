package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import com.pchudzik.springmock.spock.test.spy.infrastructure.ServiceInteractionRecorder
import com.pchudzik.springmock.spock.test.spy.infrastructure.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static com.pchudzik.springmock.spock.test.spy.ShouldInjectSpyByAliasTest.OtherService.CALL_ARGUMENT

@ContextConfiguration
class ShouldInjectSpyByAliasTest extends Specification {
@Autowired
	OtherService otherService

	@AutowiredSpy(alias = "alias")
	Service spy

	def "should inject spy by alias"() {
		when:
		final result = otherService.hello()

		then:
		result == Service.DEFAULT_RESPONSE
		1 * spy.hello(CALL_ARGUMENT)
	}

	@Configuration
	static class Config {
		@Bean
		OtherService otherService() {
			new OtherService()
		}

		@Bean(name = "alias")
		Service service() {
			new Service(new ServiceInteractionRecorder())
		}
	}

	private static class OtherService {
		private static final CALL_ARGUMENT = "call argument"

		@Autowired
		@Qualifier("alias")
		Service service

		String hello() {
			service.hello(CALL_ARGUMENT)
		}
	}
}
