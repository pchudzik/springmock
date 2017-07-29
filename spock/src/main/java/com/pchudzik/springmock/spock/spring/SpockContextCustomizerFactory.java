package com.pchudzik.springmock.spock.spring;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.spring.MockContextCustomizer;
import com.pchudzik.springmock.infrastructure.spring.MockContextCustomizerFactory;
import com.pchudzik.springmock.spock.SpockDoubleFactory;
import com.pchudzik.springmock.spock.configuration.SpockDoubleConfigurationParser;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.test.context.ContextCustomizer;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class SpockContextCustomizerFactory extends MockContextCustomizerFactory {
	public SpockContextCustomizerFactory() {
		super(new SpockDoubleConfigurationParser());
	}

	@Override
	protected ContextCustomizer createContextCustomizer(DoubleRegistry doubleRegistry) {
		return new MockContextCustomizer(
				SpockDoubleFactory.class,
				doubleRegistry,
				Stream
						.of(new SimpleEntry<>(SkipSpockBeansPostProcessing.BEAN_NAME, skipSpockBeansPostProcessing()))
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	private static BeanDefinition skipSpockBeansPostProcessing() {
		return BeanDefinitionBuilder
				.rootBeanDefinition(SkipSpockBeansPostProcessing.class)
				.getBeanDefinition();
	}
}
