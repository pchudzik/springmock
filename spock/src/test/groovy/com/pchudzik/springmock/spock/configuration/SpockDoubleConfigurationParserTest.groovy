package com.pchudzik.springmock.spock.configuration

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import spock.lang.Specification

import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.findDoublesInClass
import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.getConfig

class SpockDoubleConfigurationParserTest extends Specification {
	def "should configure mock name base on field name"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition(AnyTest.mockFieldName)

		expect:
		getConfig(definition).createDoubleConfiguration()[SpockSettingsKeys.NAME] == AnyTest.mockFieldName
	}

	def "should configure spy name base on field name"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition(AnyTest.spyFieldName)

		expect:
		getConfig(definition).createDoubleConfiguration()[SpockSettingsKeys.NAME] == AnyTest.spyFieldName
	}

	def "should configure mock name base on AutowiredMock#name"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("my mock")

		expect:
		getConfig(definition).createDoubleConfiguration()[SpockSettingsKeys.NAME] == AnyTest.namedMock
	}

	def "should configure spy name base on AutowiredSpy#name"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition(AnyTest.namedSpy)

		expect:
		getConfig(definition).createDoubleConfiguration()[SpockSettingsKeys.NAME] == AnyTest.namedSpy
	}

	private static class AnyTest {
		public static final mockFieldName = "defaultMock"
		public static final spyFieldName = "defaultSpy"
		public static final namedMock = "my mock"
		public static final namedSpy = "my spy"

		@AutowiredMock
		Object defaultMock

		@AutowiredSpy
		Object defaultSpy

		@AutowiredSpy(name = "my spy")
		Object spy

		@AutowiredMock(name = "my mock")
		Object mock
	}
}
