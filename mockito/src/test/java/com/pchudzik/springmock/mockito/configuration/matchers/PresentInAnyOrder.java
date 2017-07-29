package com.pchudzik.springmock.mockito.configuration.matchers;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class PresentInAnyOrder extends ArgumentMatcher<Class[]> implements VarargMatcher {
	private final Set<Class<?>> classes;

	public PresentInAnyOrder(Class<?> ... classes) {
		this.classes = new HashSet<>(asList(classes));
	}

	@Override
	public boolean matches(Object argument) {
		final Class<?>[] actualClasses = (Class<?>[]) argument;
		return new HashSet<>(asList(actualClasses)).equals(classes);
	}
}
