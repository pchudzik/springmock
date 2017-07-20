package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import com.pchudzik.springmock.spock.test.spy.infrastructure.SpyConfig
import com.pchudzik.springmock.spock.test.spy.infrastructure.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class CreatedSpyShouldBeInjectable extends Specification {
	@AutowiredSpy
	Service service

	@Autowired
	OtherService otherService

	def "created spy should be injectable"() {
		given:
		final spyResponse = "spy response"

		when:
		final result = otherService.execute()

		then:
		1 * service.hello(OtherService.CALL_ARGUMENT) >> spyResponse

		and:
		result == spyResponse
	}

	@Configuration
	@Import(SpyConfig)
	static class Config {
		@Bean
		OtherService otherService(@Autowired Service service) {
			new OtherService(service)
		}
	}

	static class OtherService {
		public static final CALL_ARGUMENT = "call argument"

		private final Service anyService

		OtherService(Service anyService) {
			this.anyService = anyService
		}

		String execute() {
			return anyService.hello(CALL_ARGUMENT)
		}
	}
}
