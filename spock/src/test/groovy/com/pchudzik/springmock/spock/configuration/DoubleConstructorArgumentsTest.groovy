package com.pchudzik.springmock.spock.configuration

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import spock.lang.Specification

import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.findDoublesInClass
import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.getConfig

class DoubleConstructorArgumentsTest extends Specification {
	def "should create mock using provided constructor arguments"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("withResponse")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.CONSTRUCTOR_ARGS].getClass() == FakeArgumentsProvider.class
	}

	def "mocks should be created using default mechanisms by default"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("mock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.CONSTRUCTOR_ARGS] == null
	}

	def "should parse constructor arguments provider from meta annotation"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("complexMock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.CONSTRUCTOR_ARGS].getClass() == FakeArgumentsProvider.class
	}

	private static class AnyTest {
		@AutowiredMock
		Object mock

		@AutowiredSpy
		@SpockDouble(constructorArguments = FakeArgumentsProvider)
		Object withResponse

		@DoubleConstructorArgumentsMetaAnnotation
		Object complexMock
	}

	static class FakeArgumentsProvider implements SpockDouble.ConstructorArgumentsProvider {
		private FakeArgumentsProvider() {}

		@Override
		List<Object> getConstructorArguments() {
			["a", "b"]
		}
	}
}
