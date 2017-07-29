package com.pchudzik.springmock.spock.configuration

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import spock.lang.Specification

import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.findDoublesInClass
import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.getConfig


class UseObjenesisInDoublesTest extends Specification {
	def "should create mock using objensis library"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("objenesis")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.USE_OBJENESIS] == true
	}

	def "mocks should be created using default mechanisms by default"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("mock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.USE_OBJENESIS] == null
	}

	def "should parse objenesis flag from meta annotations"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("complexMock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.USE_OBJENESIS] == true
	}

	private static class AnyTest {
		@AutowiredMock
		Object mock

		@AutowiredSpy
		@SpockDouble(useObjenesis = true)
		Object objenesis

		@UseObjenesisMetaAnnotation
		Object complexMock
	}
}
