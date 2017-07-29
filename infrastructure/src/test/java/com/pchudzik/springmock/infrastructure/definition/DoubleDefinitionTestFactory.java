package com.pchudzik.springmock.infrastructure.definition;

import java.util.concurrent.atomic.AtomicLong;

public class DoubleDefinitionTestFactory {
	private static final AtomicLong idGenerator = new AtomicLong();

	private DoubleDefinitionTestFactory(){}

	public static DoubleDefinition doubleDefinition(Class<?> doubleClass) {
		return doubleDefinition(doubleClass, "double " + idGenerator.incrementAndGet());
	}

	public static DoubleDefinition doubleDefinition(Class<?> doubleClass, String name) {
		return DoubleDefinition.builder()
				.name(name)
				.doubleClass(doubleClass)
				.build();
	}
}
