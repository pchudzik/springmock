package com.pchudzik.springmock.mockito.configuration;

import org.mockito.MockSettings;

interface SettingsDecorator {
	MockSettings apply(MockSettings mockSettings);
}
