package com.pchudzik.springmock.spock.configuration;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;

public class SpockDoubleConfigurationParser implements DoubleConfigurationParser<SpockDoubleConfiguration, SpockDouble> {
	@Override
	public SpockDoubleConfiguration parseMockConfiguration(String doubleName, SpockDouble configuration) {
		return parseDoubleConfiguration(doubleName, configuration);
	}

	@Override
	public SpockDoubleConfiguration parseSpyConfiguration(String doubleName, SpockDouble configuration) {
		return parseDoubleConfiguration(doubleName, configuration);
	}

	private SpockDoubleConfiguration parseDoubleConfiguration(String doubleName, SpockDouble spockDouble) {
		final SpockDoubleConfiguration.SpockDoubleConfigurationBuilder configurationBuilder = SpockDoubleConfiguration.builder();

		if (spockDouble != null) {
			configurationBuilder.defaultResponse(spockDouble.defaultResponse())
					.constructorArgs(spockDouble.constructorArguments())
					.stub(spockDouble.stub())
					.useObjenesis(spockDouble.useObjenesis())
					.implementation(spockDouble.mockImplementation())
					.global(spockDouble.global());
		}

		return configurationBuilder
				.name(doubleName)
				.build();
	}
}
