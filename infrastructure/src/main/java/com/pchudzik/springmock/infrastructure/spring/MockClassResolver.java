package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

/**
 * <p>Creates link between beanName registered using @AutowiredMock with actual mock type</p>
 *
 * <p>It is required to to provide bean class type from the outside because mocks are created using
 * {@link DoubleFactory#createMock(DoubleDefinition)} and {@link DoubleFactory#createSpy(Object,
 * DoubleDefinition)} and signature of both of those methods returns Objects. Based on factory method
 * signature spring decides to use Object as bean target class instead of class defined in
 * beanDefinition.</p>
 *
 * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#determineTargetType(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Class[])
 * @see ProblemWithObjectReturningFactoryMethodFailingTest
 * @see ProblemWithObjectReturningFactoryMethodPassingTest
 *
 */
class MockClassResolver extends InstantiationAwareBeanPostProcessorAdapter {
	private static final Class<?> NOT_PREDICTABLE = null;
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "mockClassResolver";

	private final DoubleRegistry doubleRegistry;

	public MockClassResolver(DoubleRegistry doubleRegistry) {
		this.doubleRegistry = doubleRegistry;
	}

	@Override
	public Class<?> predictBeanType(Class<?> beanClass, String beanName) {
		final DoubleSearch mockSearch = doubleRegistry.mockSearch();
		if(mockSearch.containsExactlyOneDouble(beanName)) {
			final DoubleDefinition definition = mockSearch.findOneDefinition(beanName);
			return definition.getDoubleClass();
		}

		return NOT_PREDICTABLE;
	}
}
