package com.pchudzik.springmock.infrastructure.definition;

import java.util.Collection;

import static java.util.Collections.emptyList;

public class SpyDefinition extends DoubleDefinition {
	public SpyDefinition(Class<?> doubleClass, String name) {
		super(doubleClass, name, emptyList());
	}

	public SpyDefinition(Class<?> doubleClass, String name, Collection<String> aliases) {
		super(doubleClass, name, aliases);
	}
}
