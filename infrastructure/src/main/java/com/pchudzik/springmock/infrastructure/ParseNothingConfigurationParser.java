package com.pchudzik.springmock.infrastructure;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import java.lang.reflect.Field;

public class ParseNothingConfigurationParser implements DoubleConfigurationParser<Object> {
	@Override
	public Object parseDoubleConfiguration(Field field) {
		return null;
	}
}
