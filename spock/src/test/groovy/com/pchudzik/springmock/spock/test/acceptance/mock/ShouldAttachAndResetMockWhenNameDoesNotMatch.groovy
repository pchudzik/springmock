package com.pchudzik.springmock.spock.test.acceptance.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import org.springframework.beans.factory.FactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class ShouldAttachAndResetMockWhenNameDoesNotMatch extends Specification {
	@AutowiredMock
	Service mock;

	@AutowiredMock(alias = "stringFactory")
	FactoryBean<String> mockFactory

	def "should inject and attach mock"() {
		when:
		mock.doStuff()

		then:
		1 * mock.doStuff()
	}

	def "should reset mock"() {
		when:
		noInteractions(mock)

		then:
		0 * mock.doStuff()
	}

	def "should inject and attach FactoryBean mock"() {
		when:
		mockFactory.getObject()

		then:
		1 * mockFactory.getObject()
	}

	def "should reset FactoryBean mock"() {
		when:
		noInteractions(mockFactory)

		then:
		0 * mockFactory.getObject()
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
		FactoryBean<String> stringFactory() {
			new FactoryBean<String>() {
				@Override
				String getObject() throws Exception {
					return "instance"
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
		}
	}
}
