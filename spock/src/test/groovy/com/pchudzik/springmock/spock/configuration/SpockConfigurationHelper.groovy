package com.pchudzik.springmock.spock.configuration

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleDefinitionRegistryFactory
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch

class SpockConfigurationHelper {
	static DoubleSearch findDoublesInClass(Class<?> testClass) {
		parseClass(testClass).doublesSearch()
	}

	static DoubleRegistry parseClass(Class<?> aClass) {
		configurationParser().parse(aClass)
	}

	static DoubleDefinitionRegistryFactory configurationParser() {
		new DoubleDefinitionRegistryFactory(SpockDouble.class, new SpockDoubleConfigurationParser())
	}

	static SpockDoubleConfiguration getConfig(DoubleDefinition definition) {
		definition.getConfiguration(SpockDoubleConfiguration)
	}
}
