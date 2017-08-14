package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import org.mockito.Mockito;

class DoubleDefinitionTestFactory {
	public static DoubleRegistry parseClass(Class<?> clazz) {
		DoubleConfigurationParser configurationParser = Mockito.mock(DoubleConfigurationParser.class);
		return parseClass(clazz, configurationParser);
	}

	public static DoubleRegistry parseClass(Class<?> clazz, DoubleConfigurationParser configurationParser) {
		final DoubleDefinitionRegistryFactory definitionRegistryFactory = new DoubleDefinitionRegistryFactory(DoubleDefinitionTestConfiguration.class, configurationParser);
		return definitionRegistryFactory.parse(clazz);
	}
}
