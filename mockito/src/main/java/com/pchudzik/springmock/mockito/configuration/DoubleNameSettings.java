package com.pchudzik.springmock.mockito.configuration;

import org.mockito.MockSettings;

import java.util.Objects;

class DoubleNameSettings implements SettingsDecorator {
	private final String name;

	public DoubleNameSettings(String name) {
		this.name = name;
	}

	@Override
	public MockSettings apply(MockSettings mockSettings) {
		return mockSettings.name(name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final DoubleNameSettings that = (DoubleNameSettings) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
