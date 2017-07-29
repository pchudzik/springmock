package com.pchudzik.springmock.spock.configuration

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.spockframework.mock.IDefaultResponse
import org.spockframework.mock.IMockInvocation
import org.spockframework.mock.ZeroOrNullResponse
import spock.lang.Specification

import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.findDoublesInClass
import static com.pchudzik.springmock.spock.configuration.SpockConfigurationHelper.getConfig

class DoubleDefaultResponseTest extends Specification {
	def "should create mock with requested default response"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("withResponse")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.DEFAULT_RESPONSE].getClass() == NoResponse.class
	}

	def "mocks should be created using default mechanisms by default"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("mock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.DEFAULT_RESPONSE] == null
	}

	def "should parse default response from meta annotations"() {
		given:
		final doubles = findDoublesInClass(AnyTest)
		final definition = doubles.findOneDefinition("complexMock")

		when:
		final doubleConfiguration = getConfig(definition).createDoubleConfiguration()

		then:
		doubleConfiguration[SpockSettingsKeys.DEFAULT_RESPONSE].getClass() == NoResponse.class
	}

	private static class AnyTest {
		@AutowiredMock
		Object mock

		@AutowiredSpy
		@SpockDouble(defaultResponse = NoResponse)
		Object withResponse

		@DoubleDefaultResponseMetaAnnotation
		Object complexMock
	}

	private static class NoResponse implements IDefaultResponse {
		private NoResponse() {}

		@Override
		Object respond(IMockInvocation invocation) {
			ZeroOrNullResponse.INSTANCE.respond(invocation)
		}
	}
}
