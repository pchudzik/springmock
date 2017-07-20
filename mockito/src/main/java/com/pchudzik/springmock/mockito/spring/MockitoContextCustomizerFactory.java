package com.pchudzik.springmock.mockito.spring;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.spring.MockContextCustomizer;
import com.pchudzik.springmock.infrastructure.spring.MockContextCustomizerFactory;
import com.pchudzik.springmock.mockito.MockitoDoubleFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.test.context.ContextCustomizer;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class MockitoContextCustomizerFactory extends MockContextCustomizerFactory {
	@Override
	protected ContextCustomizer createContextCustomizer(DoubleRegistry doubleRegistry) {
		return new MockContextCustomizer(
				MockitoDoubleFactory.class,
				doubleRegistry,
				Stream
						.of(new SimpleEntry<>(SkipMockitoBeansPostProcessing.BEAN_NAME, skipMockitoBeansPostProcessing()))
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	private static AbstractBeanDefinition skipMockitoBeansPostProcessing() {
		return BeanDefinitionBuilder.genericBeanDefinition(SkipMockitoBeansPostProcessing.class).getBeanDefinition();
	}
}
