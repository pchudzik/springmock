package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Predicate;

class IsAutowiredDoublePredicate implements Predicate<Field> {
	private final Class<? extends Annotation> annotationType;

	private IsAutowiredDoublePredicate(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
	}

	public static IsAutowiredDoublePredicate isAutowiredSpy() {
		return new IsAutowiredDoublePredicate(AutowiredSpy.class);
	}

	public static IsAutowiredDoublePredicate isAutowiredMock() {
		return new IsAutowiredDoublePredicate(AutowiredMock.class);
	}

	@Override
	public boolean test(Field field) {
		return AnnotationUtils.getAnnotation(field, annotationType) != null;
	}
}
