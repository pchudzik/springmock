package com.pchudzik.springmock.mockito.configuration.matchers;

import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;
import org.mockito.listeners.InvocationListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

public class HasInvocationListener extends ArgumentMatcher<InvocationListener> implements VarargMatcher {
	private final Collection<Class<? extends InvocationListener>> expectedListenerClass;

	public static Matcher<? extends InvocationListener> hasInstancesOf(Class<? extends InvocationListener> ... expectedClasses) {
		return new HasInvocationListener(asList(expectedClasses));
	}

	private HasInvocationListener(Collection<Class<? extends InvocationListener>> expectedListenerClass) {
		this.expectedListenerClass = expectedListenerClass;
	}

	@Override
	public boolean matches(Object argument) {
		if(argument.getClass().isArray()) {
			final InvocationListener [] listeners = (InvocationListener[]) argument;
			return matches(asList(listeners));
		} else {
			final InvocationListener listener = (InvocationListener) argument;
			return matches(singleton(listener));
		}
	}

	private boolean matches(Collection<InvocationListener> listeners) {
		final Collection<Class> actualListenerClasses = listeners.stream()
				.map(Object::getClass)
				.collect(Collectors.toList());
		return Objects.equals(actualListenerClasses.size(), expectedListenerClass.size())
				&& Objects.equals(new HashSet<>(actualListenerClasses), new HashSet<>(expectedListenerClass));
	}
}
