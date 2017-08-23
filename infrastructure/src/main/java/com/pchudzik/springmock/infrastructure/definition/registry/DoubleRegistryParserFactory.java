package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;

import java.lang.annotation.Annotation;

public class DoubleRegistryParserFactory {
	private final DoubleClassResolver doubleClassResolver = new DoubleClassResolver();
	private final AnnotatedElementsScanner annotatedElementsScanner = new AnnotatedElementsScanner();
	private final DoubleNameResolver doubleNameResolver = new DoubleNameResolver();
	private final DoubleConfigurationResolver doubleConfigurationResolver;

	public DoubleRegistryParserFactory(Class<? extends Annotation> configurationAnnotation, DoubleConfigurationParser configurationParser) {
		this.doubleConfigurationResolver = new DoubleConfigurationResolver(configurationAnnotation, configurationParser);
	}

	public DoubleRegistryParser doubleRegistryParser() {
		return new DoubleRegistryParser(annotatedElementsScanner(), mockDoubleDefinitionFactory(), spyDoubleDefinitionFactory());
	}

	protected AnnotatedElementsScanner annotatedElementsScanner() {
		return annotatedElementsScanner;
	}

	protected DoubleDefinitionFactory<AutowiredMock> mockDoubleDefinitionFactory() {
		return new DoubleDefinitionFactory<>(
				AutowiredMock.class,
				AnnotationDetails::mock,
				doubleNameResolver(),
				doubleClassResolver(),
				doubleConfigurationResolver());
	}

	protected DoubleDefinitionFactory<AutowiredSpy> spyDoubleDefinitionFactory() {
		return new DoubleDefinitionFactory<>(
				AutowiredSpy.class,
				AnnotationDetails::spy,
				doubleNameResolver(),
				doubleClassResolver(),
				doubleConfigurationResolver());
	}

	protected DoubleConfigurationResolver doubleConfigurationResolver() {
		return doubleConfigurationResolver;
	}

	protected DoubleClassResolver doubleClassResolver() {
		return doubleClassResolver;
	}

	protected DoubleNameResolver doubleNameResolver() {
		return doubleNameResolver;
	}
}
