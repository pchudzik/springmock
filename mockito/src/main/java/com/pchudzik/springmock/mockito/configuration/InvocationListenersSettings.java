package com.pchudzik.springmock.mockito.configuration;

import org.mockito.MockSettings;
import org.mockito.listeners.InvocationListener;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

class InvocationListenersSettings implements SettingsDecorator {
	private final Collection<Class<? extends InvocationListener>> listenerClasses;

	public InvocationListenersSettings(Class<? extends InvocationListener>[] listenerClasses) {
		this.listenerClasses = asList(listenerClasses);
	}

	@Override
	public MockSettings apply(MockSettings mockSettings) {
		if(listenerClasses.isEmpty()) {
			return mockSettings;
		}

		return mockSettings.invocationListeners(createListeners());
	}

	private InvocationListener[] createListeners() {
		final List<InvocationListener> listeners = listenerClasses
				.stream()
				.map(BeanUtils::instantiateClass)
				.collect(toList());
		return listeners.toArray(new InvocationListener[listeners.size()]);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final InvocationListenersSettings that = (InvocationListenersSettings) o;
		return Objects.equals(listenerClasses, that.listenerClasses);
	}

	@Override
	public int hashCode() {
		return Objects.hash(listenerClasses);
	}
}
