package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistryParser;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistryParserFactory;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import java.lang.annotation.Annotation;
import java.util.List;

public abstract class MockContextCustomizerFactory implements ContextCustomizerFactory {
	private final Class<? extends Annotation> configurationAnnotation;
	private final DoubleConfigurationParser<?, Annotation> configurationParser;

	public MockContextCustomizerFactory(Class<? extends Annotation> configurationAnnotation, DoubleConfigurationParser configurationParser) {
		this.configurationAnnotation = configurationAnnotation;
		this.configurationParser = configurationParser;
	}

	@Override
	public ContextCustomizer createContextCustomizer(Class<?> aClass, List<ContextConfigurationAttributes> list) {
		final DoubleRegistryParser registryFactory = new DoubleRegistryParserFactory(configurationAnnotation, configurationParser).doubleRegistryParser();
		return createContextCustomizer(registryFactory.parse(aClass));
	}

	/**
	 * <p>Should create context customizer with all mock definitions configured in the application context.</p>
	 *
	 * @param doubleRegistry
	 * @return
	 */
	protected abstract ContextCustomizer createContextCustomizer(DoubleRegistry doubleRegistry);
}
