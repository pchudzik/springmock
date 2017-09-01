package com.pchudzik.springmock.infrastructure;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

public class ParseNothingConfigurationParser implements DoubleConfigurationParser<Object, Annotation> {

	@Override
	public Object parseMockConfiguration(String doubleName, @Nullable Annotation configuration) {
		return null;
	}

	@Override
	public Object parseSpyConfiguration(String doubleName, @Nullable Annotation configuration) {
		return null;
	}
}
