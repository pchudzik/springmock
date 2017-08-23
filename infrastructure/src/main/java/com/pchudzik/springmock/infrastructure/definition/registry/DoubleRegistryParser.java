package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static com.pchudzik.springmock.infrastructure.definition.registry.IsAutowiredDoublePredicate.isAutowiredMock;
import static com.pchudzik.springmock.infrastructure.definition.registry.IsAutowiredDoublePredicate.isAutowiredSpy;
import static java.util.stream.Collectors.toSet;

/**
 * Parses @{@link AutowiredMock} and @{@link AutowiredSpy} and creates double definitions stored in
 * the registry
 */
public class DoubleRegistryParser {
	private final AnnotatedElementsScanner annotatedElementsScanner;
	private final DoubleDefinitionFactory mockDefinitionFactory;
	private final DoubleDefinitionFactory spyDefinitionFactory;

	public DoubleRegistryParser(
			AnnotatedElementsScanner annotatedElementsScanner,
			DoubleDefinitionFactory mockDefinitionFactory,
			DoubleDefinitionFactory spyDefinitionFactory) {
		this.annotatedElementsScanner = annotatedElementsScanner;
		this.mockDefinitionFactory = mockDefinitionFactory;
		this.spyDefinitionFactory = spyDefinitionFactory;
	}

	public DoubleRegistry parse(Class<?> clazz) {
		final Collection<Class<?>> annotatedClasses = annotatedElementsScanner.findAnnotatedClasses(clazz);
		final Collection<Field> annotatedFields = annotatedClasses.stream()
				.map(annotatedElementsScanner::findAnnotatedFields)
				.flatMap(Collection::stream)
				.collect(toSet());

		return new DoubleRegistry(
				extractDoubleDefinitions(mockDefinitionFactory, annotatedClasses, annotatedFields.stream().filter(isAutowiredMock())),
				extractDoubleDefinitions(spyDefinitionFactory, annotatedClasses, annotatedFields.stream().filter(isAutowiredSpy())));
	}

	private Set<DoubleDefinition> extractDoubleDefinitions(DoubleDefinitionFactory doubleDefinitionFactory, Collection<Class<?>> annotatedClasses, Stream<Field> annotatedFields) {
		return Stream
				.concat(
						annotatedClasses.stream().map(doubleDefinitionFactory::createDoubleDefinitions).flatMap(Collection::stream),
						annotatedFields.map(doubleDefinitionFactory::createDoubleDefinition))
				.collect(toSet());
	}
}
