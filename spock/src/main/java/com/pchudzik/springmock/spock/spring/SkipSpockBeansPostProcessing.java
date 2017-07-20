package com.pchudzik.springmock.spock.spring;

import com.pchudzik.springmock.spock.SpockConstants;
import org.spockframework.mock.MockUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

public class SkipSpockBeansPostProcessing extends InstantiationAwareBeanPostProcessorAdapter {
	public static final String BEAN_NAME = SpockConstants.PACKAGE_PREFIX + "skipSpockBeansPostProcessing";

	private final MockUtil mockUtil = new MockUtil();

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return !mockUtil.isMock(bean);
	}
}
