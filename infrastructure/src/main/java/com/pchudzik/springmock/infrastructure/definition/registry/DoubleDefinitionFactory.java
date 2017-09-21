package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getRepeatableAnnotations;

class DoubleDefinitionFactory<T extends Annotation> {
	private final DoubleNameResolver doubleNameResolver;
	private final DoubleClassResolver doubleClassResolver;
	private final DoubleConfigurationResolver doubleConfigurationResolver;

	private final Class<T> annotationType;
	private final Function<T, AnnotationDetails> detailsFactory;

	DoubleDefinitionFactory(
			Class<T> annotationType,
			Function<T, AnnotationDetails> detailsFactory,
			DoubleNameResolver doubleNameResolver,
			DoubleClassResolver doubleClassResolver,
			DoubleConfigurationResolver doubleConfigurationResolver) {
		this.doubleNameResolver = doubleNameResolver;
		this.doubleClassResolver = doubleClassResolver;
		this.doubleConfigurationResolver = doubleConfigurationResolver;
		this.annotationType = annotationType;
		this.detailsFactory = detailsFactory;
	}

	public Collection<DoubleDefinition> createDoubleDefinitions(Class<?> clazz) {
		return getRepeatableAnnotations(clazz, annotationType)
				.stream()
				.map(detailsFactory)
				.map(this::createDoubleDefinition)
				.collect(Collectors.toSet());
	}

	public DoubleDefinition createDoubleDefinition(Field field) {
		return createDoubleDefinition(
				detailsFactory.apply(getAnnotation(field, annotationType)),
				field);
	}

	private DoubleDefinition createDoubleDefinition(AnnotationDetails details, Field field) {
		final String doubleName = doubleNameResolver.resolveDoubleName(details, field.getName());
		final Class<?> doubleClass = doubleClassResolver.resolveDoubleClass(details, field.getType());

		return DoubleDefinition.builder()
				.name(doubleName)
				.aliases(resolveDoubleAliases(details, doubleName, field))
				.doubleClass(doubleClass)
				.doubleConfiguration(details
						.resolveConfiguration(doubleConfigurationResolver)
						.apply(doubleName, field))
				.build();
	}

	private DoubleDefinition createDoubleDefinition(AnnotationDetails details) {
		final String doubleName = doubleNameResolver.resolveDoubleName(details);
		final Class<?> doubleClass = doubleClassResolver.resolveDoubleClass(details);

		return DoubleDefinition.builder()
				.name(doubleName)
				.aliases(details.getAlias())
				.doubleClass(doubleClass)
				.doubleConfiguration(details
						.resolveConfiguration(doubleConfigurationResolver)
						.apply(doubleName, DoubleConfigurationResolver.NO_FIELD))
				.build();
	}

	private Collection<String> resolveDoubleAliases(AnnotationDetails details, String doubleName, Field field) {
		/*
		Include field name as an alias. This way injection is more flexible and allows to inject have different name for
		inject mock into field when mock name does not match registered double name. With this custom @Qualifier
		implementation can be avoided and doubles registered under different name are resolvable by field name
		see com.pchudzik.springmock.infrastructure.test.name.DoubleNameShouldBeUsedAsQualifier for sample test case
		 */

		final Set<String> aliases = new HashSet<>(details.getAlias());
		if(!Objects.equals(field.getName(), doubleName)) {
			aliases.add(field.getName());
		}
		return aliases;
	}
}
