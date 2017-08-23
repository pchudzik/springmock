package com.pchudzik.springmock.infrastructure.definition.registry;

import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.pchudzik.springmock.infrastructure.definition.registry.IsAutowiredDoublePredicate.isAutowiredMock;
import static com.pchudzik.springmock.infrastructure.definition.registry.IsAutowiredDoublePredicate.isAutowiredSpy;
import static java.util.stream.Collectors.toSet;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;
import static org.springframework.util.ReflectionUtils.doWithFields;

class AnnotatedElementsScanner {
	public Collection<Class<?>> findAnnotatedClasses(Class<?> rootClass) {
		return Stream
				.concat(
						Stream.of(rootClass),
						findDeclaredConfigurationClasses(rootClass))
				.collect(toSet());
	}

	public Collection<Field> findAnnotatedFields(Class<?> clazz) {
		return findAllFields(clazz)
				.filter(isAutowiredMock().or(isAutowiredSpy()))
				.collect(toSet());
	}

	private Stream<Field> findAllFields(Class<?> clazz) {
		final Set<Field> allFields = new HashSet<>();
		doWithFields(clazz, allFields::add);
		return allFields.stream();
	}

	private Stream<Class<?>> findDeclaredConfigurationClasses(Class<?> rootClass) {
		return Stream
				.of(rootClass.getDeclaredClasses())
				.filter(declaredClass -> getAnnotation(declaredClass, Configuration.class) != null);
	}
}
