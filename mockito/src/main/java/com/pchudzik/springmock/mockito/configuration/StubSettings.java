package com.pchudzik.springmock.mockito.configuration;

import org.mockito.MockSettings;

import java.util.Objects;

class StubSettings implements SettingsDecorator {
	private final boolean stubOnly;

	public StubSettings(boolean stubOnly) {
		this.stubOnly = stubOnly;
	}

	@Override
	public MockSettings apply(MockSettings mockSettings) {
		return stubOnly
				? mockSettings.stubOnly()
				: mockSettings;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final StubSettings that = (StubSettings) o;
		return stubOnly == that.stubOnly;
	}

	@Override
	public int hashCode() {
		return Objects.hash(stubOnly);
	}
}
