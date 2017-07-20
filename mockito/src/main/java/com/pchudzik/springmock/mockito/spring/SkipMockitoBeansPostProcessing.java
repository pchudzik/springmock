package com.pchudzik.springmock.mockito.spring;

import com.pchudzik.springmock.infrastructure.MockConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import static org.mockito.Mockito.mockingDetails;

class SkipMockitoBeansPostProcessing extends InstantiationAwareBeanPostProcessorAdapter {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "skipMockitoBeansPostProcessing";

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return !mockingDetails(bean).isMock();
	}
}
