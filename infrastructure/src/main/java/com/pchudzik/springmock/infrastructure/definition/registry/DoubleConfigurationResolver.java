package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.BiFunction;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

class DoubleConfigurationResolver {
	private final Class<? extends Annotation> configurationAnnotation;
	private final DoubleConfigurationParser<?, Annotation> configurationParser;

	DoubleConfigurationResolver(Class<? extends Annotation> configurationAnnotation, DoubleConfigurationParser<?, Annotation> configurationParser) {
		this.configurationAnnotation = configurationAnnotation;
		this.configurationParser = configurationParser;
	}

	public Object resolveMockConfiguration(String doubleName, Field field) {
		return resolveDoubleConfiguration(
				doubleName,
				field,
				configurationParser::parseMockConfiguration);
	}

	public Object resolveSpyConfiguration(String doubleName, Field field) {
		return resolveDoubleConfiguration(
				doubleName,
				field,
				configurationParser::parseSpyConfiguration);
	}

	private Object resolveDoubleConfiguration(String doubleName, Field field, BiFunction<String, Annotation, Object> configurationCreator) {
		return configurationCreator.apply(
				doubleName,
				getAnnotation(field, configurationAnnotation));
	}
}
