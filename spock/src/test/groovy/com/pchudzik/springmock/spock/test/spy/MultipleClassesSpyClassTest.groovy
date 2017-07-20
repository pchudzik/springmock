package com.pchudzik.springmock.spock.test.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import com.pchudzik.springmock.spock.test.spy.infrastructure.ServiceInteractionRecorder
import com.pchudzik.springmock.spock.test.spy.infrastructure.Service
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class MultipleClassesSpyClassTest extends Specification{
	@AutowiredSpy
	Service spy1

	@AutowiredSpy
	Service spy2

	@Autowired
	Service service

	def "should create and distinguish multiple spy instances"() {
		given:
		final mockUtil = new MockUtil()

		expect:
		!mockUtil.isMock(service)

		and:
		mockUtil.isMock(spy1)
		mockUtil.isMock(spy2)

		and:
		!spy1.is(spy2)
	}

	@Configuration
	static class Config {
		@Bean
		Service service() {
			new Service(new ServiceInteractionRecorder())
		}

		@Bean
		Service spy1() {
			new Service(new ServiceInteractionRecorder())
		}

		@Bean
		Service spy2() {
			new Service(new ServiceInteractionRecorder())
		}
	}
}
