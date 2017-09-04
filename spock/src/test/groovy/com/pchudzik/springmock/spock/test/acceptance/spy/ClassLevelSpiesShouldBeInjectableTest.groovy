package com.pchudzik.springmock.spock.test.acceptance.spy

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.junit.Assert.assertTrue

@ContextConfiguration
@AutowiredSpy(doubleClass = ClassLevelSpiesShouldBeInjectableTest.TestCaseLevel.class)
class ClassLevelSpiesShouldBeInjectableTest extends Specification{
	@Autowired
	ApplicationContext applicationContext;

	def "should inject test class level spies"() {
		given:
		final object = applicationContext.getBean(TestCaseLevel.class);

		expect:
		assertTrue(new MockUtil().isMock(object));
	}

	def "should inject configuration class level spies"() {
		given:
		final object = applicationContext.getBean(ConfigurationLevel.class)

		expect:
		assertTrue(new MockUtil().isMock(object))
	}

	@Configuration
	@AutowiredSpy(doubleClass = ConfigurationLevel.class)
	static class Config {

	}

	static class TestCaseLevel {
	}

	static class ConfigurationLevel {
	}
}
