package com.pchudzik.springmock.infrastructure;

import java.lang.annotation.Annotation;

public class ParseNothingConfigurationParser implements DoubleConfigurationParser<Object, Annotation> {

	@Override
	public Object parseMockConfiguration(String doubleName, Annotation configuration) {
		return null;
	}

	@Override
	public Object parseSpyConfiguration(String doubleName, Annotation configuration) {
		return null;
	}
}
