package com.pchudzik.springmock.spock.configuration

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.spockframework.mock.MockImplementation
import spock.lang.Specification

import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.findDoublesInClass
import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.getConfig

class SpockDoubleImplementationTest extends Specification {
	def "should create mock using requested implementation"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("withImplementation")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.IMPLEMENTATION] == MockImplementation.GROOVY
	}

	def "mocks should be created using default mechanisms by default"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("mock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.IMPLEMENTATION] == null
	}

	def "should parse implementation from meta annotations"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("complexMock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.IMPLEMENTATION] == MockImplementation.GROOVY
	}

	private static class AnyTest {
		@AutowiredMock
		Object mock

		@AutowiredSpy
		@SpockDouble(mockImplementation = MockImplementation.GROOVY)
		Object withImplementation

		@SpockDoubleImplementationMetaAnnotation
		Object complexMock
	}
}
