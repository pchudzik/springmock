package com.pchudzik.springmock.infrastructure.definition.registry;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

class DoubleNameResolver {
	private static final String NO_FIELD = null;

	public String resolveDoubleName(AnnotationDetails details) {
		return resolveDoubleName(details, NO_FIELD);
	}

	public String resolveDoubleName(AnnotationDetails details, @Nullable String fieldName) {
		final Supplier<IllegalStateException> nameGenerationErrorSupplier = () -> new IllegalStateException("Can not generate double name");
		return Stream
				.of(
						details.getName(),
						Optional.ofNullable(fieldName),
						details.getDoubleClass().map(this::generateNameFromClass))
				.filter(Optional::isPresent)
				.map(maybeName -> maybeName.orElseThrow(nameGenerationErrorSupplier))
				.findFirst()
				.orElseThrow(nameGenerationErrorSupplier);
	}

	private String generateNameFromClass(Class<?> doubleClass) {
		final String className = doubleClass.getSimpleName();
		final String classFirstLetter = CharUtils.toString(className.charAt(0));
		final String restOfClassName =StringUtils.substring(doubleClass.getSimpleName(), 1);

		return classFirstLetter.toLowerCase() + restOfClassName;
	}
}
