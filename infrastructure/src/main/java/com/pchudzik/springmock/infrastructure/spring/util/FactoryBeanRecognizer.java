package com.pchudzik.springmock.infrastructure.spring.util;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;

import static org.springframework.beans.factory.BeanFactory.FACTORY_BEAN_PREFIX;

public class FactoryBeanRecognizer {
	public static boolean isFactoryBean(Class<?> objectClazz) {
		return FactoryBean.class.isAssignableFrom(objectClazz);
	}

	public static String getFactoryBeanName(String definitionName) {
		if (definitionName.startsWith(FACTORY_BEAN_PREFIX)) {
			return definitionName;
		}

		return FACTORY_BEAN_PREFIX + definitionName;
	}

	public static String getBeanDefinitionName(String beanName) {
		return BeanFactoryUtils.transformedBeanName(beanName);
	}
}
