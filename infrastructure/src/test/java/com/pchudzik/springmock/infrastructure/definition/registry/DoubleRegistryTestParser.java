package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import org.mockito.Mockito;

class DoubleRegistryTestParser {
	public static DoubleRegistry parseClass(Class<?> clazz) {
		DoubleConfigurationParser configurationParser = Mockito.mock(DoubleConfigurationParser.class);
		return parseClass(clazz, configurationParser);
	}

	public static DoubleRegistry parseClass(Class<?> clazz, DoubleConfigurationParser configurationParser) {
		final DoubleRegistryParser definitionRegistryFactory = new DoubleRegistryParserFactory(DoubleDefinitionTestConfiguration.class, configurationParser).doubleRegistryParser();
		return definitionRegistryFactory.parse(clazz);
	}
}
