package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.mockito.Mockito;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

class AutowiredMockBuilder {
	private String name;
	private String[] alias;
	private Class<?> doubleClass;

	public AutowiredMockBuilder() {
		AutowiredMock mock = findAnnotation(AnnotationDefaults.class, AutowiredMock.class);
		name = mock.name();
		alias = mock.alias();
		doubleClass = mock.doubleClass();
	}

	public AutowiredMockBuilder name(String name) {
		this.name = name;
		return this;
	}

	public AutowiredMockBuilder alias(String... alias) {
		this.alias = alias;
		return this;
	}

	public AutowiredMockBuilder doubleClass(Class<?> doubleClass) {
		this.doubleClass = doubleClass;
		return this;
	}

	public AutowiredMock build() {
		AutowiredMock mock = Mockito.mock(AutowiredMock.class);
		Mockito.when(mock.name()).thenReturn(name);
		Mockito.when(mock.alias()).thenReturn(alias);
		Mockito.when(mock.doubleClass()).thenAnswer(inv -> doubleClass);
		return mock;
	}

	@AutowiredMock
	private static class AnnotationDefaults {
	}
}
