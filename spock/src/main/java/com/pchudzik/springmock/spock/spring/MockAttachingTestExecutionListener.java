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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
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
			throw new IllegalArgumentException(getClass().getSimpleName() + " can be applied only for spock specifications");
		}

		final Specification specification = (Specification) testInstance;
		final List<Object> mocks = new LinkedList<>();
		final ApplicationContext applicationContext = testContext.getApplicationContext();
		final Collection<String> beanNames = getBeanDefinitionNames(applicationContext);

		for (String beanName : beanNames) {
			final BeanDefinition beanDefinition = getBeanDefinition(applicationContext, beanName);
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

	private BeanDefinition getBeanDefinition(ApplicationContext applicationContext, String beanName) {
		return walkContext(applicationContext, ctx -> tryToGetBeanDefinition((BeanDefinitionRegistry) ctx, beanName))
				.stream()
				.flatMap(selectOnlyPresentOptionals())
				.findFirst()
				.orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition for " + beanName));
	}

	private <T> Function<Optional<T>, Stream<? extends T>> selectOnlyPresentOptionals() {
		return maybeDefinition -> maybeDefinition.map(Stream::of).orElse(Stream.empty());
	}

	private Collection<String> getBeanDefinitionNames(ApplicationContext applicationContext) {
		return walkContext(applicationContext, ctx -> asList(ctx.getBeanDefinitionNames()))
				.stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	private <T> Collection<T> walkContext(ApplicationContext applicationContext, Function<ApplicationContext, T> contextProcessor) {
		final List<T> result = new LinkedList<>();
		ApplicationContext currentContext = applicationContext;
		while (currentContext != null) {
			T processingResult = contextProcessor.apply(currentContext);
			currentContext = currentContext.getParent();

			result.add(processingResult);
		}

		return result;
	}

	private Optional<BeanDefinition> tryToGetBeanDefinition(BeanDefinitionRegistry registry, String beanName) {
		try {
			return Optional.of(registry.getBeanDefinition(beanName));
		} catch (NoSuchBeanDefinitionException ex) {
			return Optional.empty();
		}
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
