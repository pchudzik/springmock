package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Optional;

public class DoubleNameResolver {
	public static String resolveDoubleName(Field field) {
		final AutowiredMock autowiredMockAnnotation = getAnnotation(field, AutowiredMock.class);
		final AutowiredSpy autowiredSpyAnnotation = getAnnotation(field, AutowiredSpy.class);

		Optional<String> resolvedName = Optional.empty();

		if(autowiredMockAnnotation != null) {
			resolvedName = getName(autowiredMockAnnotation.name());
		}

		if(autowiredSpyAnnotation != null) {
			resolvedName = getName(autowiredSpyAnnotation.name());
		}

		return resolvedName.orElseGet(field::getName);
	}

	private static <A extends Annotation> A getAnnotation(AnnotatedElement field, Class<A> annotation) {
		return AnnotationUtils.findAnnotation(field, annotation);
	}

	private static Optional<String> getName(String maybeName) {
		return StringUtils.isNotBlank(maybeName)
				? Optional.of(maybeName)
				: Optional.empty();
	}
}
