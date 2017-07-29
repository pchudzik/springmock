package com.pchudzik.springmock.mockito.configuration;

import org.mockito.MockSettings;
import org.mockito.mock.SerializableMode;

import java.util.Objects;

class DoubleSerializableModeSettings implements SettingsDecorator {
	private final SerializableMode serializableMode;

	public DoubleSerializableModeSettings(SerializableMode serializableMode) {
		this.serializableMode = serializableMode;
	}

	@Override
	public MockSettings apply(MockSettings mockSettings) {
		return mockSettings.serializable(serializableMode);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final DoubleSerializableModeSettings that = (DoubleSerializableModeSettings) o;
		return serializableMode == that.serializableMode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(serializableMode);
	}
}
