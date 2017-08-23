package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.pchudzik.springmock.infrastructure.annotation.NoDoubleClassDefined.isDoubleClassDefinitionMissing;
import static java.util.Arrays.asList;

class AnnotationDetails {
	private final Function<DoubleConfigurationResolver, ConfigurationCreator> configurationResolver;
	private final ParamExtractor<? extends Annotation> annotationParamExtractor;

	private AnnotationDetails(Function<DoubleConfigurationResolver, ConfigurationCreator> configurationResolver, ParamExtractor<? extends Annotation> annotationParamExtractor) {
		this.configurationResolver = configurationResolver;
		this.annotationParamExtractor = annotationParamExtractor;
	}

	public static AnnotationDetails mock(AutowiredMock autowiredMock) {
		return new AnnotationDetails(
				doubleConfigurationResolver -> doubleConfigurationResolver::resolveMockConfiguration,
				new AutowiredMockDetailsExtractor(autowiredMock));
	}

	public static AnnotationDetails spy(AutowiredSpy autowiredSpy) {
		return new AnnotationDetails(
				configurationResolver -> configurationResolver::resolveSpyConfiguration,
				new AutowiredSpyDetailsExtractor(autowiredSpy));
	}

	public final Optional<String> getName() {
		final String name = annotationParamExtractor.getNameParam();
		if (StringUtils.isBlank(name)) {
			return Optional.empty();
		}

		return Optional.of(name);
	}

	public final Collection<String> getAlias() {
		final String[] alias = annotationParamExtractor.getAliasParam();

		if (ArrayUtils.isEmpty(alias)) {
			return Collections.emptyList();
		}

		return new HashSet<>(asList(alias));
	}

	public final Optional<Class<?>> getDoubleClass() {
		final Class<?> doubleClass = annotationParamExtractor.getDoubleClassParam();
		if (isDoubleClassDefinitionMissing(doubleClass)) {
			return Optional.empty();
		}

		return Optional.of(doubleClass);
	}

	public BiFunction<String, Field, Object> resolveConfiguration(DoubleConfigurationResolver resolver) {
		return configurationResolver.apply(resolver);
	}

	private interface ConfigurationCreator extends BiFunction<String, Field, Object> {
	}

	private interface ParamExtractor<A extends Annotation> {
		String getNameParam();

		String[] getAliasParam();

		Class<?> getDoubleClassParam();
	}

	private static class AutowiredSpyDetailsExtractor implements ParamExtractor<AutowiredSpy> {
		private final AutowiredSpy annotation;

		private AutowiredSpyDetailsExtractor(AutowiredSpy annotation) {
			this.annotation = annotation;
		}

		@Override
		public String[] getAliasParam() {
			return annotation.alias();
		}

		@Override
		public Class<?> getDoubleClassParam() {
			return annotation.doubleClass();
		}

		@Override
		public String getNameParam() {
			return annotation.name();
		}
	}

	private static class AutowiredMockDetailsExtractor implements ParamExtractor<AutowiredMock> {
		private final AutowiredMock annotation;

		protected AutowiredMockDetailsExtractor(AutowiredMock annotation) {
			this.annotation = annotation;
		}

		@Override
		public String[] getAliasParam() {
			return annotation.alias();
		}

		@Override
		public Class<?> getDoubleClassParam() {
			return annotation.doubleClass();
		}

		@Override
		public String getNameParam() {
			return annotation.name();
		}
	}
}
