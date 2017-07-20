package com.pchudzik.springmock.spock.spring;

import com.pchudzik.springmock.spock.SpockConstants;
import org.spockframework.mock.MockUtil;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import spock.lang.Specification;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.springframework.beans.factory.BeanFactory.FACTORY_BEAN_PREFIX;

/**
 * Will attach spock detached mocks to specification currently being executed.
 * It partially duplicated behaviour from {@link org.spockframework.spring.SpringMockTestExecutionListener}
 * but extends it's behaviours and includes factory beans to be attached to specification as well
 * <p>
 * TODO raise an incident for spockframework so they'll handle it
 */
public class MockAttachingTestExecutionListener extends AbstractTestExecutionListener {
	private static final String MOCKED_BEANS_NAMES = SpockConstants.PACKAGE_PREFIX + "testExecutionListener.mockedBeans";
	private final MockUtil mockUtil = new MockUtil();

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		final Object testInstance = testContext.getTestInstance();
		if (!(testInstance instanceof Specification)) {
			throw new IllegalArgumentException(getClass().getSimpleName() + " can be applied only for spock specifications");
		}

		final List<Object> mockedBeans = new LinkedList<>();

		final Specification specification = (Specification) testInstance;
		final ApplicationContext applicationContext = testContext.getApplicationContext();
		final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext;
		final String[] mockBeanNames = applicationContext.getBeanDefinitionNames();

		for (String beanName : mockBeanNames) {
			final BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanName);
			if (!beanDefinition.isAbstract() && beanDefinition.isSingleton()) {
				Optional<Object> bean = tryToGetBean(applicationContext, beanName);
				Optional<Object> beanFactory = tryToGetBean(applicationContext, FACTORY_BEAN_PREFIX + beanName);

				attachIfPresent(specification, bean, mockedBeans);
				attachIfPresent(specification, beanFactory, mockedBeans);
			}
		}

		testContext.setAttribute(MOCKED_BEANS_NAMES, mockedBeans);
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		final List<Object> mockedBeans = (List<Object>) testContext.getAttribute(MOCKED_BEANS_NAMES);
		if (mockedBeans != null) {
			mockedBeans.forEach(mockUtil::detachMock);
		}
	}

	private void attachIfPresent(Specification specification, Optional<Object> maybeBean, List<Object> mocks) {
		if (maybeBean.isPresent() && mockUtil.isMock(maybeBean.get())) {
			mockUtil.attachMock(maybeBean.get(), specification);
			mocks.add(maybeBean.get());
		}
	}

	private Optional<Object> tryToGetBean(ApplicationContext applicationContext, String beanName) {
		try {
			//null is possible when mocking factoryBean without default return value
			return Optional.ofNullable(applicationContext.getBean(beanName));
		} catch (NoSuchBeanDefinitionException | BeanIsNotAFactoryException ex) {
			return Optional.empty();
		}
	}
}
