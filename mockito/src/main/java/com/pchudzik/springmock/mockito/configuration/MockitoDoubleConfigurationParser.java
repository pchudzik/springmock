package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleNameResolver;
import com.pchudzik.springmock.mockito.configuration.MockitoDoubleConfiguration.MockitoDoubleConfigurationBuilder;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Optional;

public class MockitoDoubleConfigurationParser implements DoubleConfigurationParser<MockitoDoubleConfiguration> {
	@Override
	public MockitoDoubleConfiguration parseDoubleConfiguration(Field field) {
		final MockitoDoubleConfigurationBuilder configurationBuilder = MockitoDoubleConfiguration.builder();

		configureDefaultAnswerForSpy(field, configurationBuilder);

		getAnnotation(field, MockitoDouble.class)
				.ifPresent(mockitoDouble -> configurationBuilder
						.answer(mockitoDouble.defaultAnswer())
						.resetMode(mockitoDouble.resetMode())
						.extraInterfaces(mockitoDouble.extraInterfaces())
						.serializableMode(mockitoDouble.serializableMode())
						.verboseLogging(mockitoDouble.verbose())
						.stubOnly(mockitoDouble.stub())
						.invocationListeners(mockitoDouble.invocationListeners())
				);
		configurationBuilder.name(DoubleNameResolver.resolveDoubleName(field));

		return configurationBuilder.build();
	}

	/**
	 * When working with mocks CallRealMethods is default answer. Otherwise method calls will not be passed to object
	 *
	 * @param field
	 * @param configurationBuilder
	 */
	private void configureDefaultAnswerForSpy(Field field, MockitoDoubleConfigurationBuilder configurationBuilder) {
		getAnnotation(field, AutowiredSpy.class).ifPresent(spy -> configurationBuilder.answer(CallsRealMethods.class));
	}

	private <A extends Annotation> Optional<A> getAnnotation(AnnotatedElement field, Class<A> annotation) {
		return Optional.ofNullable(AnnotationUtils.findAnnotation(field, annotation));
	}
}
