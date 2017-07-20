package com.pchudzik.springmock.infrastructure.definition;

import java.util.Collection;
import java.util.Collections;

public class MockDefinition extends DoubleDefinition {
	public MockDefinition(Class<?> doubleClass, String name) {
		this(doubleClass, name, Collections.emptyList());
	}

	public MockDefinition(Class<?> doubleClass, String name, Collection<String> aliases) {
		super(doubleClass, name, aliases);
	}
}
