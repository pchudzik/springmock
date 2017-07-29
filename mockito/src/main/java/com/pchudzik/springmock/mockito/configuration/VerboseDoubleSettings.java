package com.pchudzik.springmock.mockito.configuration;

import org.mockito.MockSettings;

import java.util.Objects;

class VerboseDoubleSettings implements SettingsDecorator {
	private final boolean verbose;

	public VerboseDoubleSettings(boolean verbose) {
		this.verbose = verbose;
	}

	@Override
	public MockSettings apply(MockSettings mockSettings) {
		return verbose
				? mockSettings.verboseLogging()
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

		final VerboseDoubleSettings that = (VerboseDoubleSettings) o;
		return verbose == that.verbose;
	}

	@Override
	public int hashCode() {
		return Objects.hash(verbose);
	}
}
