package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.mockito.Mockito;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

class AutowiredSpyBuilder {
	private String name;
	private String[] alias;
	private Class<?> doubleClass;

	public AutowiredSpyBuilder() {
		AutowiredSpy spy = findAnnotation(AnnotationDefaults.class, AutowiredSpy.class);
		name = spy.name();
		alias = spy.alias();
		doubleClass = spy.doubleClass();
	}

	public AutowiredSpyBuilder name(String name) {
		this.name = name;
		return this;
	}

	public AutowiredSpyBuilder alias(String... alias) {
		this.alias = alias;
		return this;
	}

	public AutowiredSpyBuilder doubleClass(Class<?> doubleClass) {
		this.doubleClass = doubleClass;
		return this;
	}

	public AutowiredSpy build() {
		AutowiredSpy spy = Mockito.mock(AutowiredSpy.class);
		Mockito.when(spy.name()).thenReturn(name);
		Mockito.when(spy.alias()).thenReturn(alias);
		Mockito.when(spy.doubleClass()).thenAnswer(inv -> doubleClass);
		return spy;
	}

	@AutowiredSpy
	private static class AnnotationDefaults {
	}
}
