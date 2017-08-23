package com.pchudzik.springmock.infrastructure.definition.registry;

import javax.annotation.Nullable;
import java.util.Optional;

class DoubleClassResolver {
	private static final Class<?> NO_FIELD_TYPE = null;

	public Class<?> resolveDoubleClass(AnnotationDetails details) {
		return resolveDoubleClass(details, NO_FIELD_TYPE);
	}

	public Class<?> resolveDoubleClass(AnnotationDetails details, @Nullable Class<?> fieldType) {
		final Optional<Class<?>> declaredDoubleClass = details.getDoubleClass();

		if(!declaredDoubleClass.isPresent() && fieldType == null) {
			throw new IllegalArgumentException("Can not resolve double class");
		}

		return declaredDoubleClass.orElse(fieldType);
	}
}
