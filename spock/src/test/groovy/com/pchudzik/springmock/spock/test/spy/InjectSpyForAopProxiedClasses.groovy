package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class InjectSpyForAopProxiedClasses extends Specification {
	@AutowiredSpy
	Service service

	def "should inject async service as a synchronous mock"() {
		given:
		service.hello() >> { throw new ExpectedException() }

		when:
		service.hello()

		then:
		thrown ExpectedException
	}

	@Configuration
	@EnableAsync
	static class Config {
		@Bean
		Service service() {
			new Service()
		}
	}

	static class Service {
		@Async
		String hello() {
			"hello"
		}
	}

	private static class ExpectedException extends RuntimeException {
	}
}
