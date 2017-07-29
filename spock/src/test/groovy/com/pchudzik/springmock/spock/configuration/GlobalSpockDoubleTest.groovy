package com.pchudzik.springmock.spock.configuration

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import spock.lang.Specification

import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.findDoublesInClass
import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.getConfig

class GlobalSpockDoubleTest extends Specification {
	def "should create global mock"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("global")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.GLOBAL] == true
	}

	def "mocks should not be global default"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("mock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.GLOBAL] == null
	}

	def "should set global mock from meta annotations"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("complexMock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.GLOBAL] == true
	}

	private static class AnyTest {
		@AutowiredMock
		Object mock

		@AutowiredSpy
		@SpockDouble(global = true)
		Object global

		@GlobalSpockDoubleMetaAnnotation
		Object complexMock
	}
}
