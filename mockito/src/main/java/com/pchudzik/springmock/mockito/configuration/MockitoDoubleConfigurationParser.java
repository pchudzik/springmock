package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.mockito.configuration.MockitoDoubleConfiguration.MockitoDoubleConfigurationBuilder;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

public class MockitoDoubleConfigurationParser implements DoubleConfigurationParser<MockitoDoubleConfiguration, MockitoDouble> {
	@Override
	public MockitoDoubleConfiguration parseMockConfiguration(String doubleName, MockitoDouble configuration) {
		final MockitoDoubleConfigurationBuilder builder = MockitoDoubleConfiguration.builder();

		if (configuration != null) {
			builder
					.answer(configuration.defaultAnswer())
					.resetMode(configuration.resetMode())
					.extraInterfaces(configuration.extraInterfaces())
					.serializableMode(configuration.serializableMode())
					.verboseLogging(configuration.verbose())
					.stubOnly(configuration.stub())
					.invocationListeners(configuration.invocationListeners());
		}

		return builder
				.name(doubleName)
				.build();
	}

	@Override
	public MockitoDoubleConfiguration parseSpyConfiguration(String doubleName, MockitoDouble configuration) {
		final MockitoDoubleConfigurationBuilder configurationBuilder = MockitoDoubleConfiguration.builder();

		configureDefaultAnswerForSpy(configurationBuilder);

		if (configuration != null) {
			configurationBuilder
					.answer(configuration.defaultAnswer())
					.resetMode(configuration.resetMode())
					.extraInterfaces(configuration.extraInterfaces())
					.serializableMode(configuration.serializableMode())
					.verboseLogging(configuration.verbose())
					.stubOnly(configuration.stub())
					.invocationListeners(configuration.invocationListeners());
		}

		configurationBuilder.name(doubleName);

		return configurationBuilder.build();
	}

	/**
	 * When working with mocks CallRealMethods is default answer. Otherwise method calls will not be passed to object
	 *
	 * @param configurationBuilder
	 */
	private void configureDefaultAnswerForSpy(MockitoDoubleConfigurationBuilder configurationBuilder) {
		configurationBuilder.answer(CallsRealMethods.class);
	}

	private <A extends Annotation> Optional<A> getAnnotation(AnnotatedElement field, Class<A> annotation) {
		return Optional.ofNullable(AnnotationUtils.findAnnotation(field, annotation));
	}
}
