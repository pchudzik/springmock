package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.springframework.beans.factory.FactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * There is a problem with org.spockframework.spring.SpringMockTestExecutionListener
 * which is responsible for attaching mocks beans to the test context
 * When getBean is executed on factoryBean then object created by factory bean is returned instead of actual factory bean
 */
@ContextConfiguration
class ReplaceFactoryBeanWithMockTest extends Specification {
	@AutowiredMock
	FactoryBean<AnyService> anyServiceFactoryBean;

	def "should replace factory bean with mock"() throws Exception {
		given:
		final mockService = Mock(AnyService.class)

		and:
		mockService.hello() >> "mock"
		anyServiceFactoryBean.getObject() >> mockService

		when:
		final anyService = anyServiceFactoryBean.getObject()

		then:
		mockService.is(anyService)
		anyService.hello() == "mock"
	}

	@Configuration
	static class Config {
		@Bean
		FactoryBean<AnyService> anyServiceFactoryBean() {
			new FactoryBean<AnyService>() {
				@Override
				AnyService getObject() throws Exception {
					{-> "not a mock"}
				}

				@Override
				Class<?> getObjectType() {
					return AnyService.class;
				}

				@Override
				boolean isSingleton() {
					return false;
				}
			}
		}
	}
}
