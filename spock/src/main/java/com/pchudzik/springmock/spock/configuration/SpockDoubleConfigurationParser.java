package com.pchudzik.springmock.spock.configuration;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleNameResolver;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class SpockDoubleConfigurationParser implements DoubleConfigurationParser<SpockDoubleConfiguration> {
	@Override
	public SpockDoubleConfiguration parseDoubleConfiguration(Field field) {
		return Optional.ofNullable(findAnnotation(field, SpockDouble.class))
				.map(spockDouble -> SpockDoubleConfiguration.builder()
						.defaultResponse(spockDouble.defaultResponse())
						.constructorArgs(spockDouble.constructorArguments())
						.stub(spockDouble.stub())
						.useObjenesis(spockDouble.useObjenesis())
						.implementation(spockDouble.mockImplementation())
						.global(spockDouble.global()))
				.orElseGet(SpockDoubleConfiguration::builder)
				.name(DoubleNameResolver.resolveDoubleName(field))
				.build();
	}
}
