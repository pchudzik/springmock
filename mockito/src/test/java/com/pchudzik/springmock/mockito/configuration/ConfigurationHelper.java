package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleDefinitionRegistryFactory;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import org.mockito.MockSettings;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.assertFalse;

public class ConfigurationHelper {
	public static MockSettings mockSettingsMock() {
		return Mockito.spy(Mockito.withSettings());
	}

	public static DoubleSearch findDoublesInClass(Class<?> clazz) {
		return parseClass(clazz).doublesSearch();
	}

	public static void expectNoConfiguration(DoubleDefinition doubleDefinition) {
		final Optional<MockitoDoubleConfiguration> configuration = doubleDefinition.getConfiguration(MockitoDoubleConfiguration.class);
		assertFalse("no configuration", configuration.isPresent());
	}

	public static MockitoDoubleConfiguration getConfiguration(DoubleDefinition doubleDefinition) {
		return doubleDefinition
				.getConfiguration(MockitoDoubleConfiguration.class)
				.orElseThrow(() -> new IllegalStateException("Configuration is expected to exists"));
	}

	private static DoubleRegistry parseClass(Class<?> clazz) {
		return createMockitoDefinitionRegistryFactory().parse(clazz);
	}

	private static DoubleDefinitionRegistryFactory createMockitoDefinitionRegistryFactory() {
		return new DoubleDefinitionRegistryFactory(MockitoDouble.class, new MockitoDoubleConfigurationParser());
	}
}
