package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.MockDefinition;
import com.pchudzik.springmock.infrastructure.definition.SpyDefinition;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;
import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * Parses @{@link AutowiredMock} and @{@link AutowiredSpy} and creates double definitions stored in
 * the registry
 */
public class DoubleDefinitionRegistryFactory {
	public DoubleRegistry parse(Class<?> clazz) {
		final Set<MockDefinition> mocks = new HashSet<>();
		final Set<SpyDefinition> spies = new HashSet<>();

		doWithFields(clazz, (Field field) -> {
			extractMockDefinition(field).ifPresent(mocks::add);
			extractSpyDefinition(field).ifPresent(spies::add);
		});

		return new DoubleRegistry(mocks, spies);
	}

	protected MockDefinition createMockDefinition(Field field, AutowiredMock autowiredMock) {
		return new MockDefinition(
				field.getType(),
				generateDoubleName(field, autowiredMock.name()),
				asList(autowiredMock.alias()));
	}

	protected SpyDefinition createSpyDefinition(Field field, AutowiredSpy autowiredSpy) {
		return new SpyDefinition(
				field.getType(),
				generateDoubleName(field, autowiredSpy.name()),
				asList(autowiredSpy.alias()));
	}

	private Optional<MockDefinition> extractMockDefinition(Field field) {
		return Optional
				.ofNullable(getAnnotation(field, AutowiredMock.class))
				.map(autowiredMock -> createMockDefinition(field, autowiredMock));
	}

	private Optional<SpyDefinition> extractSpyDefinition(Field field) {
		return Optional
				.ofNullable(getAnnotation(field, AutowiredSpy.class))
				.map(autowiredSpy -> createSpyDefinition(field, autowiredSpy));
	}

	private String generateDoubleName(Field field, String maybeMockName) {
		if (isBlank(maybeMockName)) {
			return field.getName();
		}

		return maybeMockName;
	}
}
