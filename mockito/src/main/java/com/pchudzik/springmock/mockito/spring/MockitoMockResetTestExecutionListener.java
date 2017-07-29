package com.pchudzik.springmock.mockito.spring;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import com.pchudzik.springmock.infrastructure.spring.util.ApplicationContextWalker;
import com.pchudzik.springmock.mockito.configuration.MockitoDoubleConfiguration;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.function.Predicate;

public class MockitoMockResetTestExecutionListener extends AbstractTestExecutionListener {
	private final MockResetExecutor mockResetExecutor;

	public MockitoMockResetTestExecutionListener() {
		this(object -> Mockito.reset(object));
	}

	MockitoMockResetTestExecutionListener(MockResetExecutor mockResetExecutor) {
		this.mockResetExecutor = mockResetExecutor;
	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		resetMocks(testContext, config -> config.shouldResetBeforeTest());
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		resetMocks(testContext, config -> config.shouldResetAfterTest());
	}

	private void resetMocks(TestContext testContext, Predicate<MockitoDoubleConfiguration> shouldResetPredicate) throws Exception {
		final ApplicationContext applicationContext = testContext.getApplicationContext();
		final ApplicationContextWalker contextWalker = new ApplicationContextWalker(applicationContext);
		final DoubleRegistry doubleRegistry = applicationContext.getBean(DoubleRegistry.BEAN_NAME, DoubleRegistry.class);
		final DoubleSearch doubleSearch = doubleRegistry.doublesSearch();

		for (String beanName : contextWalker.getBeanDefinitionNames()) {
			final Object bean = applicationContext.getBean(beanName);
			if (Mockito.mockingDetails(bean).isMock() && doubleSearch.containsExactlyOneDouble(beanName)) {
				final DoubleDefinition definition = doubleSearch.findOneDefinition(beanName);
				final MockitoDoubleConfiguration configuration = definition
						.getConfiguration(MockitoDoubleConfiguration.class)
						.orElseGet(MockitoDoubleConfiguration::defaultConfiguration);

				if (shouldResetPredicate.test(configuration)) {
					mockResetExecutor.resetMock(bean);
				}
			}
		}
	}

	interface MockResetExecutor {
		void resetMock(Object object);
	}
}
