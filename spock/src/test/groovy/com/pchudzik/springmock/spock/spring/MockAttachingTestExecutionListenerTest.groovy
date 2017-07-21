package com.pchudzik.springmock.spock.spring

import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.context.TestContext
import spock.lang.Specification

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
				"notMock": new Object(),
				"mock"   : mock,
				"spy"    : spy
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
				"notAMock"   : new Object(),
				"factoryBean": factoryBeanMock
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
				"notMock"        : new Object(),
				"factoryBeanMock": factoryBeanMock,
				"regularMock"    : regularMock,
				"regularSpy"     : regularSpy
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
				parentMock    : parentMock,
				parentSpy     : parentSpy,
				parentNotAMock: new Object()
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

	private ApplicationContext buildAppContext(Map<String, Object> beans) {
		buildAppContext(null, beans)
	}

	private ApplicationContext buildAppContext(ApplicationContext parent, Map<String, Object> beans) {
		final beanFactory = new DefaultListableBeanFactory()
		final applicationContext = new GenericApplicationContext(beanFactory, parent)

		beans.each { key, value ->
			final factoryBean = "${key}_factory"
			beanFactory.registerBeanDefinition(factoryBean, BeanDefinitionBuilder
					.rootBeanDefinition(MockBeanHolder.class)
					.addConstructorArgValue(value)
					.getBeanDefinition())
			beanFactory.registerBeanDefinition(key, BeanDefinitionBuilder
					.rootBeanDefinition(value != null ? value.class : Object.class)
					.setFactoryMethodOnBean("getBean", factoryBean)
					.getBeanDefinition())
		}
		applicationContext.refresh()

		return applicationContext
	}

	static class MockBeanHolder {
		private final Object bean

		MockBeanHolder(Object bean) {
			this.bean = bean
		}

		Object getBean() {
			return bean
		}
	}

}
