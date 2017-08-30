package com.pchudzik.springmock.spock.spring

import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.FactoryBean
import org.springframework.test.context.TestContext
import spock.lang.Specification

import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.bean
import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.buildAppContext
import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.withEmptyDoubleRegistry
import static com.pchudzik.springmock.spock.spring.MockAttachingTestExecutionListener.MOCKED_BEANS_NAMES

class MockAttachingTestExecutionListenerTest extends Specification {
	def specification = new Specification() {}
	def mockUtil = Spy(new MockUtil())
	def contextAttributes = [:]
	def testContext = Mock(TestContext) {
		getTestInstance() >> specification
		setAttribute(_, _) >> { key, value -> contextAttributes.put(key, value) }
		getAttribute(_) >> { arguments -> contextAttributes.get(arguments[0]) }
	}
	def listener = new MockAttachingTestExecutionListener(mockUtil)

	def "should fail when executing on non spock.lang.Specification test case"() {
		given:
		final testContext = Mock(TestContext) {
			getTestInstance() >> new Object()
		}

		when:
		listener.beforeTestMethod(testContext)

		then:
		thrown IllegalArgumentException
	}

	def "should attach detached mocks to specification"() {
		given:
		final mock = Mock(Object)
		final spy = Spy(new Object())
		testContext.getApplicationContext() >> buildAppContext([
				bean("notMock", new Object()),
				bean("mock", mock),
				bean("spy", spy),
				withEmptyDoubleRegistry()
		])

		when:
		listener.beforeTestMethod(testContext)

		then:
		1 * testContext.setAttribute(MOCKED_BEANS_NAMES, [mock, spy])
		1 * mockUtil.attachMock(mock, specification)
		1 * mockUtil.attachMock(spy, specification)

		and:
		0 * mockUtil.attachMock(_, specification)
	}

	def "should attach mocked factory beans to context"() {
		given:
		final factoryBeanMock = Mock(FactoryBean)
		testContext.getApplicationContext() >> buildAppContext([
				bean("notAMock", new Object()),
				bean("factoryBean", factoryBeanMock),
				withEmptyDoubleRegistry()
		])

		when:
		listener.beforeTestMethod(testContext)

		then:
		1 * testContext.setAttribute(MOCKED_BEANS_NAMES, [factoryBeanMock])
		1 * mockUtil.attachMock(factoryBeanMock, specification)

		and:
		0 * mockUtil.attachMock(_, specification)
	}

	def "should detach mocks after test execution is done"() {
		given:
		final factoryBeanMock = Mock(FactoryBean)
		final regularMock = Mock(Object)
		final regularSpy = Spy(new Object())
		testContext.getApplicationContext() >> buildAppContext([
				bean("notMock", new Object()),
				bean("factoryBeanMock", factoryBeanMock),
				bean("regularMock", regularMock),
				bean("regularSpy", regularSpy),
				withEmptyDoubleRegistry()
		])
		listener.beforeTestMethod(testContext)

		when:
		listener.afterTestMethod(testContext)

		then:
		1 * mockUtil.detachMock(factoryBeanMock)
		1 * mockUtil.detachMock(regularMock)
		1 * mockUtil.detachMock(regularSpy)

		and:
		0 * mockUtil.detachMock(_)
	}

	def "should not fail when no mocks are registered after test execution"() {
		when:
		listener.afterTestMethod(testContext)

		then:
		noExceptionThrown()
	}

	def "should attach mocks defined on level of context hierarchy"() {
		given:
		final parentMock = Mock(Object)
		final parentSpy = Spy(new Object())
		final childMock = Mock(Object)
		final childSpy = Spy(new Object())
		final parentContext = buildAppContext([
				bean("parentMock", parentMock),
				bean("parentSpy", parentSpy),
				bean("parentNotAMock", new Object()),
				withEmptyDoubleRegistry()
		])
		final childContext = buildAppContext(parentContext, [
				childMock    : childMock,
				childSpy     : childSpy,
				childNotAMock: new Object()
		])
		testContext.getApplicationContext() >> childContext

		when:
		listener.beforeTestMethod(testContext)

		then:
		[parentMock, parentSpy, childMock, childSpy].each {
			1 * mockUtil.attachMock(it, specification)
		}

		and:
		0 * mockUtil.attachMock(_, specification)
	}
}
