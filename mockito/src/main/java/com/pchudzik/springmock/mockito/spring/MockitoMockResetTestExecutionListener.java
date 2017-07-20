package com.pchudzik.springmock.mockito.spring;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class MockitoMockResetTestExecutionListener extends AbstractTestExecutionListener {
	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		super.beforeTestMethod(testContext);
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		final ApplicationContext applicationContext = testContext.getApplicationContext();
		final DoubleRegistry doubleRegistry = applicationContext.getBean(DoubleRegistry.BEAN_NAME, DoubleRegistry.class);

		doubleRegistry.getRegisteredMockNames().forEach(mockName -> {
			final Object mockBean = applicationContext.getBean(mockName);
			Mockito.reset(mockBean);
		});
	}
}
