package com.pchudzik.springmock.mockito.configuration;

import org.mockito.MockSettings;

import java.util.Collection;
import java.util.Objects;

class DoubleInterfacesMockSettings implements SettingsDecorator {
	private final Collection<Class<?>> extraInterfaces;

	public DoubleInterfacesMockSettings(Collection<Class<?>> extraInterfaces) {
		this.extraInterfaces = extraInterfaces;
	}

	@Override
	public MockSettings apply(MockSettings mockSettings) {
		return mockSettings.extraInterfaces(extraInterfaces.toArray(new Class[extraInterfaces.size()]));
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final DoubleInterfacesMockSettings that = (DoubleInterfacesMockSettings) o;
		return Objects.equals(this.extraInterfaces, that.extraInterfaces);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(extraInterfaces);
	}
}
