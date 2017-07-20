package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.junit.Test
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class ReplaceExistingBeanWithMockTest extends Specification {
	@AutowiredMock
	AnyService anyService;

	@Test
	public void should_replace_existing_bean_with_mock() {
		given:
		anyService.hello() >> "mock"

		expect:
		anyService.hello() == "mock"
	}

	@Configuration
	static class Config {
		@Bean
		AnyService anyService() {
			{ -> "not a mock" }
		}
	}
}
