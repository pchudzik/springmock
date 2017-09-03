package com.pchudzik.springmock.spock.spring

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.FactoryBean
import org.springframework.test.context.TestContext
import spock.lang.Specification

import java.util.stream.Stream

import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.*
import static com.pchudzik.springmock.spock.spring.MockAttachingTestExecutionListener.MOCKED_BEANS_NAMES
import static java.util.Collections.emptyList

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

	def "should do nothing when executing on non spock.lang.Specification test case"() {
		given:
		final testContext = Mock(TestContext)

		when:
		listener.beforeTestMethod(testContext)

		then:
		1 * testContext.getTestInstance() >> new Object()

		and:
		0 * testContext._
		0 * mockUtil._
	}

	def "should attach detached mocks to specification"() {
		given:
		final aMock = Mock(Object)
		final aSpy = Spy(new Object())
		final mockName = "mock"
		final spyName = "spy"
		testContext.getApplicationContext() >> buildAppContext([
				bean("notMock", new Object()),
				bean(mockName, aMock),
				bean(spyName, aSpy),
				withDoubleRegistry(new DoubleRegistry(
						mocks([doubleDefinition(mockName)]),
						spies([doubleDefinition(spyName)])
				))
		])

		when:
		listener.beforeTestMethod(testContext)

		then:
		1 * testContext.setAttribute(MOCKED_BEANS_NAMES, [aMock, aSpy])
		1 * mockUtil.attachMock(aMock, specification)
		1 * mockUtil.attachMock(aSpy, specification)

		and:
		0 * mockUtil.attachMock(_, specification)
	}

	def "should attach mocked factory beans to context"() {
		given:
		final factoryBeanMock = Mock(FactoryBean)
		final factoryBeanMockName = "factoryBean"
		testContext.getApplicationContext() >> buildAppContext([
				bean("notAMock", new Object()),
				bean(factoryBeanMockName, factoryBeanMock),
				withDoubleRegistry(new DoubleRegistry(
						mocks([doubleDefinition(factoryBeanMockName, FactoryBean)]),
						spies(emptyList())
				))
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
		final factoryBeanMockName = "factoryBeanMock"
		final mockName = "regularMock"
		final spyName = "regularSpy"
		testContext.getApplicationContext() >> buildAppContext([
				bean("notMock", new Object()),
				bean(factoryBeanMockName, factoryBeanMock),
				bean(mockName, regularMock),
				bean(spyName, regularSpy),
				withDoubleRegistry(new DoubleRegistry(
						mocks([doubleDefinition(mockName), doubleDefinition(factoryBeanMockName, FactoryBean)]),
						spies([doubleDefinition(spyName)])
				))
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
		final parentMockName = "parentMock"
		final parentSpyName = "parentSpy"
		final childMockName = "childMock"
		final childSpyName = "childSpy"
		final doubleRegistry = withDoubleRegistry(new DoubleRegistry(
				mocks([
						doubleDefinition(parentMockName),
						doubleDefinition(childMockName)
				]),
				spies([
						doubleDefinition(parentSpyName),
						doubleDefinition(childSpyName)
				])
		))

		and:
		final parentContext = buildAppContext([
				bean(parentMockName, parentMock),
				bean(parentSpyName, parentSpy),
				bean("parentNotAMock", new Object())
		])
		final childContext = buildAppContext(parentContext, Stream.of(
				bean(childMockName, childMock),
				bean(childSpyName, childSpy),
				bean("childNotAMock", new Object()),
				doubleRegistry
		))
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

	private DoubleDefinition doubleDefinition(String name, Class<?> clazz = Object.class) {
		DoubleDefinition.builder().name(name).doubleClass(clazz).build()
	}

	private Collection<DoubleDefinition> mocks(Collection<DoubleDefinition> mocksDefinitions) {
		return mocksDefinitions
	}

	private Collection<DoubleDefinition> spies(Collection<DoubleDefinition> spiesDefinitions) {
		return spiesDefinitions
	}
}
