package com.pchudzik.springmock.spock.spring;

import com.pchudzik.springmock.infrastructure.spring.util.ApplicationContextWalker;
import com.pchudzik.springmock.spock.SpockConstants;
import org.spockframework.mock.MockUtil;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import spock.lang.Specification;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.beans.factory.BeanFactory.FACTORY_BEAN_PREFIX;

/**
 * Will attach spock detached mocks to specification currently being executed.
 * It partially duplicated behaviour from {@link org.spockframework.spring.SpringMockTestExecutionListener}
 * but extends it's behaviours and includes factory beans to be attached to specification as well
 * <p>
 * TODO raise an incident for spockframework so they'll handle it
 */
public class MockAttachingTestExecutionListener extends AbstractTestExecutionListener {
	static final String MOCKED_BEANS_NAMES = SpockConstants.PACKAGE_PREFIX + "testExecutionListener.mockedBeans";
	private final MockUtil mockUtil;

	public MockAttachingTestExecutionListener() {
		this(new MockUtil());
	}

	MockAttachingTestExecutionListener(MockUtil mockUtil) {
		this.mockUtil = mockUtil;
	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		final Object testInstance = testContext.getTestInstance();
		if (!(testInstance instanceof Specification)) {
			return;
		}

		final Specification specification = (Specification) testInstance;
		final List<Object> mocks = new LinkedList<>();
		final ApplicationContext applicationContext = testContext.getApplicationContext();
		final ApplicationContextWalker contextWalker = new ApplicationContextWalker(applicationContext);

		for (String beanName : contextWalker.getBeanDefinitionNames()) {
			final BeanDefinition beanDefinition = contextWalker.getBeanDefinition(beanName);
			if (!beanDefinition.isAbstract() && beanDefinition.isSingleton()) {
				Stream
						.of(
								tryToGetBean(applicationContext, beanName),
								tryToGetBean(applicationContext, FACTORY_BEAN_PREFIX + beanName))
						.filter(mockUtil::isMock)
						.forEach(mock -> {
							mocks.add(mock);
							mockUtil.attachMock(mock, specification);
						});
			}
		}

		testContext.setAttribute(MOCKED_BEANS_NAMES, mocks);
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		getMocksFromContext(testContext).forEach(mockUtil::detachMock);
	}

	private Object tryToGetBean(ApplicationContext applicationContext, String beanName) {
		try {
			//null is possible when mocking factoryBean without default return value
			return applicationContext.getBean(beanName);
		} catch (NoSuchBeanDefinitionException | BeanIsNotAFactoryException ex) {
			return null;
		}
	}

	private List<Object> getMocksFromContext(TestContext testContext) {
		return Optional
				.ofNullable((List<Object>) testContext.getAttribute(MOCKED_BEANS_NAMES))
				.orElseGet(LinkedList::new);

	}
}
