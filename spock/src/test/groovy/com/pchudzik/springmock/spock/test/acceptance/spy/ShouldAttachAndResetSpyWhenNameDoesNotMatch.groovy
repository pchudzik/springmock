package com.pchudzik.springmock.spock.test.acceptance.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.springframework.beans.factory.FactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class ShouldAttachAndResetSpyWhenNameDoesNotMatch extends Specification {
	@AutowiredSpy
	Service spy

	@AutowiredSpy(alias = "stringFactoryBean")
	FactoryBean<String> spyFactory

	def "should inject spy"() {
		when:
		spy.doStuff()

		then:
		1 * spy.doStuff()
	}

	def "should reset spy"() {
		when:
		noInteractions(spy)

		then:
		0 * spy.doStuff()
	}

	def "should inject and attach FactoryBean spy"() {
		when:
		spyFactory.getObject()

		then:
		1 * spyFactory.getObject()
	}

	def "should reset FactoryBean spy"() {
		when:
		noInteractions(spyFactory)

		then:
		0 * spyFactory.getObject()
	}

	private void noInteractions(Object anything) {
	}

	@Configuration
	static class Config {
		@Bean
		Service service() {
			new Service()
		}

		@Bean
		FactoryBean<String> stringFactoryBean() {
			new FactoryBean<String>() {
				@Override
				String getObject() throws Exception {
					return "factory"
				}

				@Override
				Class<?> getObjectType() {
					return String.class
				}

				@Override
				boolean isSingleton() {
					return true
				}
			}
		}
	}

	static class Service {
		void doStuff() {
			//nothing here
		}
	}
}
